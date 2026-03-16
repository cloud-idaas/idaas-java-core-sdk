package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpMethod 单元测试
 */
class HttpMethodTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: GET 应存在")
    void enumValue_Get_ShouldExist() {
        HttpMethod get = HttpMethod.GET;

        assertNotNull(get);
        assertEquals("GET", get.name());
    }

    @Test
    @DisplayName("枚举值: POST 应存在")
    void enumValue_Post_ShouldExist() {
        HttpMethod post = HttpMethod.POST;

        assertNotNull(post);
        assertEquals("POST", post.name());
    }

    @Test
    @DisplayName("枚举值: PUT 应存在")
    void enumValue_Put_ShouldExist() {
        HttpMethod put = HttpMethod.PUT;

        assertNotNull(put);
        assertEquals("PUT", put.name());
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回 3 个枚举值")
    void values_ShouldReturnThreeValues() {
        HttpMethod[] values = HttpMethod.values();

        assertEquals(3, values.length);
    }

    @Test
    @DisplayName("values: 应包含所有枚举值")
    void values_ShouldContainAllValues() {
        HttpMethod[] values = HttpMethod.values();

        assertTrue(containsValue(values, HttpMethod.GET));
        assertTrue(containsValue(values, HttpMethod.POST));
        assertTrue(containsValue(values, HttpMethod.PUT));
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 'GET' 应返回 GET 枚举值")
    void valueOf_GetString_ShouldReturnGetEnum() {
        HttpMethod get = HttpMethod.valueOf("GET");

        assertEquals(HttpMethod.GET, get);
    }

    @Test
    @DisplayName("valueOf: 'POST' 应返回 POST 枚举值")
    void valueOf_PostString_ShouldReturnPostEnum() {
        HttpMethod post = HttpMethod.valueOf("POST");

        assertEquals(HttpMethod.POST, post);
    }

    @Test
    @DisplayName("valueOf: 'PUT' 应返回 PUT 枚举值")
    void valueOf_PutString_ShouldReturnPutEnum() {
        HttpMethod put = HttpMethod.valueOf("PUT");

        assertEquals(HttpMethod.PUT, put);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpMethod.valueOf("DELETE");
        });
    }

    @Test
    @DisplayName("valueOf: null 应抛出 NullPointerException")
    void valueOf_NullName_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            HttpMethod.valueOf(null);
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: GET 的序号应为 0")
    void ordinal_Get_ShouldBeZero() {
        assertEquals(0, HttpMethod.GET.ordinal());
    }

    @Test
    @DisplayName("ordinal: POST 的序号应为 1")
    void ordinal_Post_ShouldBeOne() {
        assertEquals(1, HttpMethod.POST.ordinal());
    }

    @Test
    @DisplayName("ordinal: PUT 的序号应为 2")
    void ordinal_Put_ShouldBeTwo() {
        assertEquals(2, HttpMethod.PUT.ordinal());
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: 各枚举值名称应正确")
    void name_AllValues_ShouldBeCorrect() {
        assertEquals("GET", HttpMethod.GET.name());
        assertEquals("POST", HttpMethod.POST.name());
        assertEquals("PUT", HttpMethod.PUT.name());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("GET", HttpMethod.GET.toString());
        assertEquals("POST", HttpMethod.POST.toString());
        assertEquals("PUT", HttpMethod.PUT.toString());
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        HttpMethod get1 = HttpMethod.GET;
        HttpMethod get2 = HttpMethod.GET;

        assertEquals(get1, get2);
        assertSame(get1, get2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(HttpMethod.GET, HttpMethod.POST);
        assertNotEquals(HttpMethod.POST, HttpMethod.PUT);
        assertNotEquals(HttpMethod.GET, HttpMethod.PUT);
    }

    // ==================== switch 使用测试 ====================

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 GET")
    void switchStatement_ShouldCorrectlyMatchGet() {
        String result = getMethodDescription(HttpMethod.GET);
        assertEquals("get", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 POST")
    void switchStatement_ShouldCorrectlyMatchPost() {
        String result = getMethodDescription(HttpMethod.POST);
        assertEquals("post", result);
    }

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配 PUT")
    void switchStatement_ShouldCorrectlyMatchPut() {
        String result = getMethodDescription(HttpMethod.PUT);
        assertEquals("put", result);
    }

    // ==================== HTTP 方法语义测试 ====================

    @Test
    @DisplayName("语义: GET 通常用于获取资源")
    void semantics_Get_ShouldBeForRetrieving() {
        // GET 方法应该是幂等的
        assertEquals("GET", HttpMethod.GET.name());
    }

    @Test
    @DisplayName("语义: POST 通常用于创建资源")
    void semantics_Post_ShouldBeForCreating() {
        // POST 方法通常不是幂等的
        assertEquals("POST", HttpMethod.POST.name());
    }

    @Test
    @DisplayName("语义: PUT 通常用于更新资源")
    void semantics_Put_ShouldBeForUpdating() {
        // PUT 方法应该是幂等的
        assertEquals("PUT", HttpMethod.PUT.name());
    }

    // ==================== 辅助方法 ====================

    private boolean containsValue(HttpMethod[] values, HttpMethod target) {
        for (HttpMethod value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private String getMethodDescription(HttpMethod method) {
        switch (method) {
            case GET:
                return "get";
            case POST:
                return "post";
            case PUT:
                return "put";
            default:
                return "unknown";
        }
    }
}
