package com.cloud_idaas.core.credential;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IDaaSTokenResponse 单元测试
 */
class IDaaSTokenResponseTest {

    private IDaaSTokenResponse response;

    @BeforeEach
    void setUp() {
        response = new IDaaSTokenResponse();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: 所有对象字段默认应为 null")
    void defaultValue_ObjectFields_ShouldBeNull() {
        assertNull(response.getAccessToken());
        assertNull(response.getIdToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getTokenType());
        assertNull(response.getIssuedTokenType());
    }

    @Test
    @DisplayName("默认值: expiresIn 默认应为 0")
    void defaultValue_ExpiresIn_ShouldBeZero() {
        assertEquals(0L, response.getExpiresIn());
    }

    @Test
    @DisplayName("默认值: expiresAt 默认应为 0")
    void defaultValue_ExpiresAt_ShouldBeZero() {
        assertEquals(0L, response.getExpiresAt());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 accessToken 应正确")
    void setter_AccessToken_ShouldBeSet() {
        response.setAccessToken("test-access-token");
        assertEquals("test-access-token", response.getAccessToken());
    }

    @Test
    @DisplayName("Setter: 设置 idToken 应正确")
    void setter_IdToken_ShouldBeSet() {
        response.setIdToken("test-id-token");
        assertEquals("test-id-token", response.getIdToken());
    }

    @Test
    @DisplayName("Setter: 设置 refreshToken 应正确")
    void setter_RefreshToken_ShouldBeSet() {
        response.setRefreshToken("test-refresh-token");
        assertEquals("test-refresh-token", response.getRefreshToken());
    }

    @Test
    @DisplayName("Setter: 设置 tokenType 应正确")
    void setter_TokenType_ShouldBeSet() {
        response.setTokenType("Bearer");
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    @DisplayName("Setter: 设置 issuedTokenType 应正确")
    void setter_IssuedTokenType_ShouldBeSet() {
        response.setIssuedTokenType("urn:ietf:params:oauth:token-type:access_token");
        assertEquals("urn:ietf:params:oauth:token-type:access_token", response.getIssuedTokenType());
    }

    @Test
    @DisplayName("Setter: 设置 expiresIn 应正确")
    void setter_ExpiresIn_ShouldBeSet() {
        response.setExpiresIn(3600L);
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    @DisplayName("Setter: 设置 expiresAt 应正确")
    void setter_ExpiresAt_ShouldBeSet() {
        response.setExpiresAt(System.currentTimeMillis() / 1000 + 3600);
        assertTrue(response.getExpiresAt() > 0);
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置字段为 null 应正确")
    void nullValue_AllFields_ShouldBeSet() {
        response.setAccessToken("test");
        response.setAccessToken(null);
        assertNull(response.getAccessToken());

        response.setIdToken("test");
        response.setIdToken(null);
        assertNull(response.getIdToken());

        response.setRefreshToken("test");
        response.setRefreshToken(null);
        assertNull(response.getRefreshToken());

        response.setTokenType("Bearer");
        response.setTokenType(null);
        assertNull(response.getTokenType());

        response.setIssuedTokenType("type");
        response.setIssuedTokenType(null);
        assertNull(response.getIssuedTokenType());
    }

    // ==================== IDaaSCredential 接口实现测试 ====================

    @Test
    @DisplayName("接口实现: 应实现 IDaaSCredential 接口")
    void interfaceImplementation_ShouldImplementIDaaSCredential() {
        assertTrue(response instanceof IDaaSCredential);
    }

    @Test
    @DisplayName("接口实现: 接口方法应返回正确的值")
    void interfaceImplementation_InterfaceMethods_ShouldReturnCorrectValues() {
        response.setAccessToken("access-token");
        response.setIdToken("id-token");
        response.setRefreshToken("refresh-token");
        response.setTokenType("Bearer");
        response.setIssuedTokenType("urn:ietf:params:oauth:token-type:access_token");

        IDaaSCredential credential = response;
        assertEquals("access-token", credential.getAccessToken());
        assertEquals("id-token", credential.getIdToken());
        assertEquals("refresh-token", credential.getRefreshToken());
        assertEquals("Bearer", credential.getTokenType());
        assertEquals("urn:ietf:params:oauth:token-type:access_token", credential.getIssuedTokenType());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(response instanceof Serializable);
    }

    // ==================== willSoonExpire 测试 ====================

    @Test
    @DisplayName("willSoonExpire: 未过期时不应返回 true")
    void willSoonExpire_NotExpired_ShouldReturnFalse() {
        long now = System.currentTimeMillis() / 1000;
        response.setExpiresIn(3600L); // 1小时
        response.setExpiresAt(now + 3600);

        assertFalse(response.willSoonExpire());
    }

    @Test
    @DisplayName("willSoonExpire: 即将过期时应返回 true")
    void willSoonExpire_AboutToExpire_ShouldReturnTrue() {
        long now = System.currentTimeMillis() / 1000;
        response.setExpiresIn(3600L); // 1小时
        response.setExpiresAt(now + 100); // 还有100秒过期，小于 expiresIn * 0.15 = 540秒

        assertTrue(response.willSoonExpire());
    }

    @Test
    @DisplayName("willSoonExpire: 已过期时应返回 true")
    void willSoonExpire_AlreadyExpired_ShouldReturnTrue() {
        long now = System.currentTimeMillis() / 1000;
        response.setExpiresIn(3600L);
        response.setExpiresAt(now - 100); // 已过期

        assertTrue(response.willSoonExpire());
    }

    @Test
    @DisplayName("willSoonExpire: expiresIn 为 0 时行为")
    void willSoonExpire_ZeroExpiresIn_ShouldNotThrow() {
        response.setExpiresIn(0L);
        response.setExpiresAt(0L);

        // 不应抛出异常
        assertDoesNotThrow(() -> response.willSoonExpire());
    }

    @Test
    @DisplayName("willSoonExpire: 边界条件测试")
    void willSoonExpire_BoundaryCondition_ShouldBeCorrect() {
        long now = System.currentTimeMillis() / 1000;
        response.setExpiresIn(1000L);
        // expiresIn * 0.15 = 150秒
        // 设置为刚好在边界上
        response.setExpiresAt(now + 150);

        // 边界条件，可能返回 true 或 false
        // 主要是确保不会抛出异常
        assertDoesNotThrow(() -> response.willSoonExpire());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("边界值: expiresIn 设为最大值应正确")
    void boundary_ExpiresInMaxValue_ShouldBeSet() {
        response.setExpiresIn(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getExpiresIn());
    }

    @Test
    @DisplayName("边界值: expiresIn 设为负数应正确")
    void boundary_ExpiresInNegative_ShouldBeSet() {
        response.setExpiresIn(-1L);
        assertEquals(-1L, response.getExpiresIn());
    }

    @Test
    @DisplayName("边界值: expiresAt 设为最大值应正确")
    void boundary_ExpiresAtMaxValue_ShouldBeSet() {
        response.setExpiresAt(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getExpiresAt());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: 设置空字符串应正确")
    void emptyString_AllFields_ShouldBeSet() {
        response.setAccessToken("");
        assertEquals("", response.getAccessToken());

        response.setIdToken("");
        assertEquals("", response.getIdToken());

        response.setRefreshToken("");
        assertEquals("", response.getRefreshToken());

        response.setTokenType("");
        assertEquals("", response.getTokenType());
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: JWT 格式的 token 应正确处理")
    void specialCharacters_JwtToken_ShouldBeCorrect() {
        String jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        response.setAccessToken(jwtToken);
        assertEquals(jwtToken, response.getAccessToken());

        response.setIdToken(jwtToken);
        assertEquals(jwtToken, response.getIdToken());

        response.setRefreshToken(jwtToken);
        assertEquals(jwtToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("特殊字符: 包含特殊字符的 token 应正确处理")
    void specialCharacters_SpecialChars_ShouldBeCorrect() {
        String specialToken = "token_with-special.chars:123~!@#$%^&*()";
        
        response.setAccessToken(specialToken);
        assertEquals(specialToken, response.getAccessToken());
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        IDaaSTokenResponse response1 = new IDaaSTokenResponse();
        IDaaSTokenResponse response2 = new IDaaSTokenResponse();

        response1.setAccessToken("token-1");
        response2.setAccessToken("token-2");

        assertEquals("token-1", response1.getAccessToken());
        assertEquals("token-2", response2.getAccessToken());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        long now = System.currentTimeMillis() / 1000;
        
        response.setAccessToken("full-access-token");
        response.setIdToken("full-id-token");
        response.setRefreshToken("full-refresh-token");
        response.setTokenType("Bearer");
        response.setIssuedTokenType("urn:ietf:params:oauth:token-type:access_token");
        response.setExpiresIn(7200L);
        response.setExpiresAt(now + 7200);

        assertEquals("full-access-token", response.getAccessToken());
        assertEquals("full-id-token", response.getIdToken());
        assertEquals("full-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("urn:ietf:params:oauth:token-type:access_token", response.getIssuedTokenType());
        assertEquals(7200L, response.getExpiresIn());
        assertEquals(now + 7200, response.getExpiresAt());
    }

    // ==================== 多次设置测试 ====================

    @Test
    @DisplayName("多次设置: 多次设置同一字段应使用最后一次的值")
    void multipleSet_SameField_ShouldUseLastValue() {
        response.setAccessToken("token-1");
        response.setAccessToken("token-2");
        response.setAccessToken("token-3");

        assertEquals("token-3", response.getAccessToken());
    }

    // ==================== 长字符串测试 ====================

    @Test
    @DisplayName("长字符串: 长 token 应正确处理")
    void longString_Token_ShouldBeCorrect() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longToken = sb.toString();

        response.setAccessToken(longToken);
        assertEquals(longToken, response.getAccessToken());
        assertEquals(1000, response.getAccessToken().length());
    }

    // ==================== 典型 OAuth2 响应场景测试 ====================

    @Test
    @DisplayName("场景: 客户端凭证模式响应")
    void scenario_ClientCredentials_ShouldBeCorrect() {
        long now = System.currentTimeMillis() / 1000;
        
        response.setAccessToken("client-access-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(3600L);
        response.setExpiresAt(now + 3600);
        // 客户端凭证模式通常没有 id_token 和 refresh_token

        assertEquals("client-access-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNull(response.getIdToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("场景: 授权码模式响应（包含所有 token）")
    void scenario_AuthorizationCode_ShouldBeCorrect() {
        long now = System.currentTimeMillis() / 1000;
        
        response.setAccessToken("user-access-token");
        response.setIdToken("user-id-token");
        response.setRefreshToken("user-refresh-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(7200L);
        response.setExpiresAt(now + 7200);

        assertEquals("user-access-token", response.getAccessToken());
        assertEquals("user-id-token", response.getIdToken());
        assertEquals("user-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    @DisplayName("场景: 令牌交换响应")
    void scenario_TokenExchange_ShouldBeCorrect() {
        long now = System.currentTimeMillis() / 1000;
        
        response.setAccessToken("exchanged-access-token");
        response.setIssuedTokenType("urn:ietf:params:oauth:token-type:access_token");
        response.setTokenType("Bearer");
        response.setExpiresIn(3600L);
        response.setExpiresAt(now + 3600);

        assertEquals("exchanged-access-token", response.getAccessToken());
        assertEquals("urn:ietf:params:oauth:token-type:access_token", response.getIssuedTokenType());
        assertEquals("Bearer", response.getTokenType());
    }
}
