package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OAuth2Constants 单元测试
 */
class OAuth2ConstantsTest {

    // ==================== 基础参数名测试 ====================

    @Test
    @DisplayName("CLIENT_ID 应正确")
    void clientId_ShouldBeCorrect() {
        assertEquals("client_id", OAuth2Constants.CLIENT_ID);
    }

    @Test
    @DisplayName("CLIENT_SECRET 应正确")
    void clientSecret_ShouldBeCorrect() {
        assertEquals("client_secret", OAuth2Constants.CLIENT_SECRET);
    }

    @Test
    @DisplayName("SCOPE 应正确")
    void scope_ShouldBeCorrect() {
        assertEquals("scope", OAuth2Constants.SCOPE);
    }

    @Test
    @DisplayName("DEVICE_CODE 应正确")
    void deviceCode_ShouldBeCorrect() {
        assertEquals("device_code", OAuth2Constants.DEVICE_CODE);
    }

    @Test
    @DisplayName("GRANT_TYPE 应正确")
    void grantType_ShouldBeCorrect() {
        assertEquals("grant_type", OAuth2Constants.GRANT_TYPE);
    }

    // ==================== 授权类型值测试 ====================

    @Test
    @DisplayName("CLIENT_CREDENTIALS_GRANT_TYPE_VALUE 应正确")
    void clientCredentialsGrantTypeValue_ShouldBeCorrect() {
        assertEquals("client_credentials", OAuth2Constants.CLIENT_CREDENTIALS_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("TOKEN_EXCHANGE_GRANT_TYPE_VALUE 应正确")
    void tokenExchangeGrantTypeValue_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:grant-type:token-exchange", 
                OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("DEVICE_CODE_GRANT_TYPE_VALUE 应正确")
    void deviceCodeGrantTypeValue_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:grant-type:device_code", 
                OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("REFRESH_TOKEN_GRANT_TYPE_VALUE 应正确")
    void refreshTokenGrantTypeValue_ShouldBeCorrect() {
        assertEquals("refresh_token", OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE);
    }

    // ==================== 客户端断言参数测试 ====================

    @Test
    @DisplayName("CLIENT_ASSERTION_TYPE 应正确")
    void clientAssertionType_ShouldBeCorrect() {
        assertEquals("client_assertion_type", OAuth2Constants.CLIENT_ASSERTION_TYPE);
    }

    @Test
    @DisplayName("CLIENT_ASSERTION 应正确")
    void clientAssertion_ShouldBeCorrect() {
        assertEquals("client_assertion", OAuth2Constants.CLIENT_ASSERTION);
    }

    @Test
    @DisplayName("APPLICATION_FEDERATED_CREDENTIAL_NAME 应正确")
    void applicationFederatedCredentialName_ShouldBeCorrect() {
        assertEquals("application_federated_credential_name", 
                OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME);
    }

    // ==================== 令牌参数测试 ====================

    @Test
    @DisplayName("REFRESH_TOKEN_PARAMETER 应正确")
    void refreshTokenParameter_ShouldBeCorrect() {
        assertEquals("refresh_token", OAuth2Constants.REFRESH_TOKEN_PARAMETER);
    }

    @Test
    @DisplayName("CLIENT_X509_CERTIFICATE 应正确")
    void clientX509Certificate_ShouldBeCorrect() {
        assertEquals("client_x509", OAuth2Constants.CLIENT_X509_CERTIFICATE);
    }

    @Test
    @DisplayName("X509_CERT_CHAINS 应正确")
    void x509CertChains_ShouldBeCorrect() {
        assertEquals("client_x509_chain", OAuth2Constants.X509_CERT_CHAINS);
    }

    // ==================== 令牌交换参数测试 ====================

    @Test
    @DisplayName("SUBJECT_TOKEN 应正确")
    void subjectToken_ShouldBeCorrect() {
        assertEquals("subject_token", OAuth2Constants.SUBJECT_TOKEN);
    }

    @Test
    @DisplayName("ACTOR_TOKEN 应正确")
    void actorToken_ShouldBeCorrect() {
        assertEquals("actor_token", OAuth2Constants.ACTOR_TOKEN);
    }

    @Test
    @DisplayName("SUBJECT_TOKEN_TYPE 应正确")
    void subjectTokenType_ShouldBeCorrect() {
        assertEquals("subject_token_type", OAuth2Constants.SUBJECT_TOKEN_TYPE);
    }

    @Test
    @DisplayName("ACTOR_TOKEN_TYPE 应正确")
    void actorTokenType_ShouldBeCorrect() {
        assertEquals("actor_token_type", OAuth2Constants.ACTOR_TOKEN_TYPE);
    }

    @Test
    @DisplayName("REQUESTED_TOKEN_TYPE 应正确")
    void requestedTokenType_ShouldBeCorrect() {
        assertEquals("requested_token_type", OAuth2Constants.REQUESTED_TOKEN_TYPE);
    }

    @Test
    @DisplayName("ACCESS_TOKEN_TYPE 应正确")
    void accessTokenType_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:token-type:access_token", 
                OAuth2Constants.ACCESS_TOKEN_TYPE);
    }

