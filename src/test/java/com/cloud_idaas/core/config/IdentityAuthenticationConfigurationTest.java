package com.cloud_idaas.core.config;

import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IdentityAuthenticationConfiguration 单元测试
 */
class IdentityAuthenticationConfigurationTest {

    private IdentityAuthenticationConfiguration config;

    @BeforeEach
    void setUp() {
        config = new IdentityAuthenticationConfiguration();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: identityType 默认应为 CLIENT")
    void defaultValue_IdentityType_ShouldBeClient() {
        assertEquals(AuthenticationIdentityEnum.CLIENT, config.getIdentityType());
    }

    @Test
    @DisplayName("默认值: authnMethod 默认应为 NONE")
    void defaultValue_AuthnMethod_ShouldBeNone() {
        assertEquals(TokenAuthnMethod.NONE, config.getAuthnMethod());
    }

    @Test
    @DisplayName("默认值: humanAuthenticateClientId 默认应为 'iap_developer'")
    void defaultValue_HumanAuthenticateClientId_ShouldBeDefault() {
        assertEquals("iap_developer", config.getHumanAuthenticateClientId());
    }

    @Test
    @DisplayName("默认值: 其他字符串字段默认应为 null")
    void defaultValue_OtherStringFields_ShouldBeNull() {
        assertNull(config.getClientSecretEnvVarName());
        assertNull(config.getPrivateKeyEnvVarName());
        assertNull(config.getApplicationFederatedCredentialName());
        assertNull(config.getClientDeployEnvironment());
        assertNull(config.getOidcTokenFilePathEnvVarName());
        assertNull(config.getOidcTokenFilePath());
        assertNull(config.getClientX509Certificate());
        assertNull(config.getX509CertChains());
        assertNull(config.getPluginName());
    }

    // ==================== Getter/Setter 测试 - identityType ====================

    @Test
    @DisplayName("Setter: 设置 identityType 为 CLIENT 应正确")
    void setter_IdentityTypeClient_ShouldBeSet() {
        config.setIdentityType(AuthenticationIdentityEnum.CLIENT);
        assertEquals(AuthenticationIdentityEnum.CLIENT, config.getIdentityType());
    }

    @Test
    @DisplayName("Setter: 设置 identityType 为 HUMAN 应正确")
    void setter_IdentityTypeHuman_ShouldBeSet() {
        config.setIdentityType(AuthenticationIdentityEnum.HUMAN);
        assertEquals(AuthenticationIdentityEnum.HUMAN, config.getIdentityType());
    }

    // ==================== Getter/Setter 测试 - authnMethod ====================

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 CLIENT_SECRET_POST 应正确")
    void setter_AuthnMethodClientSecretPost_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
        assertEquals(TokenAuthnMethod.CLIENT_SECRET_POST, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 CLIENT_SECRET_BASIC 应正确")
    void setter_AuthnMethodClientSecretBasic_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_BASIC);
        assertEquals(TokenAuthnMethod.CLIENT_SECRET_BASIC, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 CLIENT_SECRET_JWT 应正确")
    void setter_AuthnMethodClientSecretJwt_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_JWT);
        assertEquals(TokenAuthnMethod.CLIENT_SECRET_JWT, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 PRIVATE_KEY_JWT 应正确")
    void setter_AuthnMethodPrivateKeyJwt_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT);
        assertEquals(TokenAuthnMethod.PRIVATE_KEY_JWT, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 PKCS7 应正确")
    void setter_AuthnMethodPkcs7_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.PKCS7);
        assertEquals(TokenAuthnMethod.PKCS7, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 PCA 应正确")
    void setter_AuthnMethodPca_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.PCA);
        assertEquals(TokenAuthnMethod.PCA, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 OIDC 应正确")
    void setter_AuthnMethodOidc_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.OIDC);
        assertEquals(TokenAuthnMethod.OIDC, config.getAuthnMethod());
    }

