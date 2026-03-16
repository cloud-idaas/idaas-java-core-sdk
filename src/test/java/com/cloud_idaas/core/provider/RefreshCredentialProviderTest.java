package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.cache.RefreshResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RefreshCredentialProvider 接口单元测试
 */
class RefreshCredentialProviderTest {

    // ==================== refreshCredential 测试 ====================

    @Test
    @DisplayName("refreshCredential: 应返回有效的 RefreshResult")
    void refreshCredential_ShouldReturnValidRefreshResult() {
        String credential = "test-credential";
        Instant staleTime = Instant.now().plusSeconds(3600);
        Instant prefetchTime = Instant.now().plusSeconds(3000);
        RefreshResult<String> refreshResult = new RefreshResult<>(credential, staleTime, prefetchTime);

        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<String> provider = mock(RefreshCredentialProvider.class);
        when(provider.refreshCredential()).thenReturn(refreshResult);

        RefreshResult<String> result = provider.refreshCredential();

        assertNotNull(result);
        assertEquals(credential, result.getValue());
        assertEquals(staleTime, result.getStaleTime());
        assertEquals(prefetchTime, result.getPrefetchTime());
    }

    @Test
    @DisplayName("refreshCredential: 可能返回 null")
    void refreshCredential_MayReturnNull() {
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<String> provider = mock(RefreshCredentialProvider.class);
        when(provider.refreshCredential()).thenReturn(null);

        RefreshResult<String> result = provider.refreshCredential();

        assertNull(result);
    }

    @Test
    @DisplayName("refreshCredential: 支持不同类型的凭证")
    void refreshCredential_ShouldSupportDifferentTypes() {
        // 测试 Integer 类型
        RefreshResult<Integer> intResult = new RefreshResult<>(123, Instant.now(), Instant.now());
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<Integer> intProvider = mock(RefreshCredentialProvider.class);
        when(intProvider.refreshCredential()).thenReturn(intResult);

        assertEquals(Integer.valueOf(123), intProvider.refreshCredential().getValue());

        // 测试自定义对象类型
        class CustomCredential {
            private final String data;
            CustomCredential(String data) { this.data = data; }
            String getData() { return data; }
        }

        CustomCredential custom = new CustomCredential("custom-data");
        RefreshResult<CustomCredential> customResult = new RefreshResult<>(custom, Instant.now(), Instant.now());
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<CustomCredential> customProvider = mock(RefreshCredentialProvider.class);
        when(customProvider.refreshCredential()).thenReturn(customResult);

        assertEquals("custom-data", customProvider.refreshCredential().getValue().getData());
    }

    // ==================== Lambda 实现测试 ====================

    @Test
    @DisplayName("Lambda 实现: 可以使用 Lambda 表达式实现")
    void lambdaImplementation_CanBeImplementedWithLambda() {
        RefreshCredentialProvider<String> provider = () -> {
            String credential = "lambda-credential";
            Instant staleTime = Instant.now().plusSeconds(3600);
            Instant prefetchTime = Instant.now().plusSeconds(3000);
            return new RefreshResult<>(credential, staleTime, prefetchTime);
        };

        RefreshResult<String> result = provider.refreshCredential();

        assertEquals("lambda-credential", result.getValue());
        assertNotNull(result.getStaleTime());
        assertNotNull(result.getPrefetchTime());
    }

    @Test
    @DisplayName("Lambda 实现: 可以使用方法引用实现")
    void lambdaImplementation_CanBeImplementedWithMethodReference() {
        RefreshCredentialProvider<String> provider = this::generateCredential;

        RefreshResult<String> result = provider.refreshCredential();

        assertEquals("method-ref-credential", result.getValue());
    }

    private RefreshResult<String> generateCredential() {
        return new RefreshResult<>(
                "method-ref-credential",
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(3000)
        );
    }

    // ==================== 多次调用测试 ====================

    @Test
    @DisplayName("多次调用: 每次调用可能返回不同的结果")
    void multipleCalls_MayReturnDifferentResults() {
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<String> provider = mock(RefreshCredentialProvider.class);

        RefreshResult<String> result1 = new RefreshResult<>("credential-1", Instant.now(), Instant.now());
        RefreshResult<String> result2 = new RefreshResult<>("credential-2", Instant.now(), Instant.now());

        when(provider.refreshCredential())
                .thenReturn(result1)
                .thenReturn(result2);

        assertEquals("credential-1", provider.refreshCredential().getValue());
        assertEquals("credential-2", provider.refreshCredential().getValue());
    }

    @Test
    @DisplayName("多次调用: 可能返回缓存的结果")
    void multipleCalls_MayReturnCachedResult() {
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<String> provider = mock(RefreshCredentialProvider.class);

        RefreshResult<String> cachedResult = new RefreshResult<>("cached-credential", Instant.now(), Instant.now());
        when(provider.refreshCredential()).thenReturn(cachedResult);

        assertSame(cachedResult, provider.refreshCredential());
        assertSame(cachedResult, provider.refreshCredential());

        verify(provider, times(2)).refreshCredential();
    }

