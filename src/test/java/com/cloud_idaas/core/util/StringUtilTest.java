package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StringUtil 单元测试
 */
class StringUtilTest {

    @ParameterizedTest
    @CsvSource({
            "hello, hello, true",
            "hello, world, false",
            "hello, HELLO, false",
            "null, null, true",
            "null, hello, false",
            "hello, null, false"
    })
    @DisplayName("equals: 比较两个字符串是否相等")
    void equals_ShouldCompareStringsCorrectly(String str1, String str2, boolean expected) {
        // 处理 null 字符串参数
        String actualStr1 = "null".equals(str1) ? null : str1;
        String actualStr2 = "null".equals(str2) ? null : str2;

        boolean result = StringUtil.equals(actualStr1, actualStr2);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("equals: 两个 null 字符串应相等")
    void equals_BothNull_ShouldReturnTrue() {
        assertTrue(StringUtil.equals(null, null));
    }

    @Test
    @DisplayName("equals: 一个 null 一个非 null 应不相等")
    void equals_OneNullOneNotNull_ShouldReturnFalse() {
        assertFalse(StringUtil.equals(null, "hello"));
        assertFalse(StringUtil.equals("hello", null));
    }

    @Test
    @DisplayName("继承自 StringUtils 的方法应可用")
    void inheritedMethods_ShouldWork() {
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank("   "));
        assertTrue(StringUtil.isBlank(null));
        assertFalse(StringUtil.isBlank("hello"));

        assertTrue(StringUtil.isNotBlank("hello"));
        assertFalse(StringUtil.isNotBlank(""));

        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("hello"));

        assertEquals("hello", StringUtil.trim("  hello  "));
    }
}
