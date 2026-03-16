package com.cloud_idaas.core.implementation.authentication.oidc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileOidcTokenProvider 单元测试
 */
class FileOidcTokenProviderTest {

    private static final String TEST_FILE_PATH = "/path/to/token/file";

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 应设置文件路径")
    void constructor_ShouldSetFilePath() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider(TEST_FILE_PATH);

        assertNotNull(provider);
        assertEquals(TEST_FILE_PATH, provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("构造函数: 应接受 null 文件路径")
    void constructor_WithNullPath_ShouldAcceptNull() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider(null);

        assertNotNull(provider);
        assertNull(provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("构造函数: 应接受空字符串文件路径")
    void constructor_WithEmptyPath_ShouldAcceptEmptyString() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider("");

        assertNotNull(provider);
        assertEquals("", provider.getOidcTokenFilePath());
    }

    // ==================== getOidcTokenFilePath 测试 ====================

    @Test
    @DisplayName("getOidcTokenFilePath: 应返回设置的文件路径")
    void getOidcTokenFilePath_ShouldReturnPath() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider(TEST_FILE_PATH);

        String result = provider.getOidcTokenFilePath();

        assertEquals(TEST_FILE_PATH, result);
    }

    @Test
    @DisplayName("getOidcTokenFilePath: 多次调用应返回相同值")
    void getOidcTokenFilePath_MultipleCalls_ShouldReturnSameValue() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider(TEST_FILE_PATH);

        String result1 = provider.getOidcTokenFilePath();
        String result2 = provider.getOidcTokenFilePath();

        assertEquals(TEST_FILE_PATH, result1);
        assertEquals(TEST_FILE_PATH, result2);
        assertSame(result1, result2);
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 OidcTokenProvider 接口")
    void interface_ShouldImplementOidcTokenProvider() {
        FileOidcTokenProvider provider = new FileOidcTokenProvider(TEST_FILE_PATH);

        assertTrue(provider instanceof com.cloud_idaas.core.provider.OidcTokenProvider);
    }

    // ==================== 功能测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护自己的文件路径")
    void multipleInstances_ShouldMaintainIndependentPaths() {
        FileOidcTokenProvider provider1 = new FileOidcTokenProvider("/path/one");
        FileOidcTokenProvider provider2 = new FileOidcTokenProvider("/path/two");

        assertEquals("/path/one", provider1.getOidcTokenFilePath());
        assertEquals("/path/two", provider2.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("功能: 相对路径应被正确存储")
    void relativePath_ShouldBeStoredCorrectly() {
        String relativePath = "./tokens/oidc.token";
        FileOidcTokenProvider provider = new FileOidcTokenProvider(relativePath);

        assertEquals(relativePath, provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("功能: 绝对路径应被正确存储")
    void absolutePath_ShouldBeStoredCorrectly() {
        String absolutePath = "/home/user/.config/oidc/token";
        FileOidcTokenProvider provider = new FileOidcTokenProvider(absolutePath);

        assertEquals(absolutePath, provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("功能: Windows 风格路径应被正确存储")
    void windowsPath_ShouldBeStoredCorrectly() {
        String windowsPath = "C:\\Users\\User\\AppData\\oidc.token";
        FileOidcTokenProvider provider = new FileOidcTokenProvider(windowsPath);

        assertEquals(windowsPath, provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("功能: 包含特殊字符的路径应被正确存储")
    void pathWithSpecialChars_ShouldBeStoredCorrectly() {
        String specialPath = "/path/with spaces/and-dashes/oidc.token";
        FileOidcTokenProvider provider = new FileOidcTokenProvider(specialPath);

        assertEquals(specialPath, provider.getOidcTokenFilePath());
    }

    @Test
    @DisplayName("功能: 长路径应被正确存储")
    void longPath_ShouldBeStoredCorrectly() {
        StringBuilder longPath = new StringBuilder("/very");
        for (int i = 0; i < 50; i++) {
            longPath.append("/long");
        }
        longPath.append("/path/to/the/token/file.oidc");
        String longPathString = longPath.toString();

        FileOidcTokenProvider provider = new FileOidcTokenProvider(longPathString);

        assertEquals(longPathString, provider.getOidcTokenFilePath());
    }
}
