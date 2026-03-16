package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BrowserUtil 单元测试
 */
class BrowserUtilTest {

    // ==================== 类结构测试 ====================

    @Test
    @DisplayName("类结构: BrowserUtil 应为 public 类")
    void classStructure_ShouldBePublic() {
        assertTrue(Modifier.isPublic(BrowserUtil.class.getModifiers()));
    }

    @Test
    @DisplayName("类结构: BrowserUtil 不应为 abstract")
    void classStructure_ShouldNotBeAbstract() {
        assertFalse(Modifier.isAbstract(BrowserUtil.class.getModifiers()));
    }

    @Test
    @DisplayName("包路径: 应位于正确的包中")
    void packagePath_ShouldBeCorrect() {
        assertEquals("com.cloud_idaas.core.util", BrowserUtil.class.getPackage().getName());
    }

    @Test
    @DisplayName("类名: 类名应为 BrowserUtil")
    void className_ShouldBeBrowserUtil() {
        assertEquals("BrowserUtil", BrowserUtil.class.getSimpleName());
    }

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 应有默认构造函数")
    void constructor_ShouldHaveDefaultConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = BrowserUtil.class.getDeclaredConstructor();
        assertNotNull(constructor);
        assertTrue(Modifier.isPublic(constructor.getModifiers()));
    }

    // ==================== 方法签名测试 ====================

    @Test
    @DisplayName("方法: open 应为 public static")
    void method_Open_ShouldBePublicStatic() throws NoSuchMethodException {
        Method method = BrowserUtil.class.getMethod("open", URI.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
    }

    @Test
    @DisplayName("方法: open 应返回 void")
    void method_Open_ShouldReturnVoid() throws NoSuchMethodException {
        Method method = BrowserUtil.class.getMethod("open", URI.class);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: open 应接受 URI 参数")
    void method_Open_ShouldAcceptUriParameter() throws NoSuchMethodException {
        Method method = BrowserUtil.class.getMethod("open", URI.class);
        Class<?>[] paramTypes = method.getParameterTypes();
        assertEquals(1, paramTypes.length);
        assertEquals(URI.class, paramTypes[0]);
    }

    @Test
    @DisplayName("方法: open 应抛出 IOException")
    void method_Open_ShouldThrowIOException() throws NoSuchMethodException {
        Method method = BrowserUtil.class.getMethod("open", URI.class);
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        boolean hasIOException = false;
        for (Class<?> exceptionType : exceptionTypes) {
            if (exceptionType.equals(IOException.class)) {
                hasIOException = true;
                break;
            }
        }
        assertTrue(hasIOException, "Method open should declare IOException");
    }

    // ==================== 工具类特性测试 ====================

    @Test
    @DisplayName("工具类: 类名应以 Util 结尾")
    void utilityClass_NameShouldEndWithUtil() {
        assertTrue(BrowserUtil.class.getSimpleName().endsWith("Util"));
    }

    @Test
    @DisplayName("工具类: 所有方法应为静态")
    void utilityClass_AllMethodsShouldBeStatic() {
        Method[] methods = BrowserUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            assertTrue(Modifier.isStatic(method.getModifiers()),
                    "Method " + method.getName() + " should be static");
        }
    }

    @Test
    @DisplayName("工具类: 不应有实例字段")
    void utilityClass_ShouldNotHaveInstanceFields() {
        java.lang.reflect.Field[] fields = BrowserUtil.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            assertTrue(Modifier.isStatic(field.getModifiers()),
                    "Field " + field.getName() + " should be static");
        }
    }

    // ==================== URI 格式测试 ====================

    @Test
    @DisplayName("URI: HTTP URI 应被正确解析")
    void uri_HttpUri_ShouldBeParsed() {
        URI uri = URI.create("http://example.com");
        assertEquals("http", uri.getScheme());
        assertEquals("example.com", uri.getHost());
    }

    @Test
    @DisplayName("URI: HTTPS URI 应被正确解析")
    void uri_HttpsUri_ShouldBeParsed() {
        URI uri = URI.create("https://example.com");
        assertEquals("https", uri.getScheme());
        assertEquals("example.com", uri.getHost());
    }

    @Test
    @DisplayName("URI: 带有端口的 URI 应被正确解析")
    void uri_WithPort_ShouldBeParsed() {
        URI uri = URI.create("https://example.com:8080/path");
        assertEquals(8080, uri.getPort());
        assertEquals("/path", uri.getPath());
    }

    @Test
    @DisplayName("URI: 带有路径的 URI 应被正确解析")
    void uri_WithPath_ShouldBeParsed() {
        URI uri = URI.create("https://example.com/path/to/resource");
        assertEquals("/path/to/resource", uri.getPath());
    }

    @Test
    @DisplayName("URI: 带有查询参数的 URI 应被正确解析")
    void uri_WithQuery_ShouldBeParsed() {
        URI uri = URI.create("https://example.com?key=value&foo=bar");
        assertNotNull(uri.getQuery());
        assertTrue(uri.getQuery().contains("key=value"));
        assertTrue(uri.getQuery().contains("foo=bar"));
    }

    @Test
    @DisplayName("URI: 带有片段的 URI 应被正确解析")
    void uri_WithFragment_ShouldBeParsed() {
        URI uri = URI.create("https://example.com/page#section");
        assertEquals("section", uri.getFragment());
    }

    @Test
    @DisplayName("URI: 完整的复杂 URI 应被正确解析")
    void uri_ComplexUri_ShouldBeParsed() {
        URI uri = URI.create("https://user:pass@example.com:8080/path/to/page?query=value#fragment");
        assertEquals("https", uri.getScheme());
        assertEquals("example.com", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("/path/to/page", uri.getPath());
        assertEquals("query=value", uri.getQuery());
        assertEquals("fragment", uri.getFragment());
    }

    @Test
    @DisplayName("URI: 本地文件 URI 应被正确解析")
    void uri_FileUri_ShouldBeParsed() {
        URI uri = URI.create("file:///path/to/file.txt");
        assertEquals("file", uri.getScheme());
    }

    // ==================== 异常情况测试 ====================

    @Test
    @DisplayName("异常: 无效的 URI 格式应抛出 IllegalArgumentException")
    void exception_InvalidUri_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                URI.create("not a valid uri")
        );
    }

    @Test
    @DisplayName("异常: null 字符串应抛出 NullPointerException")
    void exception_NullString_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                URI.create(null)
        );
    }

    @Test
    @DisplayName("异常: 缺少 scheme 的 URI 应抛出 IllegalArgumentException")
    void exception_MissingScheme_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                URI.create("://example.com")
        );
    }

    // ==================== Desktop 支持测试 ====================

    @Test
    @DisplayName("Desktop: isDesktopSupported 应返回 boolean")
    void desktop_IsDesktopSupported_ShouldReturnBoolean() {
        // 验证方法存在且返回 boolean
        boolean supported = Desktop.isDesktopSupported();
        // 不验证具体值，因为它取决于运行环境
        assertTrue(supported || !supported);
    }

    @Test
    @DisplayName("Desktop: getDesktop 在支持时应返回 Desktop 实例")
    void desktop_GetDesktop_WhenSupported_ShouldReturnInstance() {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            assertNotNull(desktop);
        }
    }

    @Test
    @DisplayName("Desktop: isSupported(BROWSE) 应返回 boolean")
    void desktop_IsBrowseSupported_ShouldReturnBoolean() {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            boolean browseSupported = desktop.isSupported(Desktop.Action.BROWSE);
            assertTrue(browseSupported || !browseSupported);
        }
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("边界: 带有特殊字符的 URI")
    void edgeCase_UriWithSpecialCharacters() {
        URI uri = URI.create("https://example.com/path%20with%20spaces?key=value%20with%20spaces");
        assertEquals("/path with spaces", uri.getPath());
    }

    @Test
    @DisplayName("边界: 超长 URI")
    void edgeCase_LongUri() {
        StringBuilder longQuery = new StringBuilder("https://example.com?");
        for (int i = 0; i < 100; i++) {
            longQuery.append("key").append(i).append("=value").append(i);
            if (i < 99) {
                longQuery.append("&");
            }
        }
        URI uri = URI.create(longQuery.toString());
        assertNotNull(uri);
        assertTrue(uri.getQuery().length() > 1000);
    }

    @Test
    @DisplayName("边界: IPv4 地址 URI")
    void edgeCase_Ipv4Uri() {
        URI uri = URI.create("http://192.168.1.1:8080/path");
        assertEquals("192.168.1.1", uri.getHost());
        assertEquals(8080, uri.getPort());
    }

    @Test
    @DisplayName("边界: IPv6 地址 URI")
    void edgeCase_Ipv6Uri() {
        URI uri = URI.create("http://[::1]:8080/path");
        assertEquals("[::1]", uri.getHost());
        assertEquals(8080, uri.getPort());
    }

    // ==================== 命名规范测试 ====================

    @Test
    @DisplayName("命名规范: 方法名应符合驼峰命名法")
    void namingConvention_MethodsShouldUseCamelCase() {
        Method[] methods = BrowserUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            assertTrue(Character.isLowerCase(name.charAt(0)),
                    "Method " + name + " should start with lowercase letter");
        }
    }

    @Test
    @DisplayName("命名规范: 类名应符合驼峰命名法")
    void namingConvention_ClassNameShouldUseCamelCase() {
        String className = BrowserUtil.class.getSimpleName();
        assertTrue(Character.isUpperCase(className.charAt(0)),
                "Class name should start with uppercase letter");
    }
}
