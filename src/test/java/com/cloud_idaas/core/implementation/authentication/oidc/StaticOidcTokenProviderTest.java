package com.cloud_idaas.core.implementation.authentication.oidc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaticOidcTokenProvider 单元测试
 */
class StaticOidcTokenProviderTest {

    private static final String TEST_TOKEN = "test-oidc-token";

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 无参构造应创建实例")
    void constructor_NoArgs_ShouldCreateInstance() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("构造函数: 带参构造应设置 token")
    void constructor_WithArgs_ShouldSetToken() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(TEST_TOKEN);

        assertNotNull(provider);
        assertEquals(TEST_TOKEN, provider.getOidcToken());
    }

    @Test
    @DisplayName("构造函数: 带参构造应接受 null 值")
    void constructor_WithNull_ShouldAcceptNull() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(null);

        assertNotNull(provider);
        assertNull(provider.getOidcToken());
    }

    @Test
    @DisplayName("构造函数: 带参构造应接受空字符串")
    void constructor_WithEmptyString_ShouldAcceptEmptyString() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider("");

        assertNotNull(provider);
        assertEquals("", provider.getOidcToken());
    }

    // ==================== getOidcToken 测试 ====================

    @Test
    @DisplayName("getOidcToken: 无参构造后应返回 null")
    void getOidcToken_AfterNoArgsConstructor_ShouldReturnNull() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider();

        assertNull(provider.getOidcToken());
    }

    @Test
    @DisplayName("getOidcToken: 应返回设置的 token")
    void getOidcToken_ShouldReturnToken() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(TEST_TOKEN);

        String result = provider.getOidcToken();

        assertEquals(TEST_TOKEN, result);
    }

    @Test
    @DisplayName("getOidcToken: 多次调用应返回相同值")
    void getOidcToken_MultipleCalls_ShouldReturnSameValue() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(TEST_TOKEN);

        String result1 = provider.getOidcToken();
        String result2 = provider.getOidcToken();
        String result3 = provider.getOidcToken();

        assertEquals(TEST_TOKEN, result1);
        assertEquals(TEST_TOKEN, result2);
        assertEquals(TEST_TOKEN, result3);
        assertSame(result1, result2);
        assertSame(result2, result3);
    }

    // ==================== setOidcToken 测试 ====================

    @Test
    @DisplayName("setOidcToken: 应正确设置 token")
    void setOidcToken_ShouldSetToken() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider();

        provider.setOidcToken(TEST_TOKEN);

        assertEquals(TEST_TOKEN, provider.getOidcToken());
    }

    @Test
    @DisplayName("setOidcToken: 应支持更新 token")
    void setOidcToken_ShouldSupportUpdate() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider("initial-token");

        provider.setOidcToken("updated-token");

        assertEquals("updated-token", provider.getOidcToken());
    }

    @Test
    @DisplayName("setOidcToken: 应支持设置为 null")
    void setOidcToken_WithNull_ShouldSetNull() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(TEST_TOKEN);

        provider.setOidcToken(null);

        assertNull(provider.getOidcToken());
    }

    @Test
    @DisplayName("setOidcToken: 应支持设置为空字符串")
    void setOidcToken_WithEmptyString_ShouldSetEmptyString() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(TEST_TOKEN);

        provider.setOidcToken("");

        assertEquals("", provider.getOidcToken());
    }

    @Test
    @DisplayName("setOidcToken: 应支持多次设置")
    void setOidcToken_MultipleSets_ShouldUseLastValue() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider();

        provider.setOidcToken("token-1");
        provider.setOidcToken("token-2");
        provider.setOidcToken("token-3");

        assertEquals("token-3", provider.getOidcToken());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 OidcTokenProvider 接口")
    void interface_ShouldImplementOidcTokenProvider() {
        StaticOidcTokenProvider provider = new StaticOidcTokenProvider();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.OidcTokenProvider);
    }

    // ==================== 功能测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护自己的 token")
    void multipleInstances_ShouldMaintainIndependentTokens() {
        StaticOidcTokenProvider provider1 = new StaticOidcTokenProvider("token-1");
        StaticOidcTokenProvider provider2 = new StaticOidcTokenProvider("token-2");

        assertEquals("token-1", provider1.getOidcToken());
        assertEquals("token-2", provider2.getOidcToken());

        provider1.setOidcToken("new-token-1");

        assertEquals("new-token-1", provider1.getOidcToken());
        assertEquals("token-2", provider2.getOidcToken());
    }

    @Test
    @DisplayName("功能: 长 token 内容应正确存储和返回")
    void longTokenContent_ShouldBeStoredCorrectly() {
        StringBuilder longToken = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longToken.append("OIDCTokenContent");
        }
        String longTokenString = longToken.toString();

        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(longTokenString);

        assertEquals(longTokenString, provider.getOidcToken());
        assertEquals(longTokenString.length(), provider.getOidcToken().length());
    }

    @Test
    @DisplayName("功能: JWT 格式 token 应正确存储和返回")
    void jwtFormatToken_ShouldBeStoredCorrectly() {
        String jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.NHVaYe26MbtOYhSKkoKYdFVomg4i8JmRcoAfGQOBfSBJU4h4NrB7k4E4R3f8j4y8z7Y8x7w6v5u4t3s2r1q0p9o8n7m6l5k4j3i2h1g0f";

        StaticOidcTokenProvider provider = new StaticOidcTokenProvider(jwtToken);

        assertEquals(jwtToken, provider.getOidcToken());
    }
}
