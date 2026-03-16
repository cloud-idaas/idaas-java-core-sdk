package com.cloud_idaas.core.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RefreshResult 单元测试
 */
class RefreshResultTest {

    private static final String TEST_VALUE = "test-value";
    private static final Instant TEST_STALE_TIME = Instant.now().plusSeconds(300);
    private static final Instant TEST_PREFETCH_TIME = Instant.now().plusSeconds(200);

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 应正确创建 RefreshResult 实例")
    void constructor_ShouldCreateRefreshResultInstance() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotNull(result);
        assertEquals(TEST_VALUE, result.getValue());
        assertEquals(TEST_STALE_TIME, result.getStaleTime());
        assertEquals(TEST_PREFETCH_TIME, result.getPrefetchTime());
    }

    @Test
    @DisplayName("构造函数: 允许 value 为 null")
    void constructor_ShouldAllowNullValue() {
        RefreshResult<String> result = new RefreshResult<>(null, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNull(result.getValue());
        assertEquals(TEST_STALE_TIME, result.getStaleTime());
        assertEquals(TEST_PREFETCH_TIME, result.getPrefetchTime());
    }

    @Test
    @DisplayName("构造函数: 允许 staleTime 为 null")
    void constructor_ShouldAllowNullStaleTime() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, null, TEST_PREFETCH_TIME);

        assertEquals(TEST_VALUE, result.getValue());
        assertNull(result.getStaleTime());
        assertEquals(TEST_PREFETCH_TIME, result.getPrefetchTime());
    }

    @Test
    @DisplayName("构造函数: 允许 prefetchTime 为 null")
    void constructor_ShouldAllowNullPrefetchTime() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, null);

        assertEquals(TEST_VALUE, result.getValue());
        assertEquals(TEST_STALE_TIME, result.getStaleTime());
        assertNull(result.getPrefetchTime());
    }

    @Test
    @DisplayName("构造函数: 允许所有字段为 null")
    void constructor_ShouldAllowAllNullFields() {
        RefreshResult<String> result = new RefreshResult<>(null, null, null);

        assertNull(result.getValue());
        assertNull(result.getStaleTime());
        assertNull(result.getPrefetchTime());
    }

    // ==================== Getter 测试 ====================

    @Test
    @DisplayName("getValue: 应返回正确的值")
    void getValue_ShouldReturnCorrectValue() {
        RefreshResult<Integer> result = new RefreshResult<>(42, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(42, result.getValue());
    }

    @Test
    @DisplayName("getStaleTime: 应返回正确的过期时间")
    void getStaleTime_ShouldReturnCorrectStaleTime() {
        Instant staleTime = Instant.parse("2025-01-01T00:00:00Z");
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, staleTime, TEST_PREFETCH_TIME);

        assertEquals(staleTime, result.getStaleTime());
    }

    @Test
    @DisplayName("getPrefetchTime: 应返回正确的预取时间")
    void getPrefetchTime_ShouldReturnCorrectPrefetchTime() {
        Instant prefetchTime = Instant.parse("2025-01-01T00:00:00Z");
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, prefetchTime);

        assertEquals(prefetchTime, result.getPrefetchTime());
    }

    // ==================== equals 测试 ====================

    @Test
    @DisplayName("equals: 相同对象应返回 true")
    void equals_SameObject_ShouldReturnTrue() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(result, result);
    }

    @Test
    @DisplayName("equals: 相同内容的对象应返回 true")
    void equals_EqualObjects_ShouldReturnTrue() {
        RefreshResult<String> result1 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("equals: 不同 value 应返回 false")
    void equals_DifferentValue_ShouldReturnFalse() {
        RefreshResult<String> result1 = new RefreshResult<>("value1", TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>("value2", TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("equals: 不同 staleTime 应返回 false")
    void equals_DifferentStaleTime_ShouldReturnFalse() {
        RefreshResult<String> result1 = new RefreshResult<>(TEST_VALUE, Instant.now(), TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>(TEST_VALUE, Instant.now().plusSeconds(100), TEST_PREFETCH_TIME);

        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("equals: 不同 prefetchTime 应返回 false")
    void equals_DifferentPrefetchTime_ShouldReturnFalse() {
        RefreshResult<String> result1 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, Instant.now());
        RefreshResult<String> result2 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, Instant.now().plusSeconds(100));

        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("equals: 与 null 比较应返回 false")
    void equals_NullComparison_ShouldReturnFalse() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotEquals(null, result);
    }

    @Test
    @DisplayName("equals: 与不同类型比较应返回 false")
    void equals_DifferentType_ShouldReturnFalse() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotEquals("string", result);
    }

    @Test
    @DisplayName("equals: null value 与非 null value 应返回 false")
    void equals_NullVsNonNullValue_ShouldReturnFalse() {
        RefreshResult<String> result1 = new RefreshResult<>(null, TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("equals: 两个 null value 应返回 true")
    void equals_BothNullValues_ShouldReturnTrue() {
        RefreshResult<String> result1 = new RefreshResult<>(null, TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>(null, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(result1, result2);
    }

    // ==================== hashCode 测试 ====================

    @Test
    @DisplayName("hashCode: 相同对象应返回相同 hashCode")
    void hashCode_SameObject_ShouldReturnSameHashCode() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(result.hashCode(), result.hashCode());
    }

    @Test
    @DisplayName("hashCode: 相同内容的对象应返回相同 hashCode")
    void hashCode_EqualObjects_ShouldReturnSameHashCode() {
        RefreshResult<String> result1 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    @DisplayName("hashCode: 不同内容的对象通常应返回不同 hashCode")
    void hashCode_DifferentObjects_ShouldUsuallyReturnDifferentHashCode() {
        RefreshResult<String> result1 = new RefreshResult<>("value1", TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<String> result2 = new RefreshResult<>("value2", TEST_STALE_TIME, TEST_PREFETCH_TIME);

        // 注意: 这不是严格要求的，但通常是期望的行为
        assertNotEquals(result1.hashCode(), result2.hashCode());
    }

    // ==================== toString 测试 ====================

    @Test
    @DisplayName("toString: 应包含类名和字段值")
    void toString_ShouldContainClassNameAndFieldValues() {
        RefreshResult<String> result = new RefreshResult<>(TEST_VALUE, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        String str = result.toString();

        assertTrue(str.contains("RefreshResult"));
        assertTrue(str.contains("value=" + TEST_VALUE));
        assertTrue(str.contains("staleTime="));
        assertTrue(str.contains("prefetchTime="));
    }

    @Test
    @DisplayName("toString: null 值应正确显示")
    void toString_WithNullValues_ShouldDisplayNull() {
        RefreshResult<String> result = new RefreshResult<>(null, null, null);

        String str = result.toString();

        assertTrue(str.contains("value=null"));
        assertTrue(str.contains("staleTime=null"));
        assertTrue(str.contains("prefetchTime=null"));
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("builder: 应创建正确的 RefreshResult 实例")
    void builder_ShouldCreateCorrectRefreshResult() {
        RefreshResult<String> result = RefreshResult.builder(TEST_VALUE)
                .staleTime(TEST_STALE_TIME)
                .prefetchTime(TEST_PREFETCH_TIME)
                .build();

        assertNotNull(result);
        assertEquals(TEST_VALUE, result.getValue());
        assertEquals(TEST_STALE_TIME, result.getStaleTime());
        assertEquals(TEST_PREFETCH_TIME, result.getPrefetchTime());
    }

    @Test
    @DisplayName("builder: 只设置 value 应创建实例")
    void builder_OnlyValue_ShouldCreateInstance() {
        RefreshResult<String> result = RefreshResult.builder(TEST_VALUE).build();

        assertNotNull(result);
        assertEquals(TEST_VALUE, result.getValue());
        assertNull(result.getStaleTime());
        assertNull(result.getPrefetchTime());
    }

    @Test
    @DisplayName("builder: 方法链应正常工作")
    void builder_MethodChaining_ShouldWork() {
        RefreshResult<String> result = RefreshResult.<String>builder(TEST_VALUE)
                .staleTime(TEST_STALE_TIME)
                .prefetchTime(TEST_PREFETCH_TIME)
                .build();

        assertEquals(TEST_VALUE, result.getValue());
        assertEquals(TEST_STALE_TIME, result.getStaleTime());
        assertEquals(TEST_PREFETCH_TIME, result.getPrefetchTime());
    }

    @Test
    @DisplayName("builder: 多次设置同一字段应使用最后一次的值")
    void builder_MultipleSetSameField_ShouldUseLastValue() {
        Instant staleTime1 = Instant.now();
        Instant staleTime2 = Instant.now().plusSeconds(100);

        RefreshResult<String> result = RefreshResult.<String>builder(TEST_VALUE)
                .staleTime(staleTime1)
                .staleTime(staleTime2)
                .build();

        assertEquals(staleTime2, result.getStaleTime());
    }

    // ==================== 泛型测试 ====================

    @Test
    @DisplayName("泛型: 应支持 Integer 类型")
    void generics_ShouldSupportIntegerType() {
        RefreshResult<Integer> result = new RefreshResult<>(123, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(123, result.getValue());
    }

    @Test
    @DisplayName("泛型: 应支持自定义对象类型")
    void generics_ShouldSupportCustomObjectType() {
        CustomObject customObj = new CustomObject("test", 42);
        RefreshResult<CustomObject> result = new RefreshResult<>(customObj, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertEquals(customObj, result.getValue());
    }

    @Test
    @DisplayName("泛型: 不同类型的 RefreshResult 不应相等")
    void generics_DifferentTypes_ShouldNotBeEqual() {
        RefreshResult<String> stringResult = new RefreshResult<>("42", TEST_STALE_TIME, TEST_PREFETCH_TIME);
        RefreshResult<Integer> intResult = new RefreshResult<>(42, TEST_STALE_TIME, TEST_PREFETCH_TIME);

        assertNotEquals(stringResult, intResult);
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