    @Test
    @DisplayName("AUDIENCE 应正确")
    void audience_ShouldBeCorrect() {
        assertEquals("audience", OAuth2Constants.AUDIENCE);
    }

    // ==================== 非空验证测试 ====================

    @Test
    @DisplayName("所有常量不应为空")
    void allConstants_ShouldNotBeNull() {
        assertNotNull(OAuth2Constants.CLIENT_ID);
        assertNotNull(OAuth2Constants.CLIENT_SECRET);
        assertNotNull(OAuth2Constants.SCOPE);
        assertNotNull(OAuth2Constants.DEVICE_CODE);
        assertNotNull(OAuth2Constants.GRANT_TYPE);
        assertNotNull(OAuth2Constants.CLIENT_CREDENTIALS_GRANT_TYPE_VALUE);
        assertNotNull(OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE);
        assertNotNull(OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE);
        assertNotNull(OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE);
        assertNotNull(OAuth2Constants.CLIENT_ASSERTION_TYPE);
        assertNotNull(OAuth2Constants.CLIENT_ASSERTION);
        assertNotNull(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME);
        assertNotNull(OAuth2Constants.REFRESH_TOKEN_PARAMETER);
        assertNotNull(OAuth2Constants.CLIENT_X509_CERTIFICATE);
        assertNotNull(OAuth2Constants.X509_CERT_CHAINS);
        assertNotNull(OAuth2Constants.SUBJECT_TOKEN);
        assertNotNull(OAuth2Constants.ACTOR_TOKEN);
        assertNotNull(OAuth2Constants.SUBJECT_TOKEN_TYPE);
        assertNotNull(OAuth2Constants.ACTOR_TOKEN_TYPE);
        assertNotNull(OAuth2Constants.REQUESTED_TOKEN_TYPE);
        assertNotNull(OAuth2Constants.ACCESS_TOKEN_TYPE);
        assertNotNull(OAuth2Constants.AUDIENCE);
    }

    @Test
    @DisplayName("所有常量不应为空字符串")
    void allConstants_ShouldNotBeEmpty() {
        assertFalse(OAuth2Constants.CLIENT_ID.isEmpty());
        assertFalse(OAuth2Constants.CLIENT_SECRET.isEmpty());
        assertFalse(OAuth2Constants.SCOPE.isEmpty());
        assertFalse(OAuth2Constants.GRANT_TYPE.isEmpty());
        assertFalse(OAuth2Constants.ACCESS_TOKEN_TYPE.isEmpty());
    }

    // ==================== 格式验证测试 ====================

    @Test
    @DisplayName("基础参数名应为下划线格式")
    void basicParameterNames_ShouldBeSnakeCase() {
        assertTrue(OAuth2Constants.CLIENT_ID.contains("_"));
        assertTrue(OAuth2Constants.CLIENT_SECRET.contains("_"));
        assertTrue(OAuth2Constants.DEVICE_CODE.contains("_"));
        assertTrue(OAuth2Constants.GRANT_TYPE.contains("_"));
    }

    @Test
    @DisplayName("URN 格式常量应以 urn: 开头")
    void urnFormatConstants_ShouldStartWithUrn() {
        assertTrue(OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE.startsWith("urn:"));
        assertTrue(OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE.startsWith("urn:"));
        assertTrue(OAuth2Constants.ACCESS_TOKEN_TYPE.startsWith("urn:"));
    }

    @Test
    @DisplayName("URN 格式常量应包含 oauth 关键字")
    void urnFormatConstants_ShouldContainOAuth() {
        assertTrue(OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE.contains("oauth"));
        assertTrue(OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE.contains("oauth"));
        assertTrue(OAuth2Constants.ACCESS_TOKEN_TYPE.contains("oauth"));
    }

    // ==================== 值一致性测试 ====================

    @Test
    @DisplayName("REFRESH_TOKEN_GRANT_TYPE_VALUE 和 REFRESH_TOKEN_PARAMETER 值应相同")
    void refreshTokenValues_ShouldBeConsistent() {
        assertEquals(OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE, 
                OAuth2Constants.REFRESH_TOKEN_PARAMETER);
    }

    // ==================== 唯一性测试 ====================

    @Test
    @DisplayName("授权类型值应唯一")
    void grantTypeValues_ShouldBeUnique() {
        String[] grantTypes = {
            OAuth2Constants.CLIENT_CREDENTIALS_GRANT_TYPE_VALUE,
            OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE,
            OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE,
            OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE
        };

        for (int i = 0; i < grantTypes.length; i++) {
            for (int j = i + 1; j < grantTypes.length; j++) {
                assertNotEquals(grantTypes[i], grantTypes[j], 
                        "授权类型值不应重复: " + grantTypes[i]);
            }
        }
    }
}
