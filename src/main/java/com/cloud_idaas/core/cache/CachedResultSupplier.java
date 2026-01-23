package com.cloud_idaas.core.cache;

import com.cloud_idaas.core.cache.strategy.OneCallerBlocksPrefetchStrategy;
import com.cloud_idaas.core.exception.CacheException;
import com.cloud_idaas.core.exception.ConcurrentOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * CachedResultSupplier is the core class of the caching mechanism, implementing the Supplier interface.
 * It provides a time-based caching mechanism, including expiration checking, prefetching strategy and other features.
 *
 * @param <T> The type of the cached value
 */
public class CachedResultSupplier<T> implements Supplier<T> {

    // Default maximum blocking refresh wait time
    private static final Duration BLOCKING_REFRESH_MAX_WAIT = Duration.ofSeconds(5);

    // Jitter random number generator
    private static final Random JITTER = new Random();

    private static final Duration JITTER_START = Duration.ofMinutes(5);

    private static final Duration JITTER_RANGE = Duration.ofMinutes(5);

    // Current cached value
    private volatile RefreshResult<T> cachedValue;

    // Value supplier, used to retrieve new values
    private final Supplier<RefreshResult<T>> valueSupplier;

    // Prefetch strategy
    private final PrefetchStrategy prefetchStrategy;

    // Clock source, pluggable for easier testing
    private final Clock clock;

    // Refresh lock, used to prevent frequent refreshing
    private final ReentrantLock refreshLock = new ReentrantLock();

    // Stale value behavior strategy
    private final StaleValueBehavior staleValueBehavior;

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedResultSupplier.class);

    /**
     * Private constructor, can only create instance via Builder
     */
    private CachedResultSupplier(Builder<T> builder) {
        this.valueSupplier = builder.valueSupplier;
        this.prefetchStrategy = builder.prefetchStrategy;
        this.clock = builder.clock;
        this.staleValueBehavior = builder.staleValueBehavior;
    }

    /**
     * Get cached value
     *
     * @return The cached value
     */
    @Override
    public T get() {
        RefreshResult<T> localCachedValue = cachedValue;

        // Check if cache has expired
        if (cacheIsStale(localCachedValue)) {
            refreshCache();
            localCachedValue = cachedValue;
        }
        // Confirm
        if (localCachedValue == null) {
            throw new ConcurrentOperationException();
        }

        // Check if prefetching is needed
        if (shouldInitiateCachePrefetch(localCachedValue)) {
            prefetchStrategy.prefetch(this::refreshCache);
        }

        return localCachedValue.getValue();
    }

    /**
     * Check if cache has expired
     *
     * @param refreshResult The cache result
     * @return True if expired, otherwise false
     */
    private boolean cacheIsStale(RefreshResult<T> refreshResult) {
        if (refreshResult == null) {
            return true;
        }
        return clock.instant().isAfter(refreshResult.getStaleTime());
    }

    /**
     * Check if cache prefetching should be initiated
     *
     * @param refreshResult The cache result
     * @return True if prefetching should be initiated, otherwise false
     */
    private boolean shouldInitiateCachePrefetch(RefreshResult<T> refreshResult) {
        if (refreshResult == null) {
            return false;
        }
        Instant prefetchTime = refreshResult.getPrefetchTime();
        return prefetchTime != null && clock.instant().isAfter(prefetchTime);
    }

    /**
     * Refresh cache
     */
    private void refreshCache() {
        boolean lockAcquired = false;
        try {
            // Try to acquire the lock, waiting up to specified time
            lockAcquired = refreshLock.tryLock(BLOCKING_REFRESH_MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                // Failed to acquire lock due to timeout, just return current value
                LOGGER.error("Failed to acquire refresh lock");
                return;
            }

            // Double-check if refresh is still needed
            if (!cacheIsStale(cachedValue)) {
                return;
            }

            // Execute the actual refresh logic
            RefreshResult<T> refreshedValue = valueSupplier.get();
            
            // Apply jitter
            handleFetchedSuccess(refreshedValue);
        } catch (Exception e) {
            // Handle refresh failure
            handleFetchFailure(e);
        } finally {
            if (lockAcquired) {
                refreshLock.unlock();
            }
        }
    }

    /**
     * Handle the newly fetched value
     *
     * @param refreshedValue The newly fetched value
     */
    private void handleFetchedSuccess(RefreshResult<T> refreshedValue) {
        // Apply jitter to staleTime and prefetchTime
        Instant staleTime = refreshedValue.getStaleTime();
        Instant prefetchTime = refreshedValue.getPrefetchTime();

        if (staleTime != null) {
            staleTime = jitterTime(staleTime);
        }

        if (prefetchTime != null) {
            prefetchTime = jitterTime(prefetchTime);
        }

        // Update cached value
        cachedValue = new RefreshResult<>(refreshedValue.getValue(), staleTime, prefetchTime);
    }

    /**
     * Handle failure when fetching value
     *
     * @param exception Exception
     */
    private void handleFetchFailure(Exception exception) {
        switch (staleValueBehavior) {
            case STRICT:
                throw new CacheException(exception);
            case ALLOW:
                LOGGER.info("Failed to refresh cache, using the old value");
            default:
                LOGGER.info("Failed to refresh cache, using the old value");
                break;
        }
    }

    /**
     * Apply jitter to specified time
     *
     * @param time       Original time
     * @return Time with added jitter
     */
    private Instant jitterTime(Instant time) {
        long jitterMills = JITTER_RANGE.toMillis();
        if (jitterMills <= 0) {
            return time;
        }

        long jitterAmount = Math.abs(JITTER.nextLong() % jitterMills);
        return time.plus(JITTER_START).plusMillis(jitterAmount);
    }

    /**
     * Builder pattern to construct CachedResultSupplier instances
     *
     * @param <T> The type of the cached value
     */
    public static class Builder<T> {
        private final Supplier<RefreshResult<T>> valueSupplier;
        private PrefetchStrategy prefetchStrategy = new OneCallerBlocksPrefetchStrategy();
        private Clock clock = Clock.systemUTC();
        private StaleValueBehavior staleValueBehavior = StaleValueBehavior.ALLOW;

        /**
         * Constructor for Builder instance
         *
         * @param valueSupplier Value supplier
         */
        public Builder(Supplier<RefreshResult<T>> valueSupplier) {
            this.valueSupplier = valueSupplier;
        }

        /**
         * Set stale value behavior strategy
         *
         * @param staleValueBehavior Stale value behavior strategy
         * @return Builder instance
         */
        public Builder<T> staleValueBehavior(StaleValueBehavior staleValueBehavior) {
            this.staleValueBehavior = staleValueBehavior;
            return this;
        }

        /**
         * Set prefetch strategy
         *
         * @param prefetchStrategy Prefetch strategy
         * @return Builder instance
         */
        public Builder<T> prefetchStrategy(PrefetchStrategy prefetchStrategy) {
            this.prefetchStrategy = prefetchStrategy;
            return this;
        }

        /**
         * Set clock source
         *
         * @param clock Clock source
         * @return Builder instance
         */
        public Builder<T> clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Build CachedResultSupplier instance
         *
         * @return CachedResultSupplier instance
         */
        public CachedResultSupplier<T> build() {
            return new CachedResultSupplier<>(this);
        }
    }
}