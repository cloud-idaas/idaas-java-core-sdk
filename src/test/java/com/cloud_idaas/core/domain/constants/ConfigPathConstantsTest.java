package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigPathConstants 单元测试
 */
class ConfigPathConstantsTest {

    // ==================== 常量值测试 ====================

    @Test
    @DisplayName("JVM_CONFIG_PATH_KEY 应正确")
    void jvmConfigPathKey_ShouldBeCorrect() {
        assertEquals("cloud_idaas_config_path", ConfigPathConstants.JVM_CONFIG_PATH_KEY);
    }

    @Test
    @DisplayName("ENV_CONFIG_PATH_KEY 应正确")
    void envConfigPathKey_ShouldBeCorrect() {
        assertEquals("CLOUD_IDAAS_CONFIG_PATH", ConfigPathConstants.ENV_CONFIG_PATH_KEY);
    }

    @Test
    @DisplayName("ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY 应正确")
    void envHumanCredentialCachePathKey_ShouldBeCorrect() {
        assertEquals("CLOUD_IDAAS_HUMAN_CREDENTIAL_CACHE_PATH", 
                ConfigPathConstants.ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY);
    }

    // ==================== 路径包含性测试 ====================

    @Test
    @DisplayName("DEFAULT_CONFIG_PATH 应包含用户目录")
    void defaultConfigPath_ShouldContainUserHome() {
        assertTrue(ConfigPathConstants.DEFAULT_CONFIG_PATH.contains(System.getProperty("user.home")));
    }

    @Test
    @DisplayName("DEFAULT_CONFIG_PATH 应包含 .cloud_idaas 目录")
    void defaultConfigPath_ShouldContainCloudIdaasDirectory() {
        assertTrue(ConfigPathConstants.DEFAULT_CONFIG_PATH.contains(".cloud_idaas"));
    }

    @Test
    @DisplayName("DEFAULT_CONFIG_PATH 应包含配置文件名")
    void defaultConfigPath_ShouldContainConfigFileName() {
        assertTrue(ConfigPathConstants.DEFAULT_CONFIG_PATH.contains("client-config.json"));
    }

    @Test
    @DisplayName("DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE 应包含用户目录")
    void defaultHumanCredentialCachePathTemplate_ShouldContainUserHome() {
        assertTrue(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE.contains(System.getProperty("user.home")));
    }

    @Test
    @DisplayName("DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE 应包含格式化占位符")
    void defaultHumanCredentialCachePathTemplate_ShouldContainFormatPlaceholders() {
        assertTrue(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE.contains("%s"));
    }

    @Test
    @DisplayName("DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE 应包含两个格式化占位符")
    void defaultHumanCredentialCachePathTemplate_ShouldContainTwoPlaceholders() {
        int count = 0;
        String template = ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE;
        int index = 0;
        while ((index = template.indexOf("%s", index)) != -1) {
            count++;
            index += 2;
        }
        assertEquals(2, count);
    }

    // ==================== 非空验证测试 ====================

    @Test
    @DisplayName("所有常量不应为空")
    void allConstants_ShouldNotBeNull() {
        assertNotNull(ConfigPathConstants.JVM_CONFIG_PATH_KEY);
        assertNotNull(ConfigPathConstants.ENV_CONFIG_PATH_KEY);
        assertNotNull(ConfigPathConstants.DEFAULT_CONFIG_PATH);
        assertNotNull(ConfigPathConstants.ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY);
        assertNotNull(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE);
    }

    @Test
    @DisplayName("所有常量不应为空字符串")
    void allConstants_ShouldNotBeEmpty() {
        assertFalse(ConfigPathConstants.JVM_CONFIG_PATH_KEY.isEmpty());
        assertFalse(ConfigPathConstants.ENV_CONFIG_PATH_KEY.isEmpty());
        assertFalse(ConfigPathConstants.DEFAULT_CONFIG_PATH.isEmpty());
        assertFalse(ConfigPathConstants.ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY.isEmpty());
        assertFalse(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE.isEmpty());
    }

    // ==================== 格式验证测试 ====================

    @Test
    @DisplayName("JVM_CONFIG_PATH_KEY 应为小写字母和下划线格式")
    void jvmConfigPathKey_ShouldBeLowerCaseWithUnderscores() {
        assertTrue(ConfigPathConstants.JVM_CONFIG_PATH_KEY.matches("^[a-z_]+$"));
    }

    @Test
    @DisplayName("ENV_CONFIG_PATH_KEY 应为大写字母和下划线格式")
    void envConfigPathKey_ShouldBeUpperCaseWithUnderscores() {
        assertTrue(ConfigPathConstants.ENV_CONFIG_PATH_KEY.matches("^[A-Z_]+$"));
    }

    @Test
    @DisplayName("ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY 应为大写字母和下划线格式")
    void envHumanCredentialCachePathKey_ShouldBeUpperCaseWithUnderscores() {
        assertTrue(ConfigPathConstants.ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY.matches("^[A-Z_]+$"));
    }

    @Test
    @DisplayName("DEFAULT_CONFIG_PATH 应为绝对路径")
    void defaultConfigPath_ShouldBeAbsolutePath() {
        assertTrue(ConfigPathConstants.DEFAULT_CONFIG_PATH.startsWith("/"));
    }

    @Test
    @DisplayName("DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE 应为绝对路径")
    void defaultHumanCredentialCachePathTemplate_ShouldBeAbsolutePath() {
        assertTrue(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE.startsWith("/"));
    }

    // ==================== 文件扩展名测试 ====================

    @Test
    @DisplayName("DEFAULT_CONFIG_PATH 应以 .json 结尾")
    void defaultConfigPath_ShouldEndWithJson() {
        assertTrue(ConfigPathConstants.DEFAULT_CONFIG_PATH.endsWith(".json"));
    }

    @Test
    @DisplayName("DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE 应以 .json 结尾")
    void defaultHumanCredentialCachePathTemplate_ShouldEndWithJson() {
        assertTrue(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE.endsWith(".json"));
    }
}
