package com.cloud_idaas.core.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaleValueBehavior 单元测试
 */
class StaleValueBehaviorTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: STRICT 应存在")
    void enumValue_Strict_ShouldExist() {
        StaleValueBehavior strict = StaleValueBehavior.STRICT;

        assertNotNull(strict);
        assertEquals("STRICT", strict.name());
    }

    @Test
    @DisplayName("枚举值: ALLOW 应存在")
    void enumValue_Allow_ShouldExist() {
        StaleValueBehavior allow = StaleValueBehavior.ALLOW;

        assertNotNull(allow);
        assertEquals("ALLOW", allow.name());
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回两个枚举值")
    void values_ShouldReturnTwoValues() {
        StaleValueBehavior[] values = StaleValueBehavior.values();

        assertEquals(2, values.length);
    }

    @Test
    @DisplayName("values: 应包含 STRICT 和 ALLOW")
    void values_ShouldContainStrictAndAllow() {
        StaleValueBehavior[] values = StaleValueBehavior.values();

        assertTrue(containsValue(values, StaleValueBehavior.STRICT));
        assertTrue(containsValue(values, StaleValueBehavior.ALLOW));
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 'STRICT' 应返回 STRICT 枚举值")
    void valueOf_StrictString_ShouldReturnStrictEnum() {
        StaleValueBehavior strict = StaleValueBehavior.valueOf("STRICT");

        assertEquals(StaleValueBehavior.STRICT, strict);
    }

    @Test
    @DisplayName("valueOf: 'ALLOW' 应返回 ALLOW 枚举值")
    void valueOf_AllowString_ShouldReturnAllowEnum() {
        StaleValueBehavior allow = StaleValueBehavior.valueOf("ALLOW");

        assertEquals(StaleValueBehavior.ALLOW, allow);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            StaleValueBehavior.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("valueOf: null 应抛出 NullPointerException")
    void valueOf_NullName_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            StaleValueBehavior.valueOf(null);
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: STRICT 的序号应为 0")
    void ordinal_Strict_ShouldBeZero() {
        assertEquals(0, StaleValueBehavior.STRICT.ordinal());
    }

    @Test
    @DisplayName("ordinal: ALLOW 的序号应为 1")
    void ordinal_Allow_ShouldBeOne() {
        assertEquals(1, StaleValueBehavior.ALLOW.ordinal());
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: STRICT 的名称应为 'STRICT'")
    void name_Strict_ShouldBeStrict() {
        assertEquals("STRICT", StaleValueBehavior.STRICT.name());
    }

    @Test
    @DisplayName("name: ALLOW 的名称应为 'ALLOW'")
    void name_Allow_ShouldBeAllow() {
        assertEquals("ALLOW", StaleValueBehavior.ALLOW.name());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("STRICT", StaleValueBehavior.STRICT.toString());
        assertEquals("ALLOW", StaleValueBehavior.ALLOW.toString());
    }

    // ==================== switch 使用测试 ====================

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 STRICT")
    void switchStatement_ShouldCorrectlyMatchStrict() {
        String result = getBehaviorDescription(StaleValueBehavior.STRICT);

        assertEquals("strict", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 ALLOW")
    void switchStatement_ShouldCorrectlyMatchAllow() {
        String result = getBehaviorDescription(StaleValueBehavior.ALLOW);

        assertEquals("allow", result);
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        StaleValueBehavior strict1 = StaleValueBehavior.STRICT;
        StaleValueBehavior strict2 = StaleValueBehavior.STRICT;

        assertEquals(strict1, strict2);
        assertSame(strict1, strict2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(StaleValueBehavior.STRICT, StaleValueBehavior.ALLOW);
    }

    // ==================== 辅助方法 ====================

    private boolean containsValue(StaleValueBehavior[] values, StaleValueBehavior target) {
        for (StaleValueBehavior value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private String getBehaviorDescription(StaleValueBehavior behavior) {
        switch (behavior) {
            case STRICT:
                return "strict";
            case ALLOW:
                return "allow";
            default:
                return "unknown";
        }
    }
}
