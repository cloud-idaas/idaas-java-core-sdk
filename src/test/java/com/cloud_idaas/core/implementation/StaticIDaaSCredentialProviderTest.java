package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.credential.IDaaSCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * StaticIDaaSCredentialProvider 单元测试
 */
class StaticIDaaSCredentialProviderTest {

    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_ID_TOKEN = "test-id-token";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private static final String TEST_TOKEN_TYPE = "Bearer";
    private static final String TEST_ISSUED_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 使用有效的 credential 成功构建")
    void builder_WithValidCredential_ShouldBuildSuccessfully() {
        IDaaSCredential credential = createMockCredential();

        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("Builder: 使用 null credential 应允许构建")
    void builder_WithNullCredential_ShouldAllowBuild() {
        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(null)
                .build();

        assertNotNull(provider);
        assertNull(provider.getCredential());
    }

    @Test
    @DisplayName("Builder: 不设置 credential 应允许构建")
    void builder_WithoutSettingCredential_ShouldAllowBuild() {
        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .build();

        assertNotNull(provider);
        assertNull(provider.getCredential());
    }

    @Test
    @DisplayName("Builder: 支持链式调用")
    void builder_ShouldSupportMethodChaining() {
        IDaaSCredential credential = createMockCredential();

        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();

        assertNotNull(provider);
        assertEquals(credential, provider.getCredential());
    }

    // ==================== getCredential 测试 ====================

    @Test
    @DisplayName("getCredential: 应返回构建时设置的 credential")
    void getCredential_ShouldReturnSetCredential() {
        IDaaSCredential credential = createMockCredential();
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        IDaaSCredential result = provider.getCredential();

        assertEquals(credential, result);
    }

    @Test
    @DisplayName("getCredential: 多次调用应返回相同的 credential")
    void getCredential_CalledMultipleTimes_ShouldReturnSameCredential() {
        IDaaSCredential credential = createMockCredential();
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        IDaaSCredential result1 = provider.getCredential();
        IDaaSCredential result2 = provider.getCredential();
        IDaaSCredential result3 = provider.getCredential();

        assertSame(result1, result2);
        assertSame(result2, result3);
        assertEquals(credential, result1);
    }

    @Test
    @DisplayName("getCredential: 当 credential 为 null 时应返回 null")
    void getCredential_WhenCredentialIsNull_ShouldReturnNull() {
        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(null)
                .build();

        IDaaSCredential result = provider.getCredential();

        assertNull(result);
    }

    // ==================== getBearerToken 测试 (继承自 IDaaSCredentialProvider) ====================

    @Test
    @DisplayName("getBearerToken: 应返回 credential 的 access token")
    void getBearerToken_ShouldReturnAccessToken() {
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn(TEST_ACCESS_TOKEN);
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        String bearerToken = provider.getBearerToken();

        assertEquals(TEST_ACCESS_TOKEN, bearerToken);
    }

    @Test
    @DisplayName("getBearerToken: 当 credential 为 null 时应返回 null")
    void getBearerToken_WhenCredentialIsNull_ShouldReturnNull() {
        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(null)
                .build();

        String bearerToken = provider.getBearerToken();

        assertNull(bearerToken);
    }

    @Test
    @DisplayName("getBearerToken: 当 access token 为 null 时应返回 null")
    void getBearerToken_WhenAccessTokenIsNull_ShouldReturnNull() {
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn(null);
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        String bearerToken = provider.getBearerToken();

        assertNull(bearerToken);
    }

    @Test
    @DisplayName("getBearerToken: 当 access token 为空字符串时应返回空字符串")
    void getBearerToken_WhenAccessTokenIsEmpty_ShouldReturnEmpty() {
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn("");
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        String bearerToken = provider.getBearerToken();

        assertEquals("", bearerToken);
    }

    // ==================== getOidcToken 测试 (继承自 IDaaSCredentialProvider) ====================

    @Test
    @DisplayName("getOidcToken: 应返回与 getBearerToken 相同的值")
    void getOidcToken_ShouldReturnSameAsBearerToken() {
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn(TEST_ACCESS_TOKEN);
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        String oidcToken = provider.getOidcToken();
        String bearerToken = provider.getBearerToken();

        assertEquals(bearerToken, oidcToken);
    }

    @Test
    @DisplayName("getOidcToken: 当 credential 为 null 时应返回 null")
    void getOidcToken_WhenCredentialIsNull_ShouldReturnNull() {
        StaticIDaaSCredentialProvider provider = StaticIDaaSCredentialProvider.builder()
                .setCredential(null)
                .build();

        String oidcToken = provider.getOidcToken();

        assertNull(oidcToken);
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口实现: 应实现 IDaaSCredentialProvider 接口")
    void implementation_ShouldImplementIDaaSCredentialProvider() {
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(createMockCredential());

        assertTrue(provider instanceof com.cloud_idaas.core.provider.IDaaSCredentialProvider);
    }

    // ==================== 线程安全测试 ====================

    @Test
    @DisplayName("线程安全: 多线程并发获取 credential 应返回相同实例")
    void threadSafety_ConcurrentAccess_ShouldReturnSameCredential() throws InterruptedException {
        IDaaSCredential credential = createMockCredential();
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        final int threadCount = 10;
        final IDaaSCredential[] results = new IDaaSCredential[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = provider.getCredential();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (IDaaSCredential result : results) {
            assertEquals(credential, result);
        }
    }

    // ==================== 特殊值测试 ====================

    @Test
    @DisplayName("特殊值: credential 包含特殊字符的 access token")
    void specialValue_AccessTokenWithSpecialCharacters() {
        String specialToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.signature";
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn(specialToken);
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        assertEquals(specialToken, provider.getBearerToken());
    }

    @Test
    @DisplayName("特殊值: credential 包含长 access token")
    void specialValue_LongAccessToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longToken = sb.toString();
        IDaaSCredential credential = createMockCredential();
        when(credential.getAccessToken()).thenReturn(longToken);
        StaticIDaaSCredentialProvider provider = createProviderWithCredential(credential);

        assertEquals(longToken, provider.getBearerToken());
        assertEquals(1000, provider.getBearerToken().length());
    }

    // ==================== 辅助方法 ====================

    private StaticIDaaSCredentialProvider createProviderWithCredential(IDaaSCredential credential) {
        return StaticIDaaSCredentialProvider.builder()
                .setCredential(credential)
                .build();
    }

    private IDaaSCredential createMockCredential() {
        IDaaSCredential credential = mock(IDaaSCredential.class);
        when(credential.getAccessToken()).thenReturn(TEST_ACCESS_TOKEN);
        when(credential.getIdToken()).thenReturn(TEST_ID_TOKEN);
        when(credential.getRefreshToken()).thenReturn(TEST_REFRESH_TOKEN);
        when(credential.getTokenType()).thenReturn(TEST_TOKEN_TYPE);
        when(credential.getIssuedTokenType()).thenReturn(TEST_ISSUED_TOKEN_TYPE);
        return credential;
    }
}
