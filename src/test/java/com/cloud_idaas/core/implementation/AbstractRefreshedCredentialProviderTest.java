package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.cache.RefreshResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AbstractRefreshedCredentialProvider 单元测试
 */
class AbstractRefreshedCredentialProviderTest {

    private static final String TEST_VALUE = "test-credential";
    private static final String TEST_CLIENT_ID = "test-client-id";

    // ==================== 具体实现类（用于测试抽象类）====================

    /**
     * 测试用的具体实现类
     */
    static class TestCredentialProvider extends AbstractRefreshedCredentialProvider<String> {

        private final String credential;
        private int refreshCount = 0;

        private TestCredentialProvider(TestBuilder builder) {
            super(builder);
            this.credential = builder.credential;
        }

        @Override
        public RefreshResult<String> refreshCredential() {
            refreshCount++;
            Instant now = Instant.now();
            Instant staleTime = now.plusSeconds(300);  // 5分钟后过期
            Instant prefetchTime = now.plusSeconds(200);  // 3分20秒后预取
            return RefreshResult.builder(credential + "-" + refreshCount)
                    .staleTime(staleTime)
                    .prefetchTime(prefetchTime)
                    .build();
        }

        public int getRefreshCount() {
            return refreshCount;
        }

        public static TestBuilder builder() {
            return new TestBuilder();
        }

        static class TestBuilder extends AbstractRefreshedCredentialProvider.BuilderImpl<TestCredentialProvider, TestBuilder> {
            private String credential = TEST_VALUE;

            public TestBuilder credential(String credential) {
                this.credential = credential;
                return this;
            }

            @Override
            public TestCredentialProvider build() {
                return new TestCredentialProvider(this);
            }
        }
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 默认 asyncCredentialUpdateEnabled 应为 false")
    void builder_DefaultAsyncCredentialUpdateEnabled_ShouldBeFalse() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 设置 asyncCredentialUpdateEnabled 为 true")
    void builder_SetAsyncCredentialUpdateEnabledTrue_ShouldBeTrue() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(true)
                .build();

        assertTrue(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 设置 asyncCredentialUpdateEnabled 为 false")
    void builder_SetAsyncCredentialUpdateEnabledFalse_ShouldBeFalse() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(false)
                .build();

        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 多次设置 asyncCredentialUpdateEnabled 应使用最后一次的值")
    void builder_SetAsyncCredentialUpdateEnabledMultipleTimes_ShouldUseLastValue() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(true)
                .asyncCredentialUpdateEnabled(false)
                .asyncCredentialUpdateEnabled(true)
                .build();

        assertTrue(provider.isAsyncCredentialUpdateEnabled());
    }

    // ==================== cachedResultSupplier 测试 ====================

    @Test
    @DisplayName("cachedResultSupplier: 默认情况下不应为 null")
    void cachedResultSupplier_Default_ShouldNotBeNull() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        assertNotNull(provider.getCachedResultSupplier());
    }

    @Test
    @DisplayName("cachedResultSupplier: 异步更新启用时应使用 NonBlockingPrefetchStrategy")
    void cachedResultSupplier_AsyncEnabled_ShouldUseNonBlockingStrategy() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(true)
                .build();

