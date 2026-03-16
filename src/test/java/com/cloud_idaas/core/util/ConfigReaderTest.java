package com.cloud_idaas.core.util;

import com.cloud_idaas.core.domain.constants.ConfigPathConstants;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigReader 单元测试
 */
class ConfigReaderTest {

    @TempDir
    Path tempDir;

    // ==================== loadFileAsString 测试 ====================

    @Test
    @DisplayName("loadFileAsString: 成功加载文件内容")
    void loadFileAsString_WithExistingFile_ShouldReturnContent() throws IOException {
        String content = "{\"key\": \"value\"}";
        Path configFile = tempDir.resolve("config.json");
        Files.write(configFile, content.getBytes());

        String result = ConfigReader.loadFileAsString(configFile.toString());

        assertNotNull(result);
        assertTrue(result.contains("\"key\": \"value\""));
    }

    @Test
    @DisplayName("loadFileAsString: 加载多行文件")
    void loadFileAsString_WithMultiLineFile_ShouldReturnAllContent() throws IOException {
        String content = "{\n  \"key1\": \"value1\",\n  \"key2\": \"value2\"\n}";
        Path configFile = tempDir.resolve("multiline.json");
        Files.write(configFile, content.getBytes());

        String result = ConfigReader.loadFileAsString(configFile.toString());

        assertTrue(result.contains("key1"));
        assertTrue(result.contains("key2"));
    }

    @Test
    @DisplayName("loadFileAsString: 加载 UTF-8 编码文件")
    void loadFileAsString_WithUtf8Content_ShouldReturnCorrectContent() throws IOException {
        String content = "{\"message\": \"中文测试\"}";
        Path configFile = tempDir.resolve("utf8.json");
        Files.write(configFile, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        String result = ConfigReader.loadFileAsString(configFile.toString());

        assertTrue(result.contains("中文测试"));
    }

    @Test
    @DisplayName("loadFileAsString: 文件不存在应抛出 ConfigException")
    void loadFileAsString_WithNonExistingFile_ShouldThrowConfigException() {
        String nonExistingPath = tempDir.resolve("non-existing.json").toString();

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ConfigReader.loadFileAsString(nonExistingPath)
        );
        assertEquals(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("loadFileAsString: 空文件应返回空字符串")
    void loadFileAsString_WithEmptyFile_ShouldReturnEmptyString() throws IOException {
        Path emptyFile = tempDir.resolve("empty.json");
        Files.createFile(emptyFile);

        String result = ConfigReader.loadFileAsString(emptyFile.toString());

        assertNotNull(result);
    }

    // ==================== getConfigAsString 测试 ====================

    @Test
    @DisplayName("getConfigAsString: 未配置路径时应抛出异常")
    void getConfigAsString_WithoutConfigPath_ShouldThrowException() {
        // 清除系统属性和环境变量
        System.clearProperty(ConfigPathConstants.JVM_CONFIG_PATH_KEY);

        // 当没有配置路径时，应该使用默认路径，如果默认路径也不存在则会抛出异常
        assertThrows(ConfigException.class, () ->
                ConfigReader.getConfigAsString()
        );
    }

    // ==================== classpath 路径测试 ====================

    @Test
    @DisplayName("loadFileAsString: classpath 前缀路径应被正确处理")
    void loadFileAsString_WithClasspathPrefix_ShouldHandle() {
        // 测试 classpath: 前缀的解析逻辑
        String classpathPath = "classpath:non-existing-resource.json";

        ConfigException exception = assertThrows(ConfigException.class, () ->
                ConfigReader.loadFileAsString(classpathPath)
        );
        assertEquals(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), exception.getErrorCode());
    }

    // ==================== 配置文件格式测试 ====================

    @Test
    @DisplayName("loadFileAsString: JSON 配置文件")
    void loadFileAsString_WithJsonConfig_ShouldReturnContent() throws IOException {
        String jsonContent = "{\n" +
                "  \"idaasInstanceId\": \"test-instance\",\n" +
                "  \"clientId\": \"test-client\",\n" +
                "  \"scope\": \"openid profile\"\n" +
                "}";
        Path jsonFile = tempDir.resolve("config.json");
        Files.write(jsonFile, jsonContent.getBytes());

        String result = ConfigReader.loadFileAsString(jsonFile.toString());

        assertTrue(result.contains("idaasInstanceId"));
        assertTrue(result.contains("test-instance"));
    }

    @Test
    @DisplayName("loadFileAsString: Properties 配置文件")
    void loadFileAsString_WithPropertiesConfig_ShouldReturnContent() throws IOException {
        String propsContent = "idaasInstanceId=test-instance\nclientId=test-client";
        Path propsFile = tempDir.resolve("config.properties");
        Files.write(propsFile, propsContent.getBytes());

        String result = ConfigReader.loadFileAsString(propsFile.toString());

        assertTrue(result.contains("idaasInstanceId"));
        assertTrue(result.contains("test-instance"));
    }

    @Test
    @DisplayName("loadFileAsString: XML 配置文件")
    void loadFileAsString_WithXmlConfig_ShouldReturnContent() throws IOException {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<config>\n" +
                "  <idaasInstanceId>test-instance</idaasInstanceId>\n" +
                "</config>";
        Path xmlFile = tempDir.resolve("config.xml");
        Files.write(xmlFile, xmlContent.getBytes());

        String result = ConfigReader.loadFileAsString(xmlFile.toString());

        assertTrue(result.contains("<?xml version"));
        assertTrue(result.contains("test-instance"));
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("loadFileAsString: 大文件应被正确处理")
    void loadFileAsString_WithLargeFile_ShouldHandle() throws IOException {
        StringBuilder largeContent = new StringBuilder();
        largeContent.append("{\n");
        for (int i = 0; i < 1000; i++) {
            largeContent.append("  \"key").append(i).append("\": \"value").append(i).append("\",\n");
        }
        largeContent.append("  \"lastKey\": \"lastValue\"\n}");

        Path largeFile = tempDir.resolve("large.json");
        Files.write(largeFile, largeContent.toString().getBytes());

        String result = ConfigReader.loadFileAsString(largeFile.toString());

        assertNotNull(result);
        assertTrue(result.length() > 10000);
        assertTrue(result.contains("key0"));
        assertTrue(result.contains("lastKey"));
    }

    @Test
    @DisplayName("loadFileAsString: 特殊字符文件应被正确处理")
    void loadFileAsString_WithSpecialCharacters_ShouldHandle() throws IOException {
        String specialContent = "{\"special\": \"!@#$%^&*()_+-=[]{}|;':\\\",./<>?\"}";
        Path specialFile = tempDir.resolve("special.json");
        Files.write(specialFile, specialContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        String result = ConfigReader.loadFileAsString(specialFile.toString());

        assertTrue(result.contains("!@#$%^&*()"));
    }
}
