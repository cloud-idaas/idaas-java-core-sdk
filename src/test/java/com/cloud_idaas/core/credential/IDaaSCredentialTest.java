package com.cloud_idaas.core.credential;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IDaaSCredential 单元测试
 * 通过测试实现类来验证接口契约
 */
class IDaaSCredentialTest {

    // ==================== 测试用实现类 ====================

    /**
     * 简单的测试实现类
     */
    static class TestCredential implements IDaaSCredential {
        private final String accessToken;
        private final String idToken;
        private final String refreshToken;
        private final String tokenType;
        private final String issuedTokenType;

        TestCredential(String accessToken, String idToken, String refreshToken, 
                       String tokenType, String issuedTokenType) {
            this.accessToken = accessToken;
            this.idToken = idToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.issuedTokenType = issuedTokenType;
        }

        @Override
        public String getAccessToken() {
            return accessToken;
        }

        @Override
        public String getIdToken() {
            return idToken;
        }

        @Override
        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public String getTokenType() {
            return tokenType;
        }

        @Override
        public String getIssuedTokenType() {
            return issuedTokenType;
        }
    }

    // ==================== 接口方法测试 ====================

    @Test
    @DisplayName("getAccessToken: 应返回正确的 access token")
    void getAccessToken_ShouldReturnCorrectValue() {
        IDaaSCredential credential = new TestCredential(
                "access-token-123", null, null, "Bearer", null);

        assertEquals("access-token-123", credential.getAccessToken());
    }

    @Test
    @DisplayName("getIdToken: 应返回正确的 id token")
    void getIdToken_ShouldReturnCorrectValue() {
        IDaaSCredential credential = new TestCredential(
                "access-token", "id-token-456", null, "Bearer", null);

        assertEquals("id-token-456", credential.getIdToken());
    }

    @Test
    @DisplayName("getRefreshToken: 应返回正确的 refresh token")
    void getRefreshToken_ShouldReturnCorrectValue() {
        IDaaSCredential credential = new TestCredential(
                "access-token", null, "refresh-token-789", "Bearer", null);

        assertEquals("refresh-token-789", credential.getRefreshToken());
    }

    @Test
    @DisplayName("getTokenType: 应返回正确的 token type")
    void getTokenType_ShouldReturnCorrectValue() {
        IDaaSCredential credential = new TestCredential(
                "access-token", null, null, "Bearer", null);

        assertEquals("Bearer", credential.getTokenType());
    }

    @Test
    @DisplayName("getIssuedTokenType: 应返回正确的 issued token type")
    void getIssuedTokenType_ShouldReturnCorrectValue() {
        IDaaSCredential credential = new TestCredential(
                "access-token", null, null, "Bearer", 
                "urn:ietf:params:oauth:token-type:access_token");

        assertEquals("urn:ietf:params:oauth:token-type:access_token", 
                credential.getIssuedTokenType());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: access token 为 null 时应返回 null")
    void getAccessToken_NullValue_ShouldReturnNull() {
        IDaaSCredential credential = new TestCredential(
                null, null, null, null, null);

        assertNull(credential.getAccessToken());
    }

    @Test
    @DisplayName("null值: id token 为 null 时应返回 null")
    void getIdToken_NullValue_ShouldReturnNull() {
        IDaaSCredential credential = new TestCredential(
                "access-token", null, null, "Bearer", null);

        assertNull(credential.getIdToken());
    }

    @Test
    @DisplayName("null值: refresh token 为 null 时应返回 null")
    void getRefreshToken_NullValue_ShouldReturnNull() {
        IDaaSCredential credential = new TestCredential(
                "access-token", null, null, "Bearer", null);

        assertNull(credential.getRefreshToken());
    }

    // ==================== 完整凭证测试 ====================

    @Test
    @DisplayName("完整凭证: 所有字段都有值时应正确返回")
    void fullCredential_AllFields_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential(
                "access-token-full",
                "id-token-full",
                "refresh-token-full",
                "Bearer",
                "urn:ietf:params:oauth:token-type:access_token"
        );

        assertEquals("access-token-full", credential.getAccessToken());
        assertEquals("id-token-full", credential.getIdToken());
        assertEquals("refresh-token-full", credential.getRefreshToken());
        assertEquals("Bearer", credential.getTokenType());
        assertEquals("urn:ietf:params:oauth:token-type:access_token", 
                credential.getIssuedTokenType());
    }

    // ==================== 多态性测试 ====================

