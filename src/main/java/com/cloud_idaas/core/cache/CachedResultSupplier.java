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

    // A value fetched inside its own prefetch window must not immediately trigger another fetch.
    private volatile RefreshResult<T> prefetchHandledValue;

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
            refreshCache(localCachedValue);
            localCachedValue = cachedValue;
        }
        // Confirm
        if (localCachedValue == null) {
            throw new ConcurrentOperationException();
        }

        // Check if prefetching is needed
        if (!cacheIsStale(localCachedValue) && shouldInitiateCachePrefetch(localCachedValue)) {
            RefreshResult<T> observedValue = localCachedValue;
            prefetchStrategy.prefetch(() -> {
                try {
                    refreshCache(observedValue);
                } catch (CacheException e) {
                    // Prefetch is opportunistic. Recheck after IO, including any concurrent replacement.
                    RefreshResult<T> current = cachedValue;
                    if (cacheIsStale(current) || (current.getExpiresAt() != null
                            && !clock.instant().isBefore(current.getExpiresAt()))) {
                        throw e;
                    }
                }
            });
            localCachedValue = cachedValue;
        }

        // Synchronous prefetch can consume the remaining token lifetime.
        if (staleValueBehavior == StaleValueBehavior.STRICT && localCachedValue.getExpiresAt() != null
                && !clock.instant().isBefore(localCachedValue.getExpiresAt())) {
            throw new CacheException(new IllegalStateException("Credential is expired"));
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
        return !clock.instant().isBefore(refreshResult.getStaleTime());
    }

    /**
     * Check if cache prefetching should be initiated
     *
     * @param refreshResult The cache result
     * @return True if prefetching should be initiated, otherwise false
     */
    private boolean shouldInitiateCachePrefetch(RefreshResult<T> refreshResult) {
        if (refreshResult == null || refreshResult == prefetchHandledValue) {
            return false;
        }
        Instant prefetchTime = refreshResult.getPrefetchTime();
        return prefetchTime != null && clock.instant().isAfter(prefetchTime);
    }

    /**
     * Refresh cache
     */
    private void refreshCache(RefreshResult<T> observedValue) {
        boolean lockAcquired = false;
        try {
            // Try to acquire the lock, waiting up to specified time
            lockAcquired = refreshLock.tryLock(BLOCKING_REFRESH_MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                // Failed to acquire lock due to timeout, just return current value
                LOGGER.error("Failed to acquire refresh lock");
                return;
            }

            // Coalesce callers that observed the same value, but never skip a stale refresh.
            if (cachedValue != observedValue && !cacheIsStale(cachedValue)) {
                return;
            }
            // Double-check if refresh is still needed
            if (!cacheIsStale(cachedValue) && !shouldInitiateCachePrefetch(cachedValue)) {
                return;
            }

            // Execute the actual refresh logic
            Instant fetchStartedAt = clock.instant();
            RefreshResult<T> refreshedValue = valueSupplier.get();
            
            // Apply jitter
            handleFetchedSuccess(refreshedValue, fetchStartedAt);
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
     * @param fetchStartedAt Conservative start of relative credential validity
     */
    private void handleFetchedSuccess(RefreshResult<T> refreshedValue, Instant fetchStartedAt) {
        // Apply jitter to staleTime and prefetchTime
        Instant staleTime = refreshedValue.getStaleTime();
        Instant prefetchTime = refreshedValue.getPrefetchTime();
        Instant expiry = refreshedValue.getExpiresAt();
        if (refreshedValue.getValue() instanceof com.cloud_idaas.core.credential.IDaaSTokenResponse) {
            com.cloud_idaas.core.credential.IDaaSTokenResponse token =
                    (com.cloud_idaas.core.credential.IDaaSTokenResponse) refreshedValue.getValue();
            if (token.getExpiresAt() > 0) {
                expiry = earlier(expiry, Instant.ofEpochSecond(token.getExpiresAt()));
            }
            if (token.getExpiresIn() > 0) {
                expiry = earlier(expiry, fetchStartedAt.plusSeconds(token.getExpiresIn()));
            }
        }

        // Reject an unusable STRICT result before replacing either cache publication field.
        // Prefetch callers can then retain a still-valid old value through the failure path.
        if (staleValueBehavior == StaleValueBehavior.STRICT
                && expiry != null && !clock.instant().isBefore(expiry)) {
            throw new IllegalStateException("Fetched credential is expired");
        }

        if (staleTime != null) {
            staleTime = jitterTime(staleTime);
        }

        if (prefetchTime != null) {
            prefetchTime = jitterTime(prefetchTime);
        }
        if (expiry != null) {
            if (staleTime == null || staleTime.isAfter(expiry)) {
                staleTime = expiry;
            }
            if (prefetchTime != null && prefetchTime.isAfter(staleTime)) {
                prefetchTime = staleTime;
            }
        }

        // Update cached value
        RefreshResult<T> fetched = new RefreshResult<>(refreshedValue.getValue(), staleTime, prefetchTime, expiry);
        prefetchHandledValue = prefetchTime != null && !clock.instant().isBefore(prefetchTime) ? fetched : null;
        cachedValue = fetched;
    }

    private static Instant earlier(Instant first, Instant second) {
        return first == null || second.isBefore(first) ? second : first;
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
