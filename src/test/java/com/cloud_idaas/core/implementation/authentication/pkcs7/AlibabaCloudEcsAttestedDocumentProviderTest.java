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
 * AlibabaCloudEcsAttestedDocumentProvider 单元测试
 */
class AlibabaCloudEcsAttestedDocumentProviderTest {

    private static final String TEST_INSTANCE_ID = "test-instance-id";
    private static final String TEST_URL_TEMPLATE = "http://test-metadata-server/pkcs7?audience=%s";
    private static final long DEFAULT_EFFECTIVE_SECONDS = 3600L;

    @BeforeEach
    void setUp() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);
    }

    /**
     * 创建有效的测试配置
     */
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
    @DisplayName("Builder: 使用必需参数应成功创建实例")
    void builder_WithRequiredParams_ShouldCreateInstance() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("Builder: idaasInstanceId 为空应抛出 IllegalArgumentException")
    void builder_WithEmptyInstanceId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId("")
                        .build()
        );

        assertEquals("idaasInstanceId cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: idaasInstanceId 为 null 应抛出 IllegalArgumentException")
    void builder_WithNullInstanceId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(null)
                        .build()
        );

        assertEquals("idaasInstanceId cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 设置自定义 metaServerPkcs7UrlTemplate 应成功")
    void builder_WithCustomUrlTemplate_ShouldSetValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .metaServerPkcs7UrlTemplate(TEST_URL_TEMPLATE)
                .build();

        assertNotNull(provider);
        assertEquals(TEST_URL_TEMPLATE, provider.getMetaServerUrlTemplate());
    }

    @Test
    @DisplayName("Builder: metaServerPkcs7UrlTemplate 为空应抛出 IllegalArgumentException")
    void builder_WithEmptyUrlTemplate_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(TEST_INSTANCE_ID)
                        .metaServerPkcs7UrlTemplate("")
                        .build()
        );

        assertEquals("metaServerUrl cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: metaServerPkcs7UrlTemplate 为 null 应抛出 IllegalArgumentException")
    void builder_WithNullUrlTemplate_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(TEST_INSTANCE_ID)
                        .metaServerPkcs7UrlTemplate(null)
                        .build()
        );

        assertEquals("metaServerUrl cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 设置有效的 defaultDocumentEffectiveSeconds 应成功")
    void builder_WithValidEffectiveSeconds_ShouldSetValue() {
        long customSeconds = 7200L;
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .defaultDocumentEffectiveSeconds(customSeconds)
                .build();

        assertNotNull(provider);
        assertEquals(customSeconds, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Builder: defaultDocumentEffectiveSeconds 小于等于 1200 应抛出 IllegalArgumentException")
    void builder_WithTooSmallEffectiveSeconds_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(TEST_INSTANCE_ID)
                        .defaultDocumentEffectiveSeconds(1200L)
                        .build()
        );

        assertEquals("defaultDocumentEffectiveSeconds must be greater than 1200 and less than 1314000", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: defaultDocumentEffectiveSeconds 大于 1314000 应抛出 IllegalArgumentException")
    void builder_WithTooLargeEffectiveSeconds_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(TEST_INSTANCE_ID)
                        .defaultDocumentEffectiveSeconds(1314001L)
                        .build()
        );

        assertEquals("defaultDocumentEffectiveSeconds must be greater than 1200 and less than 1314000", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 边界值 1201 应成功")
    void builder_WithBoundaryMinValue_ShouldSucceed() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .defaultDocumentEffectiveSeconds(1201L)
                .build();

        assertNotNull(provider);
        assertEquals(1201L, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Builder: 边界值 1313999 应成功")
    void builder_WithBoundaryMaxValue_ShouldSucceed() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .defaultDocumentEffectiveSeconds(1313999L)
                .build();

        assertNotNull(provider);
        assertEquals(1313999L, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Builder: 使用默认的 defaultDocumentEffectiveSeconds 应为 3600")
    void builder_DefaultEffectiveSeconds_ShouldBe3600() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertEquals(DEFAULT_EFFECTIVE_SECONDS, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Builder: 使用默认的 metaServerUrlTemplate 不应为空")
    void builder_DefaultUrlTemplate_ShouldNotBeNull() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertNotNull(provider.getMetaServerUrlTemplate());
        assertTrue(provider.getMetaServerUrlTemplate().contains("100.100.100.200"));
    }

    // ==================== Getter 和 Setter 测试 ====================

    @Test
    @DisplayName("Getter: getIdaasInstanceId 应返回正确的实例 ID")
    void getIdaasInstanceId_ShouldReturnCorrectValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertEquals(TEST_INSTANCE_ID, provider.getIdaasInstanceId());
    }

    @Test
    @DisplayName("Setter: setIdaasInstanceId 应更新实例 ID")
    void setIdaasInstanceId_ShouldUpdateValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        String newInstanceId = "new-instance-id";
        provider.setIdaasInstanceId(newInstanceId);

        assertEquals(newInstanceId, provider.getIdaasInstanceId());
    }

    @Test
    @DisplayName("Setter: setIdaasInstanceId 可设置为 null")
    void setIdaasInstanceId_ToNull_ShouldAccept() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        provider.setIdaasInstanceId(null);

        assertNull(provider.getIdaasInstanceId());
    }

    @Test
    @DisplayName("Getter: getDefaultDocumentEffectiveSeconds 应返回正确的值")
    void getDefaultDocumentEffectiveSeconds_ShouldReturnCorrectValue() {
        long customSeconds = 7200L;
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .defaultDocumentEffectiveSeconds(customSeconds)
                .build();

        assertEquals(customSeconds, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Setter: setDefaultDocumentEffectiveSeconds 应更新值")
    void setDefaultDocumentEffectiveSeconds_ShouldUpdateValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        long newSeconds = 7200L;
        provider.setDefaultDocumentEffectiveSeconds(newSeconds);

        assertEquals(newSeconds, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Getter: getSigningTime 初始值应大于 0")
    void getSigningTime_InitialValue_ShouldBeGreaterThanZero() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertTrue(provider.getSigningTime() > 0);
    }

    @Test
    @DisplayName("Setter: setSigningTime 应更新时间戳")
    void setSigningTime_ShouldUpdateValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        long newSigningTime = 1234567890L;
        provider.setSigningTime(newSigningTime);

        assertEquals(newSigningTime, provider.getSigningTime());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 Pkcs7AttestedDocumentProvider 接口")
    void interface_ShouldImplementPkcs7AttestedDocumentProvider() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider);
    }

    @Test
    @DisplayName("继承: 应继承 AbstractRefreshedCredentialProvider")
    void inheritance_ShouldExtendAbstractRefreshedCredentialProvider() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertTrue(provider instanceof com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider);
    }

    // ==================== Builder 方法链测试 ====================

    @Test
    @DisplayName("Builder: 方法链应返回正确的 Builder 类型")
    void builder_MethodChaining_ShouldReturnCorrectBuilderType() {
        AlibabaCloudEcsAttestedDocumentProvider.AlibabaCloudEcsAttestedDocumentProviderBuilder builder =
                AlibabaCloudEcsAttestedDocumentProvider.builder()
                        .idaasInstanceId(TEST_INSTANCE_ID)
                        .metaServerPkcs7UrlTemplate(TEST_URL_TEMPLATE)
                        .defaultDocumentEffectiveSeconds(7200L);

        assertNotNull(builder);
        AlibabaCloudEcsAttestedDocumentProvider provider = builder.build();
        assertNotNull(provider);
        assertEquals(TEST_INSTANCE_ID, provider.getIdaasInstanceId());
        assertEquals(TEST_URL_TEMPLATE, provider.getMetaServerUrlTemplate());
        assertEquals(7200L, provider.getDefaultDocumentEffectiveSeconds());
    }

    @Test
    @DisplayName("Builder: 多次设置同一属性应使用最后一次的值")
    void builder_SetPropertyMultipleTimes_ShouldUseLastValue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId("first-id")
                .idaasInstanceId("second-id")
                .idaasInstanceId("final-id")
                .build();

        assertEquals("final-id", provider.getIdaasInstanceId());
    }

    // ==================== 多个实例测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护自己的属性")
    void multipleInstances_ShouldMaintainIndependentProperties() {
        AlibabaCloudEcsAttestedDocumentProvider provider1 = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId("instance-1")
                .defaultDocumentEffectiveSeconds(3600L)
                .build();

        AlibabaCloudEcsAttestedDocumentProvider provider2 = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId("instance-2")
                .defaultDocumentEffectiveSeconds(7200L)
                .build();

        assertEquals("instance-1", provider1.getIdaasInstanceId());
        assertEquals("instance-2", provider2.getIdaasInstanceId());
        assertEquals(3600L, provider1.getDefaultDocumentEffectiveSeconds());
        assertEquals(7200L, provider2.getDefaultDocumentEffectiveSeconds());

        // 修改 provider1 不应影响 provider2
        provider1.setIdaasInstanceId("modified-instance");
        assertEquals("modified-instance", provider1.getIdaasInstanceId());
        assertEquals("instance-2", provider2.getIdaasInstanceId());
    }

    // ==================== asyncCredentialUpdateEnabled 测试 ====================

    @Test
    @DisplayName("Builder: 设置 asyncCredentialUpdateEnabled 为 true")
    void builder_SetAsyncCredentialUpdateEnabledTrue_ShouldBeTrue() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .asyncCredentialUpdateEnabled(true)
                .build();

        assertTrue(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Builder: 默认 asyncCredentialUpdateEnabled 应为 false")
    void builder_DefaultAsyncCredentialUpdateEnabled_ShouldBeFalse() {
        AlibabaCloudEcsAttestedDocumentProvider provider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                .idaasInstanceId(TEST_INSTANCE_ID)
                .build();

        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }
}
