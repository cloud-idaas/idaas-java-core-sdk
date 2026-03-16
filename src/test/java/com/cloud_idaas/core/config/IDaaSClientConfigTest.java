package com.cloud_idaas.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IDaaSClientConfig 单元测试
 */
class IDaaSClientConfigTest {

    private IDaaSClientConfig config;

    @BeforeEach
    void setUp() {
        config = new IDaaSClientConfig();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: scope 默认应为 'urn:cloud:idaas:pam|.all'")
    void defaultValue_Scope_ShouldBeDefault() {
        assertEquals("urn:cloud:idaas:pam|.all", config.getScope());
    }

    @Test
    @DisplayName("默认值: httpConfiguration 默认应为非空实例")
    void defaultValue_HttpConfiguration_ShouldBeNonNull() {
        assertNotNull(config.getHttpConfiguration());
    }

    @Test
    @DisplayName("默认值: 其他字符串字段默认应为 null")
    void defaultValue_OtherStringFields_ShouldBeNull() {
        assertNull(config.getIdaasInstanceId());
        assertNull(config.getClientId());
        assertNull(config.getIssuer());
        assertNull(config.getTokenEndpoint());
        assertNull(config.getDeviceAuthorizationEndpoint());
        assertNull(config.getDeveloperApiEndpoint());
        assertNull(config.getOpenApiEndpoint());
        assertNull(config.getAuthnConfiguration());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 idaasInstanceId 应正确")
    void setter_IdaasInstanceId_ShouldBeSet() {
        config.setIdaasInstanceId("instance-123");
        assertEquals("instance-123", config.getIdaasInstanceId());
    }

    @Test
    @DisplayName("Setter: 设置 clientId 应正确")
    void setter_ClientId_ShouldBeSet() {
        config.setClientId("client-456");
        assertEquals("client-456", config.getClientId());
    }

    @Test
    @DisplayName("Setter: 设置 scope 应正确")
    void setter_Scope_ShouldBeSet() {
        config.setScope("custom-scope");
        assertEquals("custom-scope", config.getScope());
    }

    @Test
    @DisplayName("Setter: 设置 issuer 应正确")
    void setter_Issuer_ShouldBeSet() {
        config.setIssuer("https://idaas.example.com");
        assertEquals("https://idaas.example.com", config.getIssuer());
    }

    @Test
    @DisplayName("Setter: 设置 tokenEndpoint 应正确")
    void setter_TokenEndpoint_ShouldBeSet() {
        config.setTokenEndpoint("https://idaas.example.com/token");
        assertEquals("https://idaas.example.com/token", config.getTokenEndpoint());
    }

    @Test
    @DisplayName("Setter: 设置 deviceAuthorizationEndpoint 应正确")
    void setter_DeviceAuthorizationEndpoint_ShouldBeSet() {
        config.setDeviceAuthorizationEndpoint("https://idaas.example.com/device");
        assertEquals("https://idaas.example.com/device", config.getDeviceAuthorizationEndpoint());
    }

    @Test
    @DisplayName("Setter: 设置 developerApiEndpoint 应正确")
    void setter_DeveloperApiEndpoint_ShouldBeSet() {
        config.setDeveloperApiEndpoint("https://api.example.com");
        assertEquals("https://api.example.com", config.getDeveloperApiEndpoint());
    }

    @Test
    @DisplayName("Setter: 设置 openApiEndpoint 应正确")
    void setter_OpenApiEndpoint_ShouldBeSet() {
        config.setOpenApiEndpoint("https://openapi.example.com");
        assertEquals("https://openapi.example.com", config.getOpenApiEndpoint());
    }

    @Test
    @DisplayName("Setter: 设置 authnConfiguration 应正确")
    void setter_AuthnConfiguration_ShouldBeSet() {
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        config.setAuthnConfiguration(authnConfig);
        assertEquals(authnConfig, config.getAuthnConfiguration());
    }

    @Test
    @DisplayName("Setter: 设置 httpConfiguration 应正确")
    void setter_HttpConfiguration_ShouldBeSet() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(3000);
        config.setHttpConfiguration(httpConfig);
        assertEquals(3000, config.getHttpConfiguration().getConnectTimeout());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置 scope 为 null 应正确")
    void nullValue_Scope_ShouldBeSet() {
        config.setScope(null);
        assertNull(config.getScope());
    }

    @Test
    @DisplayName("null值: 设置 httpConfiguration 为 null 应正确")
    void nullValue_HttpConfiguration_ShouldBeSet() {
        config.setHttpConfiguration(null);
        assertNull(config.getHttpConfiguration());
    }

    @Test
    @DisplayName("null值: 设置 authnConfiguration 为 null 应正确")
    void nullValue_AuthnConfiguration_ShouldBeSet() {
        config.setAuthnConfiguration(new IdentityAuthenticationConfiguration());
        config.setAuthnConfiguration(null);
        assertNull(config.getAuthnConfiguration());
    }

    // ==================== assign 方法测试 ====================

    @Test
    @DisplayName("assign: 从非空对象赋值应正确")
    void assign_NonNullOther_ShouldCopyAllFields() {
        IDaaSClientConfig other = new IDaaSClientConfig();
        other.setIdaasInstanceId("instance-123");
        other.setClientId("client-456");
        other.setScope("custom-scope");
        other.setIssuer("https://idaas.example.com");
        other.setTokenEndpoint("https://idaas.example.com/token");
        other.setDeviceAuthorizationEndpoint("https://idaas.example.com/device");
        other.setDeveloperApiEndpoint("https://api.example.com");
        other.setOpenApiEndpoint("https://openapi.example.com");

        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setPluginName("test-plugin");
        other.setAuthnConfiguration(authnConfig);

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(3000);
        other.setHttpConfiguration(httpConfig);

        config.assign(other);

        assertEquals("instance-123", config.getIdaasInstanceId());
        assertEquals("client-456", config.getClientId());
        assertEquals("custom-scope", config.getScope());
        assertEquals("https://idaas.example.com", config.getIssuer());
        assertEquals("https://idaas.example.com/token", config.getTokenEndpoint());
        assertEquals("https://idaas.example.com/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://api.example.com", config.getDeveloperApiEndpoint());
        assertEquals("https://openapi.example.com", config.getOpenApiEndpoint());
        assertNotNull(config.getAuthnConfiguration());
        assertEquals("test-plugin", config.getAuthnConfiguration().getPluginName());
        assertNotNull(config.getHttpConfiguration());
        assertEquals(3000, config.getHttpConfiguration().getConnectTimeout());
    }

    @Test
    @DisplayName("assign: 从 null 赋值不应改变当前对象")
    void assign_NullOther_ShouldNotChangeCurrentObject() {
        config.setIdaasInstanceId("original-instance");
        config.setClientId("original-client");

        config.assign(null);

        assertEquals("original-instance", config.getIdaasInstanceId());
        assertEquals("original-client", config.getClientId());
    }

    @Test
    @DisplayName("assign: authnConfiguration 为 null 时应正确处理")
    void assign_NullAuthnConfiguration_ShouldBeHandled() {
        IDaaSClientConfig other = new IDaaSClientConfig();
        other.setIdaasInstanceId("instance-123");
        other.setAuthnConfiguration(null);

        config.assign(other);

        assertNull(config.getAuthnConfiguration());
    }

    @Test
    @DisplayName("assign: httpConfiguration 为 null 时应正确处理")
    void assign_NullHttpConfiguration_ShouldBeHandled() {
        IDaaSClientConfig other = new IDaaSClientConfig();
        other.setIdaasInstanceId("instance-123");
        other.setHttpConfiguration(null);

        config.assign(other);

        assertNull(config.getHttpConfiguration());
    }

    @Test
    @DisplayName("assign: 赋值后修改源对象不应影响当前对象")
    void assign_ModifyingSource_ShouldNotAffectTarget() {
        IDaaSClientConfig other = new IDaaSClientConfig();
        other.setIdaasInstanceId("instance-123");
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(3000);
        other.setHttpConfiguration(httpConfig);

        config.assign(other);

        other.setIdaasInstanceId("modified-instance");
        other.getHttpConfiguration().setConnectTimeout(9999);

        assertEquals("instance-123", config.getIdaasInstanceId());
        assertEquals(3000, config.getHttpConfiguration().getConnectTimeout());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(config instanceof Serializable);
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        IDaaSClientConfig config1 = new IDaaSClientConfig();
        IDaaSClientConfig config2 = new IDaaSClientConfig();

        config1.setIdaasInstanceId("instance-1");
        config2.setIdaasInstanceId("instance-2");

        assertEquals("instance-1", config1.getIdaasInstanceId());
        assertEquals("instance-2", config2.getIdaasInstanceId());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        config.setIdaasInstanceId("instance-123");
        config.setClientId("client-456");
        config.setScope("full-scope");
        config.setIssuer("https://full.example.com");
        config.setTokenEndpoint("https://full.example.com/token");
        config.setDeviceAuthorizationEndpoint("https://full.example.com/device");
        config.setDeveloperApiEndpoint("https://full.example.com/api");
        config.setOpenApiEndpoint("https://full.example.com/openapi");

        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        config.setAuthnConfiguration(authnConfig);

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(5000);
        config.setHttpConfiguration(httpConfig);

        assertEquals("instance-123", config.getIdaasInstanceId());
        assertEquals("client-456", config.getClientId());
        assertEquals("full-scope", config.getScope());
        assertEquals("https://full.example.com", config.getIssuer());
        assertEquals("https://full.example.com/token", config.getTokenEndpoint());
        assertEquals("https://full.example.com/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://full.example.com/api", config.getDeveloperApiEndpoint());
        assertEquals("https://full.example.com/openapi", config.getOpenApiEndpoint());
        assertNotNull(config.getAuthnConfiguration());
        assertNotNull(config.getHttpConfiguration());
        assertEquals(5000, config.getHttpConfiguration().getConnectTimeout());
    }
}