    @Test
    @DisplayName("Setter: 设置 authnMethod 为 PLUGIN 应正确")
    void setter_AuthnMethodPlugin_ShouldBeSet() {
        config.setAuthnMethod(TokenAuthnMethod.PLUGIN);
        assertEquals(TokenAuthnMethod.PLUGIN, config.getAuthnMethod());
    }

    // ==================== Getter/Setter 测试 - 字符串字段 ====================

    @Test
    @DisplayName("Setter: 设置 clientSecretEnvVarName 应正确")
    void setter_ClientSecretEnvVarName_ShouldBeSet() {
        config.setClientSecretEnvVarName("MY_SECRET");
        assertEquals("MY_SECRET", config.getClientSecretEnvVarName());
    }

    @Test
    @DisplayName("Setter: 设置 privateKeyEnvVarName 应正确")
    void setter_PrivateKeyEnvVarName_ShouldBeSet() {
        config.setPrivateKeyEnvVarName("MY_PRIVATE_KEY");
        assertEquals("MY_PRIVATE_KEY", config.getPrivateKeyEnvVarName());
    }

    @Test
    @DisplayName("Setter: 设置 applicationFederatedCredentialName 应正确")
    void setter_ApplicationFederatedCredentialName_ShouldBeSet() {
        config.setApplicationFederatedCredentialName("my-federated-cred");
        assertEquals("my-federated-cred", config.getApplicationFederatedCredentialName());
    }

    @Test
    @DisplayName("Setter: 设置 oidcTokenFilePathEnvVarName 应正确")
    void setter_OidcTokenFilePathEnvVarName_ShouldBeSet() {
        config.setOidcTokenFilePathEnvVarName("OIDC_TOKEN_PATH");
        assertEquals("OIDC_TOKEN_PATH", config.getOidcTokenFilePathEnvVarName());
    }

    @Test
    @DisplayName("Setter: 设置 oidcTokenFilePath 应正确")
    void setter_OidcTokenFilePath_ShouldBeSet() {
        config.setOidcTokenFilePath("/path/to/token");
        assertEquals("/path/to/token", config.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("Setter: 设置 clientX509Certificate 应正确")
    void setter_ClientX509Certificate_ShouldBeSet() {
        config.setClientX509Certificate("cert-content");
        assertEquals("cert-content", config.getClientX509Certificate());
    }

    @Test
    @DisplayName("Setter: 设置 x509CertChains 应正确")
    void setter_X509CertChains_ShouldBeSet() {
        config.setX509CertChains("chain-content");
        assertEquals("chain-content", config.getX509CertChains());
    }

    @Test
    @DisplayName("Setter: 设置 pluginName 应正确")
    void setter_PluginName_ShouldBeSet() {
        config.setPluginName("my-plugin");
        assertEquals("my-plugin", config.getPluginName());
    }

    @Test
    @DisplayName("Setter: 设置 humanAuthenticateClientId 应正确")
    void setter_HumanAuthenticateClientId_ShouldBeSet() {
        config.setHumanAuthenticateClientId("custom_client");
        assertEquals("custom_client", config.getHumanAuthenticateClientId());
    }

    // ==================== Getter/Setter 测试 - clientDeployEnvironment ====================

    @Test
    @DisplayName("Setter: 设置 clientDeployEnvironment 为 COMMON 应正确")
    void setter_ClientDeployEnvironmentCommon_ShouldBeSet() {
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.COMMON);
        assertEquals(ClientDeployEnvironmentEnum.COMMON, config.getClientDeployEnvironment());
    }

    @Test
    @DisplayName("Setter: 设置 clientDeployEnvironment 为 KUBERNETES 应正确")
    void setter_ClientDeployEnvironmentKubernetes_ShouldBeSet() {
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.KUBERNETES);
        assertEquals(ClientDeployEnvironmentEnum.KUBERNETES, config.getClientDeployEnvironment());
    }

