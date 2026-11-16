package com.cloud_idaas.core.cache;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.exception.CacheException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/** Regression coverage for hard token expiry without opt-in capability extensions. */
class TokenExpiryRegressionTest {
    private static final Instant START = Instant.parse("2026-11-12T00:00:00Z");

    @Test void expiredSuccessfulPrefetchDoesNotReplaceValidValueAndCanRetry() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            int call = calls.incrementAndGet();
            if (call == 2) return RefreshResult.builder(token("expired", 1)).build();
            return RefreshResult.builder(token(call == 1 ? "valid" : "recovered", 10000))
                    .staleTime(START.plusSeconds(9000)).prefetchTime(START.plusSeconds(500)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("valid", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1500);
        assertEquals("valid", cache.get().getAccessToken());
        assertEquals("recovered", cache.get().getAccessToken());
        assertEquals(3, calls.get());
    }

    @Test void defaultPrefetchActuallyRefreshesBeforeStaleTime() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            return RefreshResult.builder(token(n == 1 ? "first" : "second", 10000))
                    .staleTime(clock.now.plusSeconds(3600)).prefetchTime(clock.now.plusSeconds(600)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1500);
        assertEquals("second", cache.get().getAccessToken());
        assertEquals("second", cache.get().getAccessToken());
        assertEquals(2, calls.get());
    }

    @Test void strictModeRefreshesAtExpiryDespiteLaterStaleTime() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            return result(token(n == 1 ? "first" : "second", n == 1 ? 1 : 60));
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1);
        assertEquals("second", cache.get().getAccessToken());
        assertEquals(2, calls.get());
    }

    @Test void strictModeRejectsAlreadyExpiredRefreshResult() {
        MutableClock clock = new MutableClock();
        IDaaSTokenResponse expired = token("expired", -1);
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> result(expired))
                .clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertThrows(CacheException.class, cache::get);
    }

    @Test void strictModeRechecksExpiryAfterSynchronousPrefetch() {
        MutableClock clock = new MutableClock();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() ->
                RefreshResult.builder(token("first", 3600)).staleTime(START.plusSeconds(3600))
                        .prefetchTime(START.plusSeconds(500)).build())
                .clock(clock).staleValueBehavior(StaleValueBehavior.STRICT)
                .prefetchStrategy(updater -> clock.now = START.plusSeconds(3600)).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1500);
        assertThrows(CacheException.class, cache::get);
    }

    @Test void strictModeDoesNotFallBackToExpiredTokenAfterRefreshFailure() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return result(token("first", 1));
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1);
        assertThrows(CacheException.class, cache::get);
    }

    @Test void synchronousPrefetchReturnsTheNewlyRefreshedValue() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            int n = calls.incrementAndGet();
            return RefreshResult.builder(token(n == 1 ? "first" : "second", n == 1 ? 3600 : 7200))
                    .staleTime(START.plusSeconds(7200)).prefetchTime(START.plusSeconds(500)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).prefetchStrategy(updater -> {
            clock.now = START.plusSeconds(3600);
            updater.run();
        }).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1500);
        assertEquals("second", cache.get().getAccessToken());
        assertEquals(2, calls.get());
        assertEquals("second", cache.get().getAccessToken());
        assertEquals(2, calls.get());
    }

    @Test void relativeExpiryStartsBeforeFetchingTheToken() {
        MutableClock clock = new MutableClock();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            IDaaSTokenResponse token = token("slow-response", 0);
            token.setExpiresAt(0);
            token.setExpiresIn(1);
            clock.now = START.plusSeconds(2);
            return result(token);
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertThrows(CacheException.class, cache::get);
    }

    @Test void explicitExpiryProtectsStringCredentialsWhenRefreshFails() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<String> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return RefreshResult.builder("identity-token").staleTime(START.plusSeconds(3600))
                    .expiresAt(START.plusSeconds(1)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("identity-token", cache.get());
        clock.now = START.plusSeconds(1);
        assertThrows(CacheException.class, cache::get);
    }

    @Test void legacyAllowModeRemainsAvailableForDirectSupplierUsers() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        IDaaSTokenResponse first = token("first", 1);
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return result(first);
        }).clock(clock).staleValueBehavior(StaleValueBehavior.ALLOW).build();
        assertSame(first, cache.get());
        clock.now = START.plusSeconds(2);
        assertSame(first, cache.get());
        assertEquals(2, calls.get());
    }

    @Test void expiresInProvidesBoundWhenAbsoluteExpiryIsMissing() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        IDaaSTokenResponse token = token("first", 0);
        token.setExpiresAt(0); token.setExpiresIn(1);
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return result(token);
        })
                .clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertSame(token, cache.get());
        // The bound is captured on fetch, not recomputed on each cached read.
        clock.now = START.plusMillis(999);
        assertSame(token, cache.get());
        clock.now = START.plusSeconds(1);
        assertThrows(CacheException.class, cache::get);
        assertEquals(2, calls.get());
    }

    @Test void strictModeRejectsExpiredTokenWhenRefreshLockTimesOut() throws Exception {
        MutableClock clock = new MutableClock();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> result(token("first", 1)))
                .clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("first", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1);
        java.lang.reflect.Field field = CachedResultSupplier.class.getDeclaredField("refreshLock");
        field.setAccessible(true);
        java.util.concurrent.locks.ReentrantLock lock = (java.util.concurrent.locks.ReentrantLock) field.get(cache);
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        lock.lock();
        try {
            java.util.concurrent.Future<?> call = executor.submit(cache::get);
            java.util.concurrent.ExecutionException failure = assertThrows(java.util.concurrent.ExecutionException.class,
                    () -> call.get(8, java.util.concurrent.TimeUnit.SECONDS));
            assertTrue(failure.getCause() instanceof CacheException);
        } finally {
            lock.unlock(); executor.shutdownNow();
        }
    }

    @Test void prefetchFailureKeepsValidCacheButFailsAfterExpiry() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
            return RefreshResult.builder(token("valid", 10000)).staleTime(START.plusSeconds(9000))
                    .prefetchTime(START.plusSeconds(500)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        assertEquals("valid", cache.get().getAccessToken());
        clock.now = START.plusSeconds(1500);
        assertEquals("valid", cache.get().getAccessToken());
        assertEquals(2, calls.get());
        clock.now = START.plusSeconds(10000);
        assertThrows(CacheException.class, cache::get);
    }

    @Test void failedPrefetchRechecksExpiryAfterNetworkDelay() {
        MutableClock clock = new MutableClock();
        AtomicInteger calls = new AtomicInteger();
        CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
            if (calls.incrementAndGet() > 1) {
                clock.now = START.plusSeconds(10000);
                throw new IllegalStateException("synthetic failure");
            }
            return RefreshResult.builder(token("valid", 10000)).staleTime(START.plusSeconds(9000))
                    .prefetchTime(START.plusSeconds(500)).build();
        }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
        cache.get();
        clock.now = START.plusSeconds(1500);
        assertThrows(CacheException.class, cache::get);
    }

    @Test void earliestOfExplicitAbsoluteAndRelativeExpiryIsEnforced() {
        for (int earliest = 0; earliest < 3; earliest++) {
            MutableClock clock = new MutableClock();
            AtomicInteger calls = new AtomicInteger();
            IDaaSTokenResponse value = token("bounded", earliest == 1 ? 60 : 3600);
            value.setExpiresIn(earliest == 2 ? 60 : 3600);
            final long explicit = earliest == 0 ? 60 : 7200;
            CachedResultSupplier<IDaaSTokenResponse> cache = new CachedResultSupplier.Builder<>(() -> {
                if (calls.incrementAndGet() > 1) throw new IllegalStateException("synthetic failure");
                return RefreshResult.builder(value).expiresAt(START.plusSeconds(explicit))
                        .staleTime(START.plusSeconds(8000)).build();
            }).clock(clock).staleValueBehavior(StaleValueBehavior.STRICT).build();
            assertSame(value, cache.get());
            clock.now = START.plusSeconds(59);
            assertSame(value, cache.get());
            clock.now = START.plusSeconds(60);
            assertThrows(CacheException.class, cache::get);
            assertEquals(2, calls.get());
        }
    }

    private static IDaaSTokenResponse token(String value, long seconds) {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setAccessToken(value); token.setExpiresAt(START.plusSeconds(seconds).getEpochSecond());
        return token;
    }

    private static RefreshResult<IDaaSTokenResponse> result(IDaaSTokenResponse token) {
        return RefreshResult.builder(token).staleTime(START.plusSeconds(300))
                .prefetchTime(START.plusSeconds(200)).build();
    }

    private static final class MutableClock extends Clock {
        private Instant now = START;
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }
}
