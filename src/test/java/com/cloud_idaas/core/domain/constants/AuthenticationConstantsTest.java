package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthenticationConstants 单元测试
 */
class AuthenticationConstantsTest {

    // ==================== 常量值测试 ====================

    @Test
    @DisplayName("KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH 应正确")
    void kubernetesServiceAccountTokenPath_ShouldBeCorrect() {
        assertEquals("/var/run/secrets/kubernetes.io/serviceaccount/token", 
                AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH);
    }

    @Test
    @DisplayName("ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL 应正确")
    void alibabaCloudEcsMetadataServiceUrl_ShouldBeCorrect() {
        assertEquals("http://100.100.100.200/latest/meta-data/", 
                AuthenticationConstants.ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL);
    }

    @Test
    @DisplayName("ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV 应正确")
    void alibabaCloudAckOidcTokenPathEnv_ShouldBeCorrect() {
        assertEquals("ALIBABA_CLOUD_OIDC_TOKEN_FILE", 
                AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV);
    }

    @Test
    @DisplayName("DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME 应正确")
    void defaultClientIdEnvironmentVariableName_ShouldBeCorrect() {
        assertEquals("CLOUD_IDAAS_CLIENT_ID", 
                AuthenticationConstants.DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME);
    }

    @Test
    @DisplayName("DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME 应正确")
    void defaultClientSecretEnvironmentVariableName_ShouldBeCorrect() {
        assertEquals("CLOUD_IDAAS_CLIENT_SECRET", 
                AuthenticationConstants.DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME);
    }

    // ==================== 非空验证测试 ====================

    @Test
    @DisplayName("所有常量不应为空")
    void allConstants_ShouldNotBeNull() {
        assertNotNull(AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH);
        assertNotNull(AuthenticationConstants.ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL);
        assertNotNull(AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV);
        assertNotNull(AuthenticationConstants.DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME);
        assertNotNull(AuthenticationConstants.DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME);
    }

    @Test
    @DisplayName("所有常量不应为空字符串")
    void allConstants_ShouldNotBeEmpty() {
        assertFalse(AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH.isEmpty());
        assertFalse(AuthenticationConstants.ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL.isEmpty());
        assertFalse(AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV.isEmpty());
        assertFalse(AuthenticationConstants.DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME.isEmpty());
        assertFalse(AuthenticationConstants.DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME.isEmpty());
    }

    // ==================== 特定格式验证测试 ====================

    @Test
    @DisplayName("KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH 应为有效路径格式")
    void kubernetesTokenPath_ShouldBeValidPathFormat() {
        assertTrue(AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH.startsWith("/"));
    }

    @Test
    @DisplayName("ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL 应为有效 URL 格式")
    void alibabaEcsMetadataUrl_ShouldBeValidUrlFormat() {
        assertTrue(AuthenticationConstants.ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL.startsWith("http://"));
    }

    @Test
    @DisplayName("环境变量名应为大写字母和下划线格式")
    void environmentVariableNames_ShouldBeUpperCaseWithUnderscores() {
        assertTrue(AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV.matches("^[A-Z_]+$"));
        assertTrue(AuthenticationConstants.DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME.matches("^[A-Z_]+$"));
        assertTrue(AuthenticationConstants.DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME.matches("^[A-Z_]+$"));
    }
}
