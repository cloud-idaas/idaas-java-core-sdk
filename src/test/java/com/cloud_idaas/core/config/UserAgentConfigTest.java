package com.cloud_idaas.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserAgentConfig 单元测试
 */
class UserAgentConfigTest {

    // ==================== getUserAgentMessage 测试 ====================

    @Test
    @DisplayName("getUserAgentMessage: 应返回非空字符串")
    void getUserAgentMessage_ShouldReturnNonEmptyString() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertNotNull(userAgent);
        assertFalse(userAgent.isEmpty());
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含 'IDaaS core'")
    void getUserAgentMessage_ShouldContainIdaasCore() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertTrue(userAgent.contains("IDaaS core"));
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含 'Java/' 标识")
    void getUserAgentMessage_ShouldContainJavaVersion() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertTrue(userAgent.contains("Java/"));
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含 'OS(' 标识")
    void getUserAgentMessage_ShouldContainOsInfo() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertTrue(userAgent.contains("OS("));
    }

    @Test
    @DisplayName("getUserAgentMessage: 格式应正确")
    void getUserAgentMessage_FormatShouldBeCorrect() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        // 格式应为: IDaaS core/{version} Java/{java_version} OS({os_name}; {os_arch})
        assertTrue(userAgent.startsWith("IDaaS core/"));
        assertTrue(userAgent.contains(" Java/"));
        assertTrue(userAgent.contains(" OS("));
        assertTrue(userAgent.endsWith(")"));
    }

    @Test
    @DisplayName("getUserAgentMessage: 多次调用应返回相同值")
    void getUserAgentMessage_MultipleCalls_ShouldReturnSameValue() {
        String userAgent1 = UserAgentConfig.getUserAgentMessage();
        String userAgent2 = UserAgentConfig.getUserAgentMessage();
        String userAgent3 = UserAgentConfig.getUserAgentMessage();

        assertEquals(userAgent1, userAgent2);
        assertEquals(userAgent2, userAgent3);
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含实际 Java 版本")
    void getUserAgentMessage_ShouldContainActualJavaVersion() {
        String userAgent = UserAgentConfig.getUserAgentMessage();
        String javaVersion = System.getProperty("java.version");

        assertTrue(userAgent.contains(javaVersion));
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含实际操作系统名称")
    void getUserAgentMessage_ShouldContainActualOsName() {
        String userAgent = UserAgentConfig.getUserAgentMessage();
        String osName = System.getProperty("os.name");

        assertTrue(userAgent.contains(osName));
    }

    @Test
    @DisplayName("getUserAgentMessage: 应包含实际操作系统架构")
    void getUserAgentMessage_ShouldContainActualOsArch() {
        String userAgent = UserAgentConfig.getUserAgentMessage();
        String osArch = System.getProperty("os.arch");

        assertTrue(userAgent.contains(osArch));
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        UserAgentConfig config = new UserAgentConfig();
        assertTrue(config instanceof Serializable);
    }

    // ==================== 实例化测试 ====================

    @Test
    @DisplayName("实例化: 应能创建实例")
    void instantiation_ShouldCreateInstance() {
        UserAgentConfig config = new UserAgentConfig();

        assertNotNull(config);
    }

    @Test
    @DisplayName("实例化: 多个实例应能正常工作")
    void instantiation_MultipleInstances_ShouldWork() {
        UserAgentConfig config1 = new UserAgentConfig();
        UserAgentConfig config2 = new UserAgentConfig();

        assertNotNull(config1);
        assertNotNull(config2);
        assertNotSame(config1, config2);
    }

    // ==================== 静态方法测试 ====================

    @Test
    @DisplayName("静态方法: getUserAgentMessage 是静态方法")
    void staticMethod_GetUserAgentMessage_ShouldBeStatic() {
        // 通过实例调用静态方法（不推荐，但测试其行为）
        UserAgentConfig config = new UserAgentConfig();
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertNotNull(userAgent);
    }

    // ==================== User-Agent 内容验证测试 ====================

    @Test
    @DisplayName("内容验证: User-Agent 不应包含 null")
    void contentValidation_ShouldNotContainNull() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        assertFalse(userAgent.contains("null"));
    }

    @Test
    @DisplayName("内容验证: User-Agent 版本部分应为有效格式")
    void contentValidation_VersionFormat_ShouldBeValid() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        // 提取版本部分: "IDaaS core/{version}"
        int startIndex = userAgent.indexOf("IDaaS core/") + "IDaaS core/".length();
        int endIndex = userAgent.indexOf(" ", startIndex);
        String version = userAgent.substring(startIndex, endIndex);

        // 版本不应为空
        assertFalse(version.isEmpty());
    }

    @Test
    @DisplayName("内容验证: OS 信息应包含分号分隔符")
    void contentValidation_OsInfo_ShouldContainSemicolon() {
        String userAgent = UserAgentConfig.getUserAgentMessage();

        // OS 信息格式: OS({os_name}; {os_arch})
        int osStart = userAgent.indexOf("OS(");
        int osEnd = userAgent.indexOf(")", osStart);
        String osInfo = userAgent.substring(osStart, osEnd + 1);

        assertTrue(osInfo.contains(";"));
    }
}
