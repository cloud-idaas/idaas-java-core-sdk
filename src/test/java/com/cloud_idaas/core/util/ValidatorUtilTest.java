package com.cloud_idaas.core.util;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.CacheException;
import com.cloud_idaas.core.exception.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidatorUtil 单元测试
 */
class ValidatorUtilTest {

    // ==================== validateConfigNotNull 测试 ====================

    @Test
    @DisplayName("validateConfigNotNull: 非空对象应通过验证")
    void validateConfigNotNull_WithNonNullObject_ShouldPass() {
        assertDoesNotThrow(() ->
                ValidatorUtil.validateConfigNotNull("test", "ERROR_CODE", "error message")
        );
    }

    @Test
    @DisplayName("validateConfigNotNull: null 对象应抛出 ConfigException")
    void validateConfigNotNull_WithNullObject_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateConfigNotNull(null, "TEST_ERROR", "Test error message")
        );
        assertEquals("TEST_ERROR", exception.getErrorCode());
        assertEquals("Test error message", exception.getMessage());
    }

    // ==================== validateTokenNotNull 测试 ====================

    @Test
    @DisplayName("validateTokenNotNull: 非空对象应通过验证")
    void validateTokenNotNull_WithNonNullObject_ShouldPass() {
        assertDoesNotThrow(() ->
                ValidatorUtil.validateTokenNotNull("test", "ERROR_CODE", "error message")
        );
    }

    @Test
    @DisplayName("validateTokenNotNull: null 对象应抛出 CacheException")
    void validateTokenNotNull_WithNullObject_ShouldThrowException() {
        CacheException exception = assertThrows(CacheException.class, () ->
                ValidatorUtil.validateTokenNotNull(null, "TEST_ERROR", "Test error message")
        );
        assertEquals("TEST_ERROR", exception.getErrorCode());
    }

    // ==================== validateBaseConfig 测试 ====================

    @Test
    @DisplayName("validateBaseConfig: 有效配置应通过验证")
    void validateBaseConfig_WithValidConfig_ShouldPass() {
        IDaaSClientConfig config = createValidBaseConfig();

        assertDoesNotThrow(() -> ValidatorUtil.validateBaseConfig(config));
    }

    @Test
    @DisplayName("validateBaseConfig: 缺少 idaasInstanceId 应抛出异常")
    void validateBaseConfig_WithoutInstanceId_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setIdaasInstanceId(null);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateBaseConfig(config)
        );
        assertEquals(ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateBaseConfig: 缺少 clientId 应抛出异常")
    void validateBaseConfig_WithoutClientId_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setClientId(null);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateBaseConfig(config)
        );
        assertEquals(ErrorCode.CLIENT_ID_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateBaseConfig: 缺少 issuer 应抛出异常")
    void validateBaseConfig_WithoutIssuer_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setIssuer(null);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateBaseConfig(config)
        );
        assertEquals(ErrorCode.ISSUER_ENDPOINT_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateBaseConfig: 缺少 tokenEndpoint 应抛出异常")
    void validateBaseConfig_WithoutTokenEndpoint_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setTokenEndpoint(null);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateBaseConfig(config)
        );
        assertEquals(ErrorCode.TOKEN_ENDPOINT_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateBaseConfig: 无效 scope 应抛出异常")
    void validateBaseConfig_WithInvalidScope_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setScope("invalid_scope_format");

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateBaseConfig(config)
        );
        assertEquals(ErrorCode.INVALID_SCOPE.getCode(), exception.getErrorCode());
    }

    // ==================== validateHumanConfig 测试 ====================

    @Test
    @DisplayName("validateHumanConfig: 有效配置应通过验证")
    void validateHumanConfig_WithValidConfig_ShouldPass() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setHumanAuthenticateClientId("test-human-client-id");
        config.setAuthnConfiguration(authnConfig);
        config.setDeviceAuthorizationEndpoint("https://example.com/device");

        assertDoesNotThrow(() -> ValidatorUtil.validateHumanConfig(config));
    }

    @Test
    @DisplayName("validateHumanConfig: 缺少 deviceAuthorizationEndpoint 应抛出异常")
    void validateHumanConfig_WithoutDeviceEndpoint_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setHumanAuthenticateClientId("test-human-client-id");
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateHumanConfig(config)
        );
        assertEquals(ErrorCode.DEVICE_AUTHORIZATION_ENDPOINT_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    // ==================== validateClientConfig 测试 ====================

    @Test
    @DisplayName("validateClientConfig: 缺少 authnConfiguration 应抛出异常")
    void validateClientConfig_WithoutAuthnConfig_ShouldThrowException() {
        IDaaSClientConfig config = createValidBaseConfig();
        config.setAuthnConfiguration(null);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.AUTHN_CONFIGURATION_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: CLIENT_SECRET_BASIC 需要 clientSecretEnvVarName")
    void validateClientConfig_ClientSecretBasic_ShouldRequireSecretEnvVar() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_BASIC);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.CLIENT_SECRET_ENV_VAR_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: CLIENT_SECRET_POST 需要 clientSecretEnvVarName")
    void validateClientConfig_ClientSecretPost_ShouldRequireSecretEnvVar() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.CLIENT_SECRET_ENV_VAR_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: CLIENT_SECRET_JWT 需要 clientSecretEnvVarName")
    void validateClientConfig_ClientSecretJwt_ShouldRequireSecretEnvVar() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_JWT);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.CLIENT_SECRET_ENV_VAR_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: PRIVATE_KEY_JWT 需要 privateKeyEnvVarName")
    void validateClientConfig_PrivateKeyJwt_ShouldRequirePrivateKeyEnvVar() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.PRIVATE_KEY_ENV_VAR_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: PKCS7 需要 applicationFederatedCredentialName 和 clientDeployEnvironment")
    void validateClientConfig_Pkcs7_ShouldRequireFederatedCredAndEnv() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.PKCS7);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: OIDC 需要 applicationFederatedCredentialName 和 clientDeployEnvironment")
    void validateClientConfig_Oidc_ShouldRequireFederatedCredAndEnv() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.OIDC);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: PCA 需要所有必需字段")
    void validateClientConfig_Pca_ShouldRequireAllFields() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.PCA);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateClientConfig: PLUGIN 需要 openApiEndpoint 和 pluginName")
    void validateClientConfig_Plugin_ShouldRequireEndpointAndName() {
        IDaaSClientConfig config = createValidBaseConfig();
        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setAuthnMethod(TokenAuthnMethod.PLUGIN);
        config.setAuthnConfiguration(authnConfig);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateClientConfig(config)
        );
        assertEquals(ErrorCode.OPEN_API_ENDPOINT_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    // ==================== validateHttpConfig 测试 ====================

    @Test
    @DisplayName("validateHttpConfig: null 配置应通过验证")
    void validateHttpConfig_WithNullConfig_ShouldPass() {
        assertDoesNotThrow(() -> ValidatorUtil.validateHttpConfig(null));
    }

    @Test
    @DisplayName("validateHttpConfig: 有效配置应通过验证")
    void validateHttpConfig_WithValidConfig_ShouldPass() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(5000);
        httpConfig.setReadTimeout(10000);

        assertDoesNotThrow(() -> ValidatorUtil.validateHttpConfig(httpConfig));
    }

    @Test
    @DisplayName("validateHttpConfig: connectTimeout 小于 2000 应抛出异常")
    void validateHttpConfig_ConnectTimeoutTooLow_ShouldThrowException() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(1000);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateHttpConfig(httpConfig)
        );
        assertEquals(ErrorCode.CONNECT_TIMEOUT_NOT_VALID.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateHttpConfig: connectTimeout 大于 60000 应抛出异常")
    void validateHttpConfig_ConnectTimeoutTooHigh_ShouldThrowException() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(70000);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateHttpConfig(httpConfig)
        );
        assertEquals(ErrorCode.CONNECT_TIMEOUT_NOT_VALID.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateHttpConfig: readTimeout 小于 2000 应抛出异常")
    void validateHttpConfig_ReadTimeoutTooLow_ShouldThrowException() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setReadTimeout(1000);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateHttpConfig(httpConfig)
        );
        assertEquals(ErrorCode.READ_TIMEOUT_NOT_VALID.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateHttpConfig: readTimeout 大于 60000 应抛出异常")
    void validateHttpConfig_ReadTimeoutTooHigh_ShouldThrowException() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setReadTimeout(70000);

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ValidatorUtil.validateHttpConfig(httpConfig)
        );
        assertEquals(ErrorCode.READ_TIMEOUT_NOT_VALID.getCode(), exception.getErrorCode());
    }

    // ==================== validateLocalToken 测试 ====================

    @Test
    @DisplayName("validateLocalToken: 有效 token 应通过验证")
    void validateLocalToken_WithValidToken_ShouldPass() {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setTokenType("Bearer");
        token.setAccessToken("access-token");
        token.setIdToken("id-token");
        token.setRefreshToken("refresh-token");

        assertDoesNotThrow(() -> ValidatorUtil.validateLocalToken(token));
    }

    @Test
    @DisplayName("validateLocalToken: 非 Bearer token 类型应抛出异常")
    void validateLocalToken_NonBearerToken_ShouldThrowException() {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setTokenType("Basic");

        CacheException exception = assertThrows(CacheException.class, () ->
                ValidatorUtil.validateLocalToken(token)
        );
        assertEquals(ErrorCode.INVALID_TOKEN_TYPE.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateLocalToken: 缺少 accessToken 应抛出异常")
    void validateLocalToken_WithoutAccessToken_ShouldThrowException() {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setTokenType("Bearer");
        token.setIdToken("id-token");
        token.setRefreshToken("refresh-token");

        CacheException exception = assertThrows(CacheException.class, () ->
                ValidatorUtil.validateLocalToken(token)
        );
        assertEquals(ErrorCode.ACCESS_TOKEN_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateLocalToken: 缺少 idToken 应抛出异常")
    void validateLocalToken_WithoutIdToken_ShouldThrowException() {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setTokenType("Bearer");
        token.setAccessToken("access-token");
        token.setRefreshToken("refresh-token");

        CacheException exception = assertThrows(CacheException.class, () ->
                ValidatorUtil.validateLocalToken(token)
        );
        assertEquals(ErrorCode.ID_TOKEN_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateLocalToken: 缺少 refreshToken 应抛出异常")
    void validateLocalToken_WithoutRefreshToken_ShouldThrowException() {
        IDaaSTokenResponse token = new IDaaSTokenResponse();
        token.setTokenType("Bearer");
        token.setAccessToken("access-token");
        token.setIdToken("id-token");

        CacheException exception = assertThrows(CacheException.class, () ->
                ValidatorUtil.validateLocalToken(token)
        );
        assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode(), exception.getErrorCode());
    }

    // ==================== 辅助方法 ====================

    private IDaaSClientConfig createValidBaseConfig() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIdaasInstanceId("test-instance");
        config.setClientId("test-client");
        config.setIssuer("https://example.com");
        config.setTokenEndpoint("https://example.com/token");
        config.setScope("app|openid");

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
}
