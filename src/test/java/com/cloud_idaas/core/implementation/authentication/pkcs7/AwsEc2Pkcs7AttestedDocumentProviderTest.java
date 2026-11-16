package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AwsEc2Pkcs7AttestedDocumentProvider 单元测试
 */
class AwsEc2Pkcs7AttestedDocumentProviderTest {

    private static final long DEFAULT_TOKEN_TTL_SECONDS = 21600L;

    @BeforeEach
    void setUp() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);
    }

    private IDaaSClientConfig createValidTestConfig() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIdaasInstanceId("test-instance-id");
        config.setClientId("test-client-id");
        config.setScope("test-audience|test-scope");
        config.setIssuer("https://test.idaas.example.com");
        config.setTokenEndpoint("https://test.idaas.example.com/token");
        config.setDeviceAuthorizationEndpoint("https://test.idaas.example.com/device");
        config.setDeveloperApiEndpoint("https://test.idaas.example.com/api");
        config.setOpenApiEndpoint("https://test.idaas.example.com/openapi");

        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setIdentityType(AuthenticationIdentityEnum.CLIENT);
        authnConfig.setAuthnMethod(TokenAuthnMethod.NONE);
        config.setAuthnConfiguration(authnConfig);

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(5000);
        httpConfig.setReadTimeout(10000);
        config.setHttpConfiguration(httpConfig);

        return config;
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("旧版无参构造器应继续可用，并使用默认参数")
    void legacyConstructor_ShouldUseDefaults() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();
        assertEquals(DEFAULT_TOKEN_TTL_SECONDS, provider.getTokenTtlSeconds());
        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 使用默认参数应成功创建实例")
    void builder_WithDefaults_ShouldCreateInstance() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .build();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("Builder: 默认 tokenTtlSeconds 应为 21600")
    void builder_DefaultTokenTtlSeconds_ShouldBe21600() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .build();

        assertEquals(DEFAULT_TOKEN_TTL_SECONDS, provider.getTokenTtlSeconds());
    }

    @Test
    @DisplayName("Builder: 设置有效的 tokenTtlSeconds 应成功")
    void builder_WithValidTokenTtlSeconds_ShouldSetValue() {
        long customTtl = 3600L;
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(customTtl)
                .build();

        assertNotNull(provider);
        assertEquals(customTtl, provider.getTokenTtlSeconds());
    }

    @Test
    @DisplayName("Builder: tokenTtlSeconds 为 0 应抛出 IllegalArgumentException")
    void builder_WithZeroTokenTtlSeconds_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AwsEc2Pkcs7AttestedDocumentProvider.builder()
                        .tokenTtlSeconds(0)
                        .build()
        );

        assertEquals("tokenTtlSeconds must be between 1 and 21600", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: tokenTtlSeconds 为负数应抛出 IllegalArgumentException")
    void builder_WithNegativeTokenTtlSeconds_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AwsEc2Pkcs7AttestedDocumentProvider.builder()
                        .tokenTtlSeconds(-1)
                        .build()
        );

        assertEquals("tokenTtlSeconds must be between 1 and 21600", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: tokenTtlSeconds 大于 21600 应抛出 IllegalArgumentException")
    void builder_WithTooLargeTokenTtlSeconds_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AwsEc2Pkcs7AttestedDocumentProvider.builder()
                        .tokenTtlSeconds(21601)
                        .build()
        );

        assertEquals("tokenTtlSeconds must be between 1 and 21600", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 边界值 1 应成功")
    void builder_WithBoundaryMinValue_ShouldSucceed() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(1)
                .build();

        assertNotNull(provider);
        assertEquals(1L, provider.getTokenTtlSeconds());
    }

    @Test
    @DisplayName("Builder: 边界值 21600 应成功")
    void builder_WithBoundaryMaxValue_ShouldSucceed() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(21600)
                .build();

        assertNotNull(provider);
        assertEquals(21600L, provider.getTokenTtlSeconds());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 Pkcs7AttestedDocumentProvider 接口")
    void interface_ShouldImplementPkcs7AttestedDocumentProvider() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .build();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider);
    }

    @Test
    @DisplayName("继承: 应继承 AbstractRefreshedCredentialProvider")
    void inheritance_ShouldExtendAbstractRefreshedCredentialProvider() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .build();

        assertTrue(provider instanceof com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider);
    }

    // ==================== Builder 方法链测试 ====================

    @Test
    @DisplayName("Builder: 方法链应返回正确的 Builder 类型")
    void builder_MethodChaining_ShouldReturnCorrectBuilderType() {
        AwsEc2Pkcs7AttestedDocumentProvider.AwsEc2Pkcs7AttestedDocumentProviderBuilder builder =
                AwsEc2Pkcs7AttestedDocumentProvider.builder()
                        .tokenTtlSeconds(3600L);

        assertNotNull(builder);
        AwsEc2Pkcs7AttestedDocumentProvider provider = builder.build();
        assertNotNull(provider);
        assertEquals(3600L, provider.getTokenTtlSeconds());
    }

    @Test
    @DisplayName("Builder: 多次设置同一属性应使用最后一次的值")
    void builder_SetPropertyMultipleTimes_ShouldUseLastValue() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(100)
                .tokenTtlSeconds(200)
                .tokenTtlSeconds(300)
                .build();

        assertEquals(300L, provider.getTokenTtlSeconds());
    }

    // ==================== 多个实例测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护自己的属性")
    void multipleInstances_ShouldMaintainIndependentProperties() {
        AwsEc2Pkcs7AttestedDocumentProvider provider1 = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(3600)
                .build();

        AwsEc2Pkcs7AttestedDocumentProvider provider2 = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .tokenTtlSeconds(7200)
                .build();

        assertEquals(3600L, provider1.getTokenTtlSeconds());
        assertEquals(7200L, provider2.getTokenTtlSeconds());
    }

    // ==================== asyncCredentialUpdateEnabled 测试 ====================

    @Test
    @DisplayName("Builder: 设置 asyncCredentialUpdateEnabled 为 true")
    void builder_SetAsyncCredentialUpdateEnabledTrue_ShouldBeTrue() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .asyncCredentialUpdateEnabled(true)
                .build();

        assertTrue(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 默认 asyncCredentialUpdateEnabled 应为 false")
    void builder_DefaultAsyncCredentialUpdateEnabled_ShouldBeFalse() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                .build();

        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }
}