    // ==================== RefreshResult Builder 测试 ====================

    @Test
    @DisplayName("RefreshResult Builder: 使用 Builder 创建 RefreshResult")
    void refreshResultBuilder_ShouldCreateRefreshResult() {
        String credential = "builder-credential";
        Instant staleTime = Instant.now().plusSeconds(3600);
        Instant prefetchTime = Instant.now().plusSeconds(3000);

        RefreshResult<String> result = RefreshResult.<String>builder(credential)
                .staleTime(staleTime)
                .prefetchTime(prefetchTime)
                .build();

        assertEquals(credential, result.getValue());
        assertEquals(staleTime, result.getStaleTime());
        assertEquals(prefetchTime, result.getPrefetchTime());
    }

    @Test
    @DisplayName("RefreshResult Builder: 可以只设置部分字段")
    void refreshResultBuilder_CanSetPartialFields() {
        String credential = "partial-credential";

        RefreshResult<String> result = RefreshResult.<String>builder(credential)
                .build();

        assertEquals(credential, result.getValue());
        assertNull(result.getStaleTime());
        assertNull(result.getPrefetchTime());
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("异常处理: 实现可能抛出运行时异常")
    void exceptionHandling_MayThrowRuntimeException() {
        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<String> provider = mock(RefreshCredentialProvider.class);
        when(provider.refreshCredential()).thenThrow(new RuntimeException("Refresh failed"));

        assertThrows(RuntimeException.class, provider::refreshCredential);
    }

    // ==================== 实际使用场景测试 ====================

    @Test
    @DisplayName("实际场景: 模拟 Token 刷新")
    void realScenario_TokenRefresh() {
        RefreshCredentialProvider<String> tokenRefresher = () -> {
            // 模拟从服务器获取新 Token
            String newToken = "new-access-token";
            Instant now = Instant.now();
            Instant staleTime = now.plusSeconds(3600); // 1小时后过期
            Instant prefetchTime = now.plusSeconds(3000); // 50分钟后预取

            return new RefreshResult<>(newToken, staleTime, prefetchTime);
        };

        RefreshResult<String> result = tokenRefresher.refreshCredential();

        assertEquals("new-access-token", result.getValue());
        assertTrue(result.getStaleTime().isAfter(Instant.now()));
        assertTrue(result.getPrefetchTime().isAfter(Instant.now()));
        assertTrue(result.getPrefetchTime().isBefore(result.getStaleTime()));
    }

    @Test
    @DisplayName("实际场景: 模拟凭证刷新失败")
    void realScenario_RefreshFailure() {
        RefreshCredentialProvider<String> failingRefresher = () -> {
            throw new RuntimeException("Failed to refresh credential: network error");
        };

        RuntimeException exception = assertThrows(RuntimeException.class, failingRefresher::refreshCredential);
        assertTrue(exception.getMessage().contains("Failed to refresh credential"));
    }

    @Test
    @DisplayName("实际场景: 模拟带重试的凭证刷新")
    void realScenario_RefreshWithRetry() {
        RefreshCredentialProvider<String> retryRefresher = new RefreshCredentialProvider<String>() {
            private int attemptCount = 0;

            @Override
            public RefreshResult<String> refreshCredential() {
                attemptCount++;
                if (attemptCount < 3) {
                    throw new RuntimeException("Temporary failure");
                }
                return new RefreshResult<>("success-after-retry", Instant.now(), Instant.now());
            }
        };

        // 前两次调用会失败
        assertThrows(RuntimeException.class, retryRefresher::refreshCredential);
        assertThrows(RuntimeException.class, retryRefresher::refreshCredential);

        // 第三次调用成功
        RefreshResult<String> result = retryRefresher.refreshCredential();
        assertEquals("success-after-retry", result.getValue());
    }

    // ==================== 泛型测试 ====================

    @Test
    @DisplayName("泛型: 支持复杂泛型类型")
    void generics_ShouldSupportComplexTypes() {
        class CredentialWrapper<T> {
            private final T credential;
            private final long timestamp;

            CredentialWrapper(T credential, long timestamp) {
                this.credential = credential;
                this.timestamp = timestamp;
            }
        }

        CredentialWrapper<String> wrapper = new CredentialWrapper<>("wrapped", System.currentTimeMillis());
        RefreshResult<CredentialWrapper<String>> result = new RefreshResult<>(wrapper, Instant.now(), Instant.now());

        @SuppressWarnings("unchecked")
        RefreshCredentialProvider<CredentialWrapper<String>> provider = mock(RefreshCredentialProvider.class);
        when(provider.refreshCredential()).thenReturn(result);

        assertEquals("wrapped", provider.refreshCredential().getValue().credential);
    }
}
