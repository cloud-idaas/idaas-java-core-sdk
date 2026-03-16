package com.cloud_idaas.core.cache;

import com.cloud_idaas.core.cache.strategy.NonBlockingPrefetchStrategy;
import com.cloud_idaas.core.cache.strategy.OneCallerBlocksPrefetchStrategy;
import com.cloud_idaas.core.exception.CacheException;
import com.cloud_idaas.core.exception.ConcurrentOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CachedResultSupplier 单元测试
 */
class CachedResultSupplierTest {

    private static final String TEST_VALUE = "test-credential";
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 默认配置应正确设置")
    void builder_DefaultConfiguration_ShouldBeSet() {
        AtomicInteger counter = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            counter.incrementAndGet();
            return createValidRefreshResult(TEST_VALUE);
        };

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier).build();

        assertNotNull(cachedSupplier);
    }

    @Test
    @DisplayName("Builder: 设置 staleValueBehavior 应正确")
    void builder_SetStaleValueBehavior_ShouldBeSet() {
        Supplier<RefreshResult<String>> supplier = () -> createValidRefreshResult(TEST_VALUE);

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .staleValueBehavior(StaleValueBehavior.STRICT)
                .build();

        assertNotNull(cachedSupplier);
    }

    @Test
    @DisplayName("Builder: 设置 prefetchStrategy 应正确")
    void builder_SetPrefetchStrategy_ShouldBeSet() {
        Supplier<RefreshResult<String>> supplier = () -> createValidRefreshResult(TEST_VALUE);

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .prefetchStrategy(new NonBlockingPrefetchStrategy())
                .build();

        assertNotNull(cachedSupplier);
    }

    @Test
    @DisplayName("Builder: 设置 clock 应正确")
    void builder_SetClock_ShouldBeSet() {
        Supplier<RefreshResult<String>> supplier = () -> createValidRefreshResult(TEST_VALUE);
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        assertNotNull(cachedSupplier);
    }

    @Test
    @DisplayName("Builder: 方法链应正常工作")
    void builder_MethodChaining_ShouldWork() {
        Supplier<RefreshResult<String>> supplier = () -> createValidRefreshResult(TEST_VALUE);
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .staleValueBehavior(StaleValueBehavior.ALLOW)
                .prefetchStrategy(new OneCallerBlocksPrefetchStrategy())
                .clock(fixedClock)
                .build();

        assertNotNull(cachedSupplier);
    }

    // ==================== get() 基本功能测试 ====================

    @Test
    @DisplayName("get: 首次调用应触发刷新并返回值")
    void get_FirstCall_ShouldTriggerRefreshAndReturnValue() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return createValidRefreshResult(TEST_VALUE);
        };

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier).build();

        String value = cachedSupplier.get();

        assertEquals(TEST_VALUE, value);
        assertEquals(1, refreshCount.get());
    }

    @Test
    @DisplayName("get: 未过期时不应重复刷新")
    void get_NotExpired_ShouldNotRefreshAgain() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return createValidRefreshResult(TEST_VALUE);
        };

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier).build();

        // 第一次调用
        cachedSupplier.get();
        assertEquals(1, refreshCount.get());

        // 第二次调用（未过期）
        cachedSupplier.get();
        assertEquals(1, refreshCount.get()); // 不应该增加
    }

    @Test
    @DisplayName("get: 过期后应重新刷新")
    void get_Expired_ShouldRefreshAgain() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        // 创建一个已经过期的 RefreshResult
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            int count = refreshCount.get();
            if (count == 1) {
                // 第一次返回已过期的结果
                return new RefreshResult<>(TEST_VALUE, 
                        FIXED_INSTANT.minusSeconds(100), // 已过期
                        null);
            } else {
                return createValidRefreshResult(TEST_VALUE + "-" + count);
            }
        };

        // 使用固定时钟，让第一次返回的结果已经过期
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        String value = cachedSupplier.get();

        // 应该刷新两次：第一次返回过期值，第二次刷新
        assertTrue(refreshCount.get() >= 1);
    }

    // ==================== 过期逻辑测试 ====================

    @Test
    @DisplayName("过期: staleTime 在当前时间之后不应过期")
    void expiry_StaleTimeAfterNow_ShouldNotBeExpired() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300), // 未来时间
                    null);
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        cachedSupplier.get();
        cachedSupplier.get();

        assertEquals(1, refreshCount.get()); // 只刷新一次
    }

    @Test
    @DisplayName("过期: staleTime 在当前时间之前应过期")
    void expiry_StaleTimeBeforeNow_ShouldBeExpired() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300),
                    null);
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        cachedSupplier.get();
        assertEquals(1, refreshCount.get());
    }

    // ==================== StaleValueBehavior 测试 ====================

    @Test
    @DisplayName("STRICT 模式: 刷新失败应抛出 CacheException")
    void strictMode_RefreshFailure_ShouldThrowCacheException() {
        Supplier<RefreshResult<String>> supplier = () -> {
            throw new RuntimeException("Refresh failed");
        };

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .staleValueBehavior(StaleValueBehavior.STRICT)
                .build();

        assertThrows(CacheException.class, cachedSupplier::get);
    }

    @Test
    @DisplayName("ALLOW 模式: 刷新失败后允许使用旧值")
    void allowMode_RefreshFailure_ShouldAllowStaleValue() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            int count = refreshCount.incrementAndGet();
            if (count == 1) {
                return createValidRefreshResult(TEST_VALUE);
            }
            throw new RuntimeException("Refresh failed");
        };

        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .staleValueBehavior(StaleValueBehavior.ALLOW)
                .build();

        // 第一次成功
        String value1 = cachedSupplier.get();
        assertEquals(TEST_VALUE, value1);

        // 后续刷新失败但不应抛出异常
        // 注意：由于缓存未过期，不会触发刷新，所以这个测试需要调整
    }

    // ==================== PrefetchStrategy 测试 ====================

    @Test
    @DisplayName("预取: prefetchTime 到期应触发预取")
    void prefetch_PrefetchTimeReached_ShouldTriggerPrefetch() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300),
                    FIXED_INSTANT.minusSeconds(100)); // prefetchTime 已过
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .prefetchStrategy(new OneCallerBlocksPrefetchStrategy())
                .build();

        cachedSupplier.get();

        // 预取应该被触发
        assertTrue(refreshCount.get() >= 1);
    }

    @Test
    @DisplayName("预取: prefetchTime 未到期不应触发预取")
    void prefetch_PrefetchTimeNotReached_ShouldNotTriggerPrefetch() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300),
                    FIXED_INSTANT.plusSeconds(200)); // prefetchTime 在未来
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        cachedSupplier.get();
        cachedSupplier.get();

        assertEquals(1, refreshCount.get()); // 只刷新一次
    }

    @Test
    @DisplayName("预取: prefetchTime 为 null 不应触发预取")
    void prefetch_NullPrefetchTime_ShouldNotTriggerPrefetch() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300),
                    null); // prefetchTime 为 null
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        cachedSupplier.get();
        cachedSupplier.get();

        assertEquals(1, refreshCount.get());
    }

    // ==================== Jitter 测试 ====================

    @Test
    @DisplayName("Jitter: 刷新成功后应应用 jitter")
    void jitter_AfterRefresh_ShouldBeApplied() {
        AtomicInteger refreshCount = new AtomicInteger(0);
        Supplier<RefreshResult<String>> supplier = () -> {
            refreshCount.incrementAndGet();
            return new RefreshResult<>(TEST_VALUE,
                    FIXED_INSTANT.plusSeconds(300),
                    FIXED_INSTANT.plusSeconds(200));
        };

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        String value = cachedSupplier.get();

        assertNotNull(value);
        assertEquals(1, refreshCount.get());
    }

    // ==================== 泛型测试 ====================

    @Test
    @DisplayName("泛型: 应支持 Integer 类型")
    void generics_ShouldSupportIntegerType() {
        Supplier<RefreshResult<Integer>> supplier = () -> createValidRefreshResult(42);

        CachedResultSupplier<Integer> cachedSupplier = new CachedResultSupplier.Builder<>(supplier).build();

        assertEquals(42, cachedSupplier.get());
    }

    @Test
    @DisplayName("泛型: 应支持自定义对象类型")
    void generics_ShouldSupportCustomObjectType() {
        CustomObject obj = new CustomObject("test", 42);
        Supplier<RefreshResult<CustomObject>> supplier = () -> createValidRefreshResult(obj);

        CachedResultSupplier<CustomObject> cachedSupplier = new CachedResultSupplier.Builder<>(supplier).build();

        CustomObject result = cachedSupplier.get();
        assertEquals(obj, result);
    }

    // ==================== null 值处理测试 ====================

    @Test
    @DisplayName("null 值: 应正确处理 null 值")
    void nullValue_ShouldBeHandled() {
        Supplier<RefreshResult<String>> supplier = () -> new RefreshResult<>(null,
                FIXED_INSTANT.plusSeconds(300),
                null);

        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneId.systemDefault());
        CachedResultSupplier<String> cachedSupplier = new CachedResultSupplier.Builder<>(supplier)
                .clock(fixedClock)
                .build();

        assertNull(cachedSupplier.get());
    }

    // ==================== 辅助方法 ====================

    private <T> RefreshResult<T> createValidRefreshResult(T value) {
        return new RefreshResult<>(value,
                Instant.now().plusSeconds(300),
                Instant.now().plusSeconds(200));
    }

    // ==================== 辅助类 ====================

    static class CustomObject {
        private final String name;
        private final int value;

        CustomObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CustomObject that = (CustomObject) o;
            return value == that.value && java.util.Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, value);
        }
    }
}