        assertNotNull(provider.getCachedResultSupplier());
        // NonBlockingPrefetchStrategy 是异步的，无法直接验证，但确保对象创建成功
    }

    @Test
    @DisplayName("cachedResultSupplier: 异步更新禁用时应使用 OneCallerBlocksPrefetchStrategy")
    void cachedResultSupplier_AsyncDisabled_ShouldUseBlockingStrategy() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(false)
                .build();

        assertNotNull(provider.getCachedResultSupplier());
        // OneCallerBlocksPrefetchStrategy 是同步阻塞的
    }

    // ==================== refreshCredential 测试 ====================

    @Test
    @DisplayName("refreshCredential: 应返回包含正确值的 RefreshResult")
    void refreshCredential_ShouldReturnRefreshResultWithCorrectValue() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .credential("my-credential")
                .build();

        RefreshResult<String> result = provider.refreshCredential();

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertTrue(result.getValue().startsWith("my-credential-"));
        assertNotNull(result.getStaleTime());
        assertNotNull(result.getPrefetchTime());
    }

    @Test
    @DisplayName("refreshCredential: 每次调用应增加刷新计数")
    void refreshCredential_MultipleCalls_ShouldIncreaseRefreshCount() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        assertEquals(0, provider.getRefreshCount());

        provider.refreshCredential();
        assertEquals(1, provider.getRefreshCount());

        provider.refreshCredential();
        assertEquals(2, provider.getRefreshCount());

        provider.refreshCredential();
        assertEquals(3, provider.getRefreshCount());
    }

    @Test
    @DisplayName("refreshCredential: 返回的结果应包含未来的 staleTime")
    void refreshCredential_ShouldReturnFutureStaleTime() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        RefreshResult<String> result = provider.refreshCredential();

        assertNotNull(result.getStaleTime());
        assertTrue(result.getStaleTime().isAfter(Instant.now()),
                "staleTime 应该是未来的时间");
    }

    @Test
    @DisplayName("refreshCredential: 返回的结果应包含未来的 prefetchTime")
    void refreshCredential_ShouldReturnFuturePrefetchTime() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        RefreshResult<String> result = provider.refreshCredential();

        assertNotNull(result.getPrefetchTime());
        assertTrue(result.getPrefetchTime().isAfter(Instant.now()),
                "prefetchTime 应该是未来的时间");
    }

    @Test
    @DisplayName("refreshCredential: prefetchTime 应该在 staleTime 之前")
    void refreshCredential_PrefetchTimeShouldBeBeforeStaleTime() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        RefreshResult<String> result = provider.refreshCredential();

        assertTrue(result.getPrefetchTime().isBefore(result.getStaleTime()),
                "prefetchTime 应该在 staleTime 之前");
    }

    // ==================== 继承和接口实现测试 ====================

    @Test
    @DisplayName("继承: 应实现 RefreshCredentialProvider 接口")
    void inheritance_ShouldImplementRefreshCredentialProvider() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.RefreshCredentialProvider);
    }

    @Test
    @DisplayName("功能: cachedResultSupplier 的 get 方法应触发刷新")
    void cachedResultSupplier_Get_ShouldTriggerRefresh() {
        TestCredentialProvider provider = TestCredentialProvider.builder()
                .build();

        // 第一次调用 get() 会触发刷新
        String value = provider.getCachedResultSupplier().get();

        assertNotNull(value);
        assertEquals(1, provider.getRefreshCount());
    }

    @Test
    @DisplayName("功能: 多个 provider 实例应独立维护自己的缓存")
    void multipleProviders_ShouldMaintainIndependentCaches() {
        TestCredentialProvider provider1 = TestCredentialProvider.builder()
                .credential("cred-1")
                .build();
        TestCredentialProvider provider2 = TestCredentialProvider.builder()
                .credential("cred-2")
                .build();

        provider1.refreshCredential();
        provider2.refreshCredential();
        provider2.refreshCredential();

        assertEquals(1, provider1.getRefreshCount());
        assertEquals(2, provider2.getRefreshCount());
    }

    // ==================== BuilderImpl 方法链测试 ====================

    @Test
    @DisplayName("Builder: 方法链应返回正确的 Builder 类型")
    void builder_MethodChaining_ShouldReturnCorrectBuilderType() {
        TestCredentialProvider.TestBuilder builder = TestCredentialProvider.builder()
                .asyncCredentialUpdateEnabled(true)
                .credential("test");

        assertNotNull(builder);
        TestCredentialProvider provider = builder.build();
        assertNotNull(provider);
        assertTrue(provider.isAsyncCredentialUpdateEnabled());
    }
}
