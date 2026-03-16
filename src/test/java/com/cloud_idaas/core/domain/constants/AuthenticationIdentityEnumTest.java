package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthenticationIdentityEnum 单元测试
 */
class AuthenticationIdentityEnumTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: HUMAN 应存在")
    void enumValue_Human_ShouldExist() {
        AuthenticationIdentityEnum human = AuthenticationIdentityEnum.HUMAN;

        assertNotNull(human);
        assertEquals("HUMAN", human.name());
    }

    @Test
    @DisplayName("枚举值: CLIENT 应存在")
    void enumValue_Client_ShouldExist() {
        AuthenticationIdentityEnum client = AuthenticationIdentityEnum.CLIENT;

        assertNotNull(client);
        assertEquals("CLIENT", client.name());
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回两个枚举值")
    void values_ShouldReturnTwoValues() {
        AuthenticationIdentityEnum[] values = AuthenticationIdentityEnum.values();

        assertEquals(2, values.length);
    }

    @Test
    @DisplayName("values: 应包含 HUMAN 和 CLIENT")
    void values_ShouldContainHumanAndClient() {
        AuthenticationIdentityEnum[] values = AuthenticationIdentityEnum.values();

        assertTrue(containsValue(values, AuthenticationIdentityEnum.HUMAN));
        assertTrue(containsValue(values, AuthenticationIdentityEnum.CLIENT));
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 'HUMAN' 应返回 HUMAN 枚举值")
    void valueOf_HumanString_ShouldReturnHumanEnum() {
        AuthenticationIdentityEnum human = AuthenticationIdentityEnum.valueOf("HUMAN");

        assertEquals(AuthenticationIdentityEnum.HUMAN, human);
    }

    @Test
    @DisplayName("valueOf: 'CLIENT' 应返回 CLIENT 枚举值")
    void valueOf_ClientString_ShouldReturnClientEnum() {
        AuthenticationIdentityEnum client = AuthenticationIdentityEnum.valueOf("CLIENT");

        assertEquals(AuthenticationIdentityEnum.CLIENT, client);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            AuthenticationIdentityEnum.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("valueOf: null 应抛出 NullPointerException")
    void valueOf_NullName_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            AuthenticationIdentityEnum.valueOf(null);
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: HUMAN 的序号应为 0")
    void ordinal_Human_ShouldBeZero() {
        assertEquals(0, AuthenticationIdentityEnum.HUMAN.ordinal());
    }

    @Test
    @DisplayName("ordinal: CLIENT 的序号应为 1")
    void ordinal_Client_ShouldBeOne() {
        assertEquals(1, AuthenticationIdentityEnum.CLIENT.ordinal());
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: HUMAN 的名称应为 'HUMAN'")
    void name_Human_ShouldBeHuman() {
        assertEquals("HUMAN", AuthenticationIdentityEnum.HUMAN.name());
    }

    @Test
    @DisplayName("name: CLIENT 的名称应为 'CLIENT'")
    void name_Client_ShouldBeClient() {
        assertEquals("CLIENT", AuthenticationIdentityEnum.CLIENT.name());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("HUMAN", AuthenticationIdentityEnum.HUMAN.toString());
        assertEquals("CLIENT", AuthenticationIdentityEnum.CLIENT.toString());
    }

    // ==================== switch 使用测试 ====================

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 HUMAN")
    void switchStatement_ShouldCorrectlyMatchHuman() {
        String result = getIdentityDescription(AuthenticationIdentityEnum.HUMAN);

        assertEquals("human", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 CLIENT")
    void switchStatement_ShouldCorrectlyMatchClient() {
        String result = getIdentityDescription(AuthenticationIdentityEnum.CLIENT);

        assertEquals("client", result);
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        AuthenticationIdentityEnum human1 = AuthenticationIdentityEnum.HUMAN;
        AuthenticationIdentityEnum human2 = AuthenticationIdentityEnum.HUMAN;

        assertEquals(human1, human2);
        assertSame(human1, human2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(AuthenticationIdentityEnum.HUMAN, AuthenticationIdentityEnum.CLIENT);
    }

    // ==================== 辅助方法 ====================

    private boolean containsValue(AuthenticationIdentityEnum[] values, AuthenticationIdentityEnum target) {
        for (AuthenticationIdentityEnum value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private String getIdentityDescription(AuthenticationIdentityEnum identity) {
        switch (identity) {
            case HUMAN:
                return "human";
            case CLIENT:
                return "client";
            default:
                return "unknown";
        }
    }
}
