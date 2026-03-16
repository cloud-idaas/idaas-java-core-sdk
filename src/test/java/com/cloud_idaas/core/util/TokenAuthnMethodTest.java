package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TokenAuthnMethod 单元测试
 */
class TokenAuthnMethodTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: NONE 应存在")
    void enumValue_None_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.NONE;

        assertNotNull(method);
        assertEquals("NONE", method.name());
    }

    @Test
    @DisplayName("枚举值: CLIENT_SECRET_POST 应存在")
    void enumValue_ClientSecretPost_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.CLIENT_SECRET_POST;

        assertNotNull(method);
        assertEquals("CLIENT_SECRET_POST", method.name());
    }

    @Test
    @DisplayName("枚举值: CLIENT_SECRET_BASIC 应存在")
    void enumValue_ClientSecretBasic_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.CLIENT_SECRET_BASIC;

        assertNotNull(method);
        assertEquals("CLIENT_SECRET_BASIC", method.name());
    }

    @Test
    @DisplayName("枚举值: CLIENT_SECRET_JWT 应存在")
    void enumValue_ClientSecretJwt_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.CLIENT_SECRET_JWT;

        assertNotNull(method);
        assertEquals("CLIENT_SECRET_JWT", method.name());
    }

    @Test
    @DisplayName("枚举值: PRIVATE_KEY_JWT 应存在")
    void enumValue_PrivateKeyJwt_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.PRIVATE_KEY_JWT;

        assertNotNull(method);
        assertEquals("PRIVATE_KEY_JWT", method.name());
    }

    @Test
    @DisplayName("枚举值: PKCS7 应存在")
    void enumValue_Pkcs7_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.PKCS7;

        assertNotNull(method);
        assertEquals("PKCS7", method.name());
    }

    @Test
    @DisplayName("枚举值: PCA 应存在")
    void enumValue_Pca_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.PCA;

        assertNotNull(method);
        assertEquals("PCA", method.name());
    }

    @Test
    @DisplayName("枚举值: OIDC 应存在")
    void enumValue_Oidc_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.OIDC;

        assertNotNull(method);
        assertEquals("OIDC", method.name());
    }

    @Test
    @DisplayName("枚举值: PLUGIN 应存在")
    void enumValue_Plugin_ShouldExist() {
        TokenAuthnMethod method = TokenAuthnMethod.PLUGIN;

        assertNotNull(method);
        assertEquals("PLUGIN", method.name());
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回 9 个枚举值")
    void values_ShouldReturnNineValues() {
        TokenAuthnMethod[] values = TokenAuthnMethod.values();

        assertEquals(9, values.length);
    }

    @Test
    @DisplayName("values: 应包含所有枚举值")
    void values_ShouldContainAllValues() {
        TokenAuthnMethod[] values = TokenAuthnMethod.values();

        assertTrue(containsValue(values, TokenAuthnMethod.NONE));
        assertTrue(containsValue(values, TokenAuthnMethod.CLIENT_SECRET_POST));
        assertTrue(containsValue(values, TokenAuthnMethod.CLIENT_SECRET_BASIC));
        assertTrue(containsValue(values, TokenAuthnMethod.CLIENT_SECRET_JWT));
        assertTrue(containsValue(values, TokenAuthnMethod.PRIVATE_KEY_JWT));
        assertTrue(containsValue(values, TokenAuthnMethod.PKCS7));
        assertTrue(containsValue(values, TokenAuthnMethod.PCA));
        assertTrue(containsValue(values, TokenAuthnMethod.OIDC));
        assertTrue(containsValue(values, TokenAuthnMethod.PLUGIN));
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 'NONE' 应返回 NONE 枚举值")
    void valueOf_NoneString_ShouldReturnNoneEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("NONE");

        assertEquals(TokenAuthnMethod.NONE, method);
    }

    @Test
    @DisplayName("valueOf: 'CLIENT_SECRET_POST' 应返回 CLIENT_SECRET_POST 枚举值")
    void valueOf_ClientSecretPostString_ShouldReturnClientSecretPostEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("CLIENT_SECRET_POST");

        assertEquals(TokenAuthnMethod.CLIENT_SECRET_POST, method);
    }

    @Test
    @DisplayName("valueOf: 'CLIENT_SECRET_BASIC' 应返回 CLIENT_SECRET_BASIC 枚举值")
    void valueOf_ClientSecretBasicString_ShouldReturnClientSecretBasicEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("CLIENT_SECRET_BASIC");

        assertEquals(TokenAuthnMethod.CLIENT_SECRET_BASIC, method);
    }

    @Test
    @DisplayName("valueOf: 'CLIENT_SECRET_JWT' 应返回 CLIENT_SECRET_JWT 枚举值")
    void valueOf_ClientSecretJwtString_ShouldReturnClientSecretJwtEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("CLIENT_SECRET_JWT");

        assertEquals(TokenAuthnMethod.CLIENT_SECRET_JWT, method);
    }

    @Test
    @DisplayName("valueOf: 'PRIVATE_KEY_JWT' 应返回 PRIVATE_KEY_JWT 枚举值")
    void valueOf_PrivateKeyJwtString_ShouldReturnPrivateKeyJwtEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("PRIVATE_KEY_JWT");

        assertEquals(TokenAuthnMethod.PRIVATE_KEY_JWT, method);
    }

    @Test
    @DisplayName("valueOf: 'PKCS7' 应返回 PKCS7 枚举值")
    void valueOf_Pkcs7String_ShouldReturnPkcs7Enum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("PKCS7");

        assertEquals(TokenAuthnMethod.PKCS7, method);
    }

    @Test
    @DisplayName("valueOf: 'PCA' 应返回 PCA 枚举值")
    void valueOf_PcaString_ShouldReturnPcaEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("PCA");

        assertEquals(TokenAuthnMethod.PCA, method);
    }

    @Test
    @DisplayName("valueOf: 'OIDC' 应返回 OIDC 枚举值")
    void valueOf_OidcString_ShouldReturnOidcEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("OIDC");

        assertEquals(TokenAuthnMethod.OIDC, method);
    }

    @Test
    @DisplayName("valueOf: 'PLUGIN' 应返回 PLUGIN 枚举值")
    void valueOf_PluginString_ShouldReturnPluginEnum() {
        TokenAuthnMethod method = TokenAuthnMethod.valueOf("PLUGIN");

        assertEquals(TokenAuthnMethod.PLUGIN, method);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            TokenAuthnMethod.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("valueOf: null 应抛出 NullPointerException")
    void valueOf_NullName_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            TokenAuthnMethod.valueOf(null);
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: NONE 的序号应为 0")
    void ordinal_None_ShouldBeZero() {
        assertEquals(0, TokenAuthnMethod.NONE.ordinal());
    }

    @Test
    @DisplayName("ordinal: CLIENT_SECRET_POST 的序号应为 1")
    void ordinal_ClientSecretPost_ShouldBeOne() {
        assertEquals(1, TokenAuthnMethod.CLIENT_SECRET_POST.ordinal());
    }

    @Test
    @DisplayName("ordinal: CLIENT_SECRET_BASIC 的序号应为 2")
    void ordinal_ClientSecretBasic_ShouldBeTwo() {
        assertEquals(2, TokenAuthnMethod.CLIENT_SECRET_BASIC.ordinal());
    }

    @Test
    @DisplayName("ordinal: CLIENT_SECRET_JWT 的序号应为 3")
    void ordinal_ClientSecretJwt_ShouldBeThree() {
        assertEquals(3, TokenAuthnMethod.CLIENT_SECRET_JWT.ordinal());
    }

    @Test
    @DisplayName("ordinal: PRIVATE_KEY_JWT 的序号应为 4")
    void ordinal_PrivateKeyJwt_ShouldBeFour() {
        assertEquals(4, TokenAuthnMethod.PRIVATE_KEY_JWT.ordinal());
    }

    @Test
    @DisplayName("ordinal: PKCS7 的序号应为 5")
    void ordinal_Pkcs7_ShouldBeFive() {
        assertEquals(5, TokenAuthnMethod.PKCS7.ordinal());
    }

    @Test
    @DisplayName("ordinal: PCA 的序号应为 6")
    void ordinal_Pca_ShouldBeSix() {
        assertEquals(6, TokenAuthnMethod.PCA.ordinal());
    }

    @Test
    @DisplayName("ordinal: OIDC 的序号应为 7")
    void ordinal_Oidc_ShouldBeSeven() {
        assertEquals(7, TokenAuthnMethod.OIDC.ordinal());
    }

    @Test
    @DisplayName("ordinal: PLUGIN 的序号应为 8")
    void ordinal_Plugin_ShouldBeEight() {
        assertEquals(8, TokenAuthnMethod.PLUGIN.ordinal());
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: 各枚举值名称应正确")
    void name_AllValues_ShouldBeCorrect() {
        assertEquals("NONE", TokenAuthnMethod.NONE.name());
        assertEquals("CLIENT_SECRET_POST", TokenAuthnMethod.CLIENT_SECRET_POST.name());
        assertEquals("CLIENT_SECRET_BASIC", TokenAuthnMethod.CLIENT_SECRET_BASIC.name());
        assertEquals("CLIENT_SECRET_JWT", TokenAuthnMethod.CLIENT_SECRET_JWT.name());
        assertEquals("PRIVATE_KEY_JWT", TokenAuthnMethod.PRIVATE_KEY_JWT.name());
        assertEquals("PKCS7", TokenAuthnMethod.PKCS7.name());
        assertEquals("PCA", TokenAuthnMethod.PCA.name());
        assertEquals("OIDC", TokenAuthnMethod.OIDC.name());
        assertEquals("PLUGIN", TokenAuthnMethod.PLUGIN.name());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("NONE", TokenAuthnMethod.NONE.toString());
        assertEquals("CLIENT_SECRET_POST", TokenAuthnMethod.CLIENT_SECRET_POST.toString());
        assertEquals("CLIENT_SECRET_BASIC", TokenAuthnMethod.CLIENT_SECRET_BASIC.toString());
        assertEquals("CLIENT_SECRET_JWT", TokenAuthnMethod.CLIENT_SECRET_JWT.toString());
        assertEquals("PRIVATE_KEY_JWT", TokenAuthnMethod.PRIVATE_KEY_JWT.toString());
        assertEquals("PKCS7", TokenAuthnMethod.PKCS7.toString());
        assertEquals("PCA", TokenAuthnMethod.PCA.toString());
        assertEquals("OIDC", TokenAuthnMethod.OIDC.toString());
        assertEquals("PLUGIN", TokenAuthnMethod.PLUGIN.toString());
    }

    // ==================== switch 使用测试 ====================

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 NONE")
    void switchStatement_ShouldCorrectlyMatchNone() {
        String result = getMethodDescription(TokenAuthnMethod.NONE);
        assertEquals("none", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 CLIENT_SECRET_POST")
    void switchStatement_ShouldCorrectlyMatchClientSecretPost() {
        String result = getMethodDescription(TokenAuthnMethod.CLIENT_SECRET_POST);
        assertEquals("client_secret_post", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 PRIVATE_KEY_JWT")
    void switchStatement_ShouldCorrectlyMatchPrivateKeyJwt() {
        String result = getMethodDescription(TokenAuthnMethod.PRIVATE_KEY_JWT);
        assertEquals("private_key_jwt", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 PKCS7")
    void switchStatement_ShouldCorrectlyMatchPkcs7() {
        String result = getMethodDescription(TokenAuthnMethod.PKCS7);
        assertEquals("pkcs7", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 PLUGIN")
    void switchStatement_ShouldCorrectlyMatchPlugin() {
        String result = getMethodDescription(TokenAuthnMethod.PLUGIN);
        assertEquals("plugin", result);
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        TokenAuthnMethod method1 = TokenAuthnMethod.NONE;
        TokenAuthnMethod method2 = TokenAuthnMethod.NONE;

        assertEquals(method1, method2);
        assertSame(method1, method2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(TokenAuthnMethod.NONE, TokenAuthnMethod.CLIENT_SECRET_POST);
        assertNotEquals(TokenAuthnMethod.CLIENT_SECRET_BASIC, TokenAuthnMethod.CLIENT_SECRET_JWT);
        assertNotEquals(TokenAuthnMethod.PKCS7, TokenAuthnMethod.PCA);
        assertNotEquals(TokenAuthnMethod.OIDC, TokenAuthnMethod.PLUGIN);
    }

    // ==================== 认证方法分类测试 ====================

    @Test
    @DisplayName("分类: 客户端密钥类认证方法")
    void category_ClientSecretMethods_ShouldBeCorrect() {
        TokenAuthnMethod[] clientSecretMethods = {
            TokenAuthnMethod.CLIENT_SECRET_POST,
            TokenAuthnMethod.CLIENT_SECRET_BASIC,
            TokenAuthnMethod.CLIENT_SECRET_JWT
        };

        assertEquals(3, clientSecretMethods.length);
        for (TokenAuthnMethod method : clientSecretMethods) {
            assertTrue(method.name().startsWith("CLIENT_SECRET"));
        }
    }

    @Test
    @DisplayName("分类: JWT 类认证方法")
    void category_JwtMethods_ShouldBeCorrect() {
        TokenAuthnMethod[] jwtMethods = {
            TokenAuthnMethod.CLIENT_SECRET_JWT,
            TokenAuthnMethod.PRIVATE_KEY_JWT
        };

        assertEquals(2, jwtMethods.length);
        for (TokenAuthnMethod method : jwtMethods) {
            assertTrue(method.name().endsWith("JWT"));
        }
    }

    @Test
    @DisplayName("分类: IDaaS 自定义认证方法")
    void category_IdaasCustomMethods_ShouldBeCorrect() {
        TokenAuthnMethod[] customMethods = {
            TokenAuthnMethod.PKCS7,
            TokenAuthnMethod.PCA,
            TokenAuthnMethod.OIDC,
            TokenAuthnMethod.PLUGIN
        };

        assertEquals(4, customMethods.length);
    }

    // ==================== 辅助方法 ====================

    private boolean containsValue(TokenAuthnMethod[] values, TokenAuthnMethod target) {
        for (TokenAuthnMethod value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private String getMethodDescription(TokenAuthnMethod method) {
        switch (method) {
            case NONE:
                return "none";
            case CLIENT_SECRET_POST:
                return "client_secret_post";
            case CLIENT_SECRET_BASIC:
                return "client_secret_basic";
            case CLIENT_SECRET_JWT:
                return "client_secret_jwt";
            case PRIVATE_KEY_JWT:
                return "private_key_jwt";
            case PKCS7:
                return "pkcs7";
            case PCA:
                return "pca";
            case OIDC:
                return "oidc";
            case PLUGIN:
                return "plugin";
            default:
                return "unknown";
        }
    }
}
