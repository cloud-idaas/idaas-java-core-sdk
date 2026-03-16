package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.credential.IDaaSCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * IDaaSCredentialProvider 接口单元测试
 */
class IDaaSCredentialProviderTest {

    // ==================== getBearerToken 测试 ====================

    @Test
    @DisplayName("getBearerToken: 当 getCredential 返回有效凭证时应返回 accessToken")
    void getBearerToken_WithValidCredential_ShouldReturnAccessToken() {
        // 创建模拟的 IDaaSCredential
        IDaaSCredential mockCredential = mock(IDaaSCredential.class);
        when(mockCredential.getAccessToken()).thenReturn("test-access-token");

        // 创建模拟的 IDaaSCredentialProvider
        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getCredential()).thenReturn(mockCredential);
        when(provider.getBearerToken()).thenCallRealMethod();

        String result = provider.getBearerToken();

        assertEquals("test-access-token", result);
        verify(provider).getCredential();
    }

    @Test
    @DisplayName("getBearerToken: 当 getCredential 返回 null 时应返回 null")
    void getBearerToken_WithNullCredential_ShouldReturnNull() {
        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getCredential()).thenReturn(null);
        when(provider.getBearerToken()).thenCallRealMethod();

        String result = provider.getBearerToken();

        assertNull(result);
    }

    @Test
    @DisplayName("getBearerToken: 当 accessToken 为 null 时应返回 null")
    void getBearerToken_WithNullAccessToken_ShouldReturnNull() {
        IDaaSCredential mockCredential = mock(IDaaSCredential.class);
        when(mockCredential.getAccessToken()).thenReturn(null);

        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getCredential()).thenReturn(mockCredential);
        when(provider.getBearerToken()).thenCallRealMethod();

        String result = provider.getBearerToken();

        assertNull(result);
    }

    @Test
    @DisplayName("getBearerToken: 当 accessToken 为空字符串时应返回空字符串")
    void getBearerToken_WithEmptyAccessToken_ShouldReturnEmptyString() {
        IDaaSCredential mockCredential = mock(IDaaSCredential.class);
        when(mockCredential.getAccessToken()).thenReturn("");

        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getCredential()).thenReturn(mockCredential);
        when(provider.getBearerToken()).thenCallRealMethod();

        String result = provider.getBearerToken();

        assertEquals("", result);
    }

    // ==================== getOidcToken 测试 ====================

    @Test
    @DisplayName("getOidcToken: 默认实现应返回 getBearerToken 的结果")
    void getOidcToken_DefaultImplementation_ShouldReturnBearerToken() {
        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getBearerToken()).thenReturn("bearer-token");
        when(provider.getOidcToken()).thenCallRealMethod();

        String result = provider.getOidcToken();

        assertEquals("bearer-token", result);
        verify(provider).getBearerToken();
    }

    @Test
    @DisplayName("getOidcToken: 当 getBearerToken 返回 null 时 getOidcToken 也应返回 null")
    void getOidcToken_WhenBearerTokenNull_ShouldReturnNull() {
        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getBearerToken()).thenReturn(null);
        when(provider.getOidcToken()).thenCallRealMethod();

        String result = provider.getOidcToken();

        assertNull(result);
    }

    // ==================== 接口契约测试 ====================

    @Test
    @DisplayName("接口契约: IDaaSCredentialProvider 继承自 OidcTokenProvider")
    void interfaceContract_ShouldExtendOidcTokenProvider() {
        assertTrue(OidcTokenProvider.class.isAssignableFrom(IDaaSCredentialProvider.class));
    }

    @Test
    @DisplayName("接口契约: getCredential 是抽象方法")
    void interfaceContract_GetCredentialShouldBeAbstract() {
        // 验证 getCredential 方法存在
        try {
            IDaaSCredentialProvider.class.getMethod("getCredential");
        } catch (NoSuchMethodException e) {
            fail("getCredential method should exist");
        }
    }

    // ==================== 模拟实现测试 ====================

    @Test
    @DisplayName("模拟实现: 自定义实现应能正常工作")
    void mockImplementation_CustomImplementationShouldWork() {
        IDaaSCredentialProvider provider = () -> {
            IDaaSCredential credential = mock(IDaaSCredential.class);
            when(credential.getAccessToken()).thenReturn("custom-token");
            return credential;
        };

        assertEquals("custom-token", provider.getBearerToken());
    }

    @Test
    @DisplayName("模拟实现: 每次调用 getCredential 都可能返回不同结果")
    void mockImplementation_MultipleCallsMayReturnDifferentResults() {
        IDaaSCredential credential1 = mock(IDaaSCredential.class);
        when(credential1.getAccessToken()).thenReturn("token-1");

        IDaaSCredential credential2 = mock(IDaaSCredential.class);
        when(credential2.getAccessToken()).thenReturn("token-2");

        IDaaSCredentialProvider provider = mock(IDaaSCredentialProvider.class);
        when(provider.getCredential())
                .thenReturn(credential1)
                .thenReturn(credential2);
        when(provider.getBearerToken()).thenCallRealMethod();

        assertEquals("token-1", provider.getBearerToken());
        assertEquals("token-2", provider.getBearerToken());
    }
}