    @Test
    @DisplayName("多态性: IDaaSTokenResponse 应可作为 IDaaSCredential 使用")
    void polymorphism_IDaaSTokenResponse_ShouldBeUsableAsIDaaSCredential() {
        IDaaSTokenResponse tokenResponse = new IDaaSTokenResponse();
        tokenResponse.setAccessToken("test-access-token");
        tokenResponse.setTokenType("Bearer");

        IDaaSCredential credential = tokenResponse;

        assertEquals("test-access-token", credential.getAccessToken());
        assertEquals("Bearer", credential.getTokenType());
    }

    // ==================== 接口契约测试 ====================

    @Test
    @DisplayName("接口契约: 不同实现应遵循相同的方法签名")
    void interfaceContract_DifferentImplementations_ShouldFollowSameSignature() {
        IDaaSCredential impl1 = new TestCredential("token1", null, null, "Bearer", null);
        IDaaSTokenResponse impl2 = new IDaaSTokenResponse();
        impl2.setAccessToken("token2");
        impl2.setTokenType("Bearer");

        // 验证两个实现都可以正常调用接口方法
        assertNotNull(impl1.getAccessToken());
        assertNotNull(impl2.getAccessToken());
        assertEquals("Bearer", impl1.getTokenType());
        assertEquals("Bearer", impl2.getTokenType());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: access token 为空字符串时应正确返回")
    void getAccessToken_EmptyString_ShouldReturnEmpty() {
        IDaaSCredential credential = new TestCredential("", null, null, null, null);

        assertEquals("", credential.getAccessToken());
    }

    @Test
    @DisplayName("空字符串: token type 为空字符串时应正确返回")
    void getTokenType_EmptyString_ShouldReturnEmpty() {
        IDaaSCredential credential = new TestCredential("token", null, null, "", null);

        assertEquals("", credential.getTokenType());
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: token 包含特殊字符时应正确返回")
    void accessToken_SpecialCharacters_ShouldBeCorrect() {
        String specialToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.signature";
        IDaaSCredential credential = new TestCredential(specialToken, null, null, "Bearer", null);

        assertEquals(specialToken, credential.getAccessToken());
    }

    @Test
    @DisplayName("特殊字符: token 包含 Unicode 字符时应正确返回")
    void accessToken_UnicodeCharacters_ShouldBeCorrect() {
        String unicodeToken = "token_测试_日本語_한국어";
        IDaaSCredential credential = new TestCredential(unicodeToken, null, null, "Bearer", null);

        assertEquals(unicodeToken, credential.getAccessToken());
    }

    // ==================== 长字符串测试 ====================

    @Test
    @DisplayName("长字符串: 长 token 应正确处理")
    void accessToken_LongString_ShouldBeCorrect() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longToken = sb.toString();

        IDaaSCredential credential = new TestCredential(longToken, null, null, "Bearer", null);

        assertEquals(longToken, credential.getAccessToken());
        assertEquals(1000, credential.getAccessToken().length());
    }

    // ==================== 常见 token type 测试 ====================

    @Test
    @DisplayName("token type: Bearer 类型应正确")
    void tokenType_Bearer_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential("token", null, null, "Bearer", null);

        assertEquals("Bearer", credential.getTokenType());
    }

    @Test
    @DisplayName("token type: MAC 类型应正确")
    void tokenType_Mac_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential("token", null, null, "MAC", null);

        assertEquals("MAC", credential.getTokenType());
    }

    // ==================== 常见 issued token type 测试 ====================

    @Test
    @DisplayName("issued token type: access_token 类型应正确")
    void issuedTokenType_AccessToken_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential(
                "token", null, null, "Bearer", 
                "urn:ietf:params:oauth:token-type:access_token");

        assertEquals("urn:ietf:params:oauth:token-type:access_token", 
                credential.getIssuedTokenType());
    }

    @Test
    @DisplayName("issued token type: refresh_token 类型应正确")
    void issuedTokenType_RefreshToken_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential(
                "token", null, "refresh-token", "Bearer", 
                "urn:ietf:params:oauth:token-type:refresh_token");

        assertEquals("urn:ietf:params:oauth:token-type:refresh_token", 
                credential.getIssuedTokenType());
    }

    @Test
    @DisplayName("issued token type: id_token 类型应正确")
    void issuedTokenType_IdToken_ShouldBeCorrect() {
        IDaaSCredential credential = new TestCredential(
                "token", "id-token", null, "Bearer", 
                "urn:ietf:params:oauth:token-type:id_token");

        assertEquals("urn:ietf:params:oauth:token-type:id_token", 
                credential.getIssuedTokenType());
    }
}
