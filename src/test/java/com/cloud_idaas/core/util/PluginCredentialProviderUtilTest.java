package com.cloud_idaas.core.util;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.provider.PluginCredentialProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginCredentialProviderUtil 单元测试
 */
class PluginCredentialProviderUtilTest {

    // ==================== getPluginCredentialProvider 测试 ====================

    @Test
    @DisplayName("getPluginCredentialProvider: null 插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithNullName_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider(null)
        );
        assertEquals("PluginName can not be empty.", exception.getMessage());
    }

    @Test
    @DisplayName("getPluginCredentialProvider: 不存在的插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithNonExistingName_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider("non-existing-plugin")
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
        assertTrue(exception.getMessage().contains("non-existing-plugin"));
    }

    @Test
    @DisplayName("getPluginCredentialProvider: 空字符串插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithEmptyName_ShouldThrowException() {
        // 空字符串不是 null，但 ServiceLoader 中不会有名为 "" 的插件
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider("")
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
    }

    // ==================== 测试用的 Mock PluginCredentialProvider ====================

    /**
     * 用于测试的 Mock 插件提供者
     */
    public static class MockPluginCredentialProvider implements PluginCredentialProvider {
        private final String name;

        public MockPluginCredentialProvider(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public IDaaSTokenResponse getIDaaSCredential(String scope) {
            IDaaSTokenResponse response = new IDaaSTokenResponse();
            response.setAccessToken("mock-token-" + name);
            return response;
        }
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("getPluginCredentialProvider: 特殊字符插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithSpecialChars_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider("plugin@#$%")
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
    }

    @Test
    @DisplayName("getPluginCredentialProvider: 超长插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithVeryLongName_ShouldThrowException() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longName.append("a");
        }

        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider(longName.toString())
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
    }

    @Test
    @DisplayName("getPluginCredentialProvider: 空白字符插件名应抛出 ConfigException")
    void getPluginCredentialProvider_WithWhitespaceName_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider("   ")
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
    }

    @Test
    @DisplayName("getPluginCredentialProvider: 大小写敏感测试")
    void getPluginCredentialProvider_CaseSensitivityTest() {
        // 插件名应该是大小写敏感的
        ConfigException exception = assertThrows(ConfigException.class, () ->
                PluginCredentialProviderUtil.getPluginCredentialProvider("TestPlugin")
        );
        assertTrue(exception.getMessage().contains("Plugin not found"));
    }
}
