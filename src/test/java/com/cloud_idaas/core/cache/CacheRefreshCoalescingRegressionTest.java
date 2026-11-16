package com.cloud_idaas.core.cache;

import com.cloud_idaas.core.exception.CacheException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/** Captures the cache version when asynchronous work is submitted, not when it runs. */
class CacheRefreshCoalescingRegressionTest {
    private static final Instant NOW = Instant.parse("2026-11-13T00:00:00Z");

    private static RefreshResult<String> result(String value, boolean expired) {
        return RefreshResult.builder(value).expiresAt(NOW.plusSeconds(expired ? -1 : 3600))
                .staleTime(NOW.plusSeconds(3600)).prefetchTime(NOW.plusSeconds(500)).build();
    }

    @Test
    void queuedPrefetchesShareTheFreshResultEvenIfItIsStillPrefetchDue() {
        MutableClock clock = new MutableClock();
        List<Runnable> jobs = new ArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() ->
                result(calls.incrementAndGet() == 1 ? "initial" : "updated", false))
                .clock(clock).prefetchStrategy(jobs::add).build();
        assertEquals("initial", cache.get());
        clock.now = NOW.plusSeconds(1500);
        assertEquals("initial", cache.get());
        assertEquals("initial", cache.get());
        assertEquals(2, jobs.size());
        jobs.get(0).run();
        jobs.get(1).run();
        assertEquals(2, calls.get()); // Initial fetch plus one shared refresh.
        assertEquals("updated", cache.get());
    }

    @Test
    void changedButExpiredResultsDoNotSuppressAnotherRefresh() {
        MutableClock clock = new MutableClock();
        List<Runnable> jobs = new ArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            return result(n == 1 ? "initial" : "expired", n > 1);
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT)
                .prefetchStrategy(jobs::add).build();
        cache.get();
        clock.now = NOW.plusSeconds(1500);
        cache.get();
        cache.get();
        assertEquals(2, jobs.size());
        jobs.get(0).run();
        jobs.get(1).run();
        assertEquals(3, calls.get());
        assertEquals("initial", cache.get()); // Invalid prefetch results never replace the still-valid value.
        clock.now = NOW.plusSeconds(10000);
        assertThrows(CacheException.class, cache::get);
    }

    @Test
    void failedRefreshDoesNotConsumeTheQueuedRetry() {
        MutableClock clock = new MutableClock();
        List<Runnable> jobs = new ArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            if (n == 2) throw new IllegalStateException("synthetic endpoint failure");
            return result(n == 1 ? "initial" : "retried", false);
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT)
                .prefetchStrategy(jobs::add).build();
        cache.get();
        clock.now = NOW.plusSeconds(1500);
        cache.get();
        cache.get();
        assertEquals(2, jobs.size());
        assertDoesNotThrow(jobs.get(0)::run);
        jobs.get(1).run();
        assertEquals("retried", cache.get());
        assertEquals(3, calls.get());
    }

    @Test
    void twentyMinuteTokenKeepsItsExistingRefreshAndFailureTiming() {
        AtomicReference<Instant> now = new AtomicReference<>(NOW);
        Clock clock = new Clock() {
            public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
            public Clock withZone(java.time.ZoneId zone) { return this; }
            public Instant instant() { return now.get(); }
        };
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return RefreshResult.builder("valid").expiresAt(NOW.plusSeconds(1200))
                    .staleTime(NOW.plusSeconds(960)).prefetchTime(NOW.plusSeconds(800)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("valid", cache.get());
        now.set(NOW.plusSeconds(950));
        assertEquals("valid", cache.get());
        assertEquals(1, calls.get());
        now.set(NOW.plusSeconds(1200));
        assertThrows(CacheException.class, cache::get);
    }
    @Test void fetchedPrefetchDueValueDoesNotRefetchUntilStaleAndFailureStillRetries() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            if (n > 2) throw new IllegalStateException("synthetic failure");
            return RefreshResult.builder("value-" + n).expiresAt(NOW.plusSeconds(3600))
                    .staleTime(NOW.plusSeconds(3600)).prefetchTime(NOW.minusSeconds(1000)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        for (int i = 0; i < 100; i++) assertEquals("value-1", cache.get());
        assertEquals(1, calls.get());
        clock.now = NOW.plusSeconds(3600);
        assertThrows(CacheException.class, cache::get); // A stale/expired value always refreshes despite the marker.
        assertEquals(2, calls.get());
        assertThrows(CacheException.class, cache::get);
        assertEquals(3, calls.get());
        assertThrows(CacheException.class, cache::get);
        assertEquals(4, calls.get());
    }

    private static final class MutableClock extends Clock {
        private Instant now = NOW;
        public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
        public Clock withZone(java.time.ZoneId zone) { return this; }
        public Instant instant() { return now; }
    }

}
