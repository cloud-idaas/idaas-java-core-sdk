package com.cloud_idaas.core.cache;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Result class encapsulating the cache value and related expiration time and prefetch time information
 *
 * @param <T> Type of the cached value
 */
public final class RefreshResult<T> {

    private final T value;
    private final Instant staleTime;
    private final Instant prefetchTime;

    /**
     * Constructs a new RefreshResult instance
     *
     * @param value        The actual cached value
     * @param staleTime    Expiration time, after which all threads will block waiting for update
     * @param prefetchTime Prefetch time, after which prefetch operation will be triggered
     */
    public RefreshResult(T value, Instant staleTime, Instant prefetchTime) {
        this.value = value;
        this.staleTime = staleTime;
        this.prefetchTime = prefetchTime;
    }

    /**
     * Gets the actual cached value
     *
     * @return Cached value
     */
    public T getValue() {
        return value;
    }

    /**
     * Gets the expiration time
     *
     * @return Expiration time
     */
    public Instant getStaleTime() {
        return staleTime;
    }

    /**
     * Gets the prefetch time
     *
     * @return Prefetch time
     */
    public Instant getPrefetchTime() {
        return prefetchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshResult<?> that = (RefreshResult<?>) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(staleTime, that.staleTime) &&
                Objects.equals(prefetchTime, that.prefetchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, staleTime, prefetchTime);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RefreshResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("staleTime=" + staleTime)
                .add("prefetchTime=" + prefetchTime)
                .toString();
    }

    public static <T> RefreshResultBuilder<T> builder(T value) {
        return new RefreshResultBuilder<>(value);
    }

    public static final class RefreshResultBuilder<T> {
        private final T value;
        private Instant staleTime;
        private Instant prefetchTime;

        private RefreshResultBuilder(T  value) {
            this.value = value;
        }

        public RefreshResultBuilder<T> staleTime(Instant staleTime) {
            this.staleTime = staleTime;
            return this;
        }

        public RefreshResultBuilder<T> prefetchTime(Instant prefetchTime) {
            this.prefetchTime = prefetchTime;
            return this;
        }

        public RefreshResult<T> build() {return new RefreshResult<>(value, staleTime, prefetchTime);}
    }
}