    @Test
    @DisplayName("Setter: 设置 clientDeployEnvironment 为 ALIBABA_CLOUD_ECS 应正确")
    void setter_ClientDeployEnvironmentAlibabaCloudEcs_ShouldBeSet() {
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS);
        assertEquals(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS, config.getClientDeployEnvironment());
    }

    @Test
    @DisplayName("Setter: 设置 clientDeployEnvironment 为 AWS_EC2 应正确")
    void setter_ClientDeployEnvironmentAwsEc2_ShouldBeSet() {
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.AWS_EC2);
        assertEquals(ClientDeployEnvironmentEnum.AWS_EC2, config.getClientDeployEnvironment());
    }

    @Test
    @DisplayName("Setter: 设置 clientDeployEnvironment 为 CUSTOM 应正确")
    void setter_ClientDeployEnvironmentCustom_ShouldBeSet() {
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.CUSTOM);
        assertEquals(ClientDeployEnvironmentEnum.CUSTOM, config.getClientDeployEnvironment());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置字符串字段为 null 应正确")
    void nullValue_StringFields_ShouldBeSet() {
        config.setClientSecretEnvVarName("test");
        config.setClientSecretEnvVarName(null);
        assertNull(config.getClientSecretEnvVarName());

        config.setPrivateKeyEnvVarName(null);
        assertNull(config.getPrivateKeyEnvVarName());

        config.setPluginName(null);
        assertNull(config.getPluginName());
    }

    // ==================== copy 方法测试 ====================

    @Test
    @DisplayName("copy: 复制非空对象应返回正确的副本")
    void copy_NonNullSource_ShouldReturnCorrectCopy() {
        IdentityAuthenticationConfiguration source = new IdentityAuthenticationConfiguration();
        source.setIdentityType(AuthenticationIdentityEnum.HUMAN);
        source.setAuthnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT);
        source.setClientSecretEnvVarName("SECRET_VAR");
        source.setPrivateKeyEnvVarName("KEY_VAR");
        source.setApplicationFederatedCredentialName("federated-cred");
        source.setClientDeployEnvironment(ClientDeployEnvironmentEnum.KUBERNETES);
        source.setOidcTokenFilePathEnvVarName("OIDC_PATH_VAR");
        source.setOidcTokenFilePath("/path/to/oidc/token");
        source.setClientX509Certificate("cert-content");
        source.setX509CertChains("chain-content");
        source.setPluginName("test-plugin");
        source.setHumanAuthenticateClientId("custom_client");

        IdentityAuthenticationConfiguration copy = IdentityAuthenticationConfiguration.copy(source);

        assertNotNull(copy);
        assertEquals(AuthenticationIdentityEnum.HUMAN, copy.getIdentityType());
        assertEquals(TokenAuthnMethod.PRIVATE_KEY_JWT, copy.getAuthnMethod());
        assertEquals("SECRET_VAR", copy.getClientSecretEnvVarName());
        assertEquals("KEY_VAR", copy.getPrivateKeyEnvVarName());
        assertEquals("federated-cred", copy.getApplicationFederatedCredentialName());
        assertEquals(ClientDeployEnvironmentEnum.KUBERNETES, copy.getClientDeployEnvironment());
        assertEquals("OIDC_PATH_VAR", copy.getOidcTokenFilePathEnvVarName());
        assertEquals("/path/to/oidc/token", copy.getOidcTokenFilePath());
        assertEquals("cert-content", copy.getClientX509Certificate());
        assertEquals("chain-content", copy.getX509CertChains());
        assertEquals("test-plugin", copy.getPluginName());
        assertEquals("custom_client", copy.getHumanAuthenticateClientId());
    }

    @Test
    @DisplayName("copy: 复制默认值对象应返回正确的副本")
    void copy_DefaultValues_ShouldReturnCorrectCopy() {
        IdentityAuthenticationConfiguration source = new IdentityAuthenticationConfiguration();

        IdentityAuthenticationConfiguration copy = IdentityAuthenticationConfiguration.copy(source);

        assertNotNull(copy);
        assertEquals(AuthenticationIdentityEnum.CLIENT, copy.getIdentityType());
        assertEquals(TokenAuthnMethod.NONE, copy.getAuthnMethod());
        assertEquals("iap_developer", copy.getHumanAuthenticateClientId());
    }

    @Test
    @DisplayName("copy: 复制 null 应返回 null")
    void copy_NullSource_ShouldReturnNull() {
        IdentityAuthenticationConfiguration copy = IdentityAuthenticationConfiguration.copy(null);

        assertNull(copy);
    }

    @Test
    @DisplayName("copy: 副本应与原对象是不同实例")
    void copy_ShouldReturnDifferentInstance() {
        IdentityAuthenticationConfiguration source = new IdentityAuthenticationConfiguration();
        source.setPluginName("test-plugin");

        IdentityAuthenticationConfiguration copy = IdentityAuthenticationConfiguration.copy(source);

        assertNotSame(source, copy);
    }

    @Test
    @DisplayName("copy: 修改副本不应影响原对象")
    void copy_ModifyingCopy_ShouldNotAffectOriginal() {
        IdentityAuthenticationConfiguration source = new IdentityAuthenticationConfiguration();
        source.setPluginName("original-plugin");
        source.setClientSecretEnvVarName("original-secret");

        IdentityAuthenticationConfiguration copy = IdentityAuthenticationConfiguration.copy(source);
        copy.setPluginName("modified-plugin");
        copy.setClientSecretEnvVarName("modified-secret");

        assertEquals("original-plugin", source.getPluginName());
        assertEquals("original-secret", source.getClientSecretEnvVarName());
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
        IdentityAuthenticationConfiguration config1 = new IdentityAuthenticationConfiguration();
        IdentityAuthenticationConfiguration config2 = new IdentityAuthenticationConfiguration();

        config1.setPluginName("plugin-1");
        config2.setPluginName("plugin-2");

        assertEquals("plugin-1", config1.getPluginName());
        assertEquals("plugin-2", config2.getPluginName());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        config.setIdentityType(AuthenticationIdentityEnum.HUMAN);
        config.setAuthnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT);
        config.setClientSecretEnvVarName("FULL_SECRET_VAR");
        config.setPrivateKeyEnvVarName("FULL_KEY_VAR");
        config.setApplicationFederatedCredentialName("full-federated-cred");
        config.setClientDeployEnvironment(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK);
        config.setOidcTokenFilePathEnvVarName("FULL_OIDC_PATH_VAR");
        config.setOidcTokenFilePath("/full/path/to/oidc/token");
        config.setClientX509Certificate("full-cert-content");
        config.setX509CertChains("full-chain-content");
        config.setPluginName("full-plugin");
        config.setHumanAuthenticateClientId("full_custom_client");

        assertEquals(AuthenticationIdentityEnum.HUMAN, config.getIdentityType());
        assertEquals(TokenAuthnMethod.PRIVATE_KEY_JWT, config.getAuthnMethod());
        assertEquals("FULL_SECRET_VAR", config.getClientSecretEnvVarName());
        assertEquals("FULL_KEY_VAR", config.getPrivateKeyEnvVarName());
        assertEquals("full-federated-cred", config.getApplicationFederatedCredentialName());
        assertEquals(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK, config.getClientDeployEnvironment());
        assertEquals("FULL_OIDC_PATH_VAR", config.getOidcTokenFilePathEnvVarName());
        assertEquals("/full/path/to/oidc/token", config.getOidcTokenFilePath());
        assertEquals("full-cert-content", config.getClientX509Certificate());
        assertEquals("full-chain-content", config.getX509CertChains());
        assertEquals("full-plugin", config.getPluginName());
        assertEquals("full_custom_client", config.getHumanAuthenticateClientId());
    }
}
