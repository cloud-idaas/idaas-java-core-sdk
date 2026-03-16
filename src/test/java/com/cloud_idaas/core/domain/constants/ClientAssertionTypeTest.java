package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientAssertionType 单元测试
 */
class ClientAssertionTypeTest {

    // ==================== 常量值测试 ====================

    @Test
    @DisplayName("OAUTH_JWT_BEARER 应正确")
    void oauthJwtBearer_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", 
                ClientAssertionType.OAUTH_JWT_BEARER);
    }

    @Test
    @DisplayName("PRIVATE_CA_JWT_BEARER 应正确")
    void privateCaJwtBearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:x509-jwt-bearer", 
                ClientAssertionType.PRIVATE_CA_JWT_BEARER);
    }

    @Test
    @DisplayName("PKCS7_BEARER 应正确")
    void pkcs7Bearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:pkcs7-bearer", 
                ClientAssertionType.PKCS7_BEARER);
    }

    @Test
    @DisplayName("OIDC_BEARER 应正确")
    void oidcBearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:id-token-bearer", 
                ClientAssertionType.OIDC_BEARER);
    }

    // ==================== 非空验证测试 ====================

    @Test
    @DisplayName("所有常量不应为空")
    void allConstants_ShouldNotBeNull() {
        assertNotNull(ClientAssertionType.OAUTH_JWT_BEARER);
        assertNotNull(ClientAssertionType.PRIVATE_CA_JWT_BEARER);
        assertNotNull(ClientAssertionType.PKCS7_BEARER);
        assertNotNull(ClientAssertionType.OIDC_BEARER);
    }

    @Test
    @DisplayName("所有常量不应为空字符串")
    void allConstants_ShouldNotBeEmpty() {
        assertFalse(ClientAssertionType.OAUTH_JWT_BEARER.isEmpty());
        assertFalse(ClientAssertionType.PRIVATE_CA_JWT_BEARER.isEmpty());
        assertFalse(ClientAssertionType.PKCS7_BEARER.isEmpty());
        assertFalse(ClientAssertionType.OIDC_BEARER.isEmpty());
    }

    // ==================== URN 格式验证测试 ====================

    @Test
    @DisplayName("所有常量应为 URN 格式")
    void allConstants_ShouldBeUrnFormat() {
        assertTrue(ClientAssertionType.OAUTH_JWT_BEARER.startsWith("urn:"));
        assertTrue(ClientAssertionType.PRIVATE_CA_JWT_BEARER.startsWith("urn:"));
        assertTrue(ClientAssertionType.PKCS7_BEARER.startsWith("urn:"));
        assertTrue(ClientAssertionType.OIDC_BEARER.startsWith("urn:"));
    }

    @Test
    @DisplayName("所有常量应包含 oauth 关键字")
    void allConstants_ShouldContainOAuth() {
        assertTrue(ClientAssertionType.OAUTH_JWT_BEARER.contains("oauth"));
        assertTrue(ClientAssertionType.PRIVATE_CA_JWT_BEARER.contains("oauth"));
        assertTrue(ClientAssertionType.PKCS7_BEARER.contains("oauth"));
        assertTrue(ClientAssertionType.OIDC_BEARER.contains("oauth"));
    }

    @Test
    @DisplayName("所有常量应包含 client-assertion-type 关键字")
    void allConstants_ShouldContainClientAssertionType() {
        assertTrue(ClientAssertionType.OAUTH_JWT_BEARER.contains("client-assertion-type"));
        assertTrue(ClientAssertionType.PRIVATE_CA_JWT_BEARER.contains("client-assertion-type"));
        assertTrue(ClientAssertionType.PKCS7_BEARER.contains("client-assertion-type"));
        assertTrue(ClientAssertionType.OIDC_BEARER.contains("client-assertion-type"));
    }

    // ==================== 唯一性测试 ====================

    @Test
    @DisplayName("所有常量值应唯一")
    void allConstants_ShouldBeUnique() {
        String[] values = {
            ClientAssertionType.OAUTH_JWT_BEARER,
            ClientAssertionType.PRIVATE_CA_JWT_BEARER,
            ClientAssertionType.PKCS7_BEARER,
            ClientAssertionType.OIDC_BEARER
        };

        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j], 
                        "常量值不应重复: " + values[i]);
            }
        }
    }

    // ==================== 特定类型验证测试 ====================

    @Test
    @DisplayName("OAUTH_JWT_BEARER 应为 IETF 标准格式")
    void oauthJwtBearer_ShouldBeIetfStandardFormat() {
        assertTrue(ClientAssertionType.OAUTH_JWT_BEARER.contains("ietf:params"));
    }

    @Test
    @DisplayName("非标准类型应包含 cloud:idaas 标识")
    void nonStandardTypes_ShouldContainCloudIdaas() {
        assertTrue(ClientAssertionType.PRIVATE_CA_JWT_BEARER.contains("cloud:idaas"));
        assertTrue(ClientAssertionType.PKCS7_BEARER.contains("cloud:idaas"));
        assertTrue(ClientAssertionType.OIDC_BEARER.contains("cloud:idaas"));
    }
}
