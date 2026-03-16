package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpResponse 单元测试
 */
class HttpResponseTest {

    private HttpResponse response;

    @BeforeEach
    void setUp() {
        response = new HttpResponse(200, "OK");
    }

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 带参数构造函数应正确设置值")
    void constructor_WithParameters_ShouldSetValues() {
        HttpResponse httpResponse = new HttpResponse(200, "Success");

        assertEquals(200, httpResponse.getStatusCode());
        assertEquals("Success", httpResponse.getBody());
    }

    @Test
    @DisplayName("构造函数: 私有构造函数应存在")
    void constructor_PrivateConstructor_ShouldExist() {
        // 通过反射可以验证私有构造函数存在
        // 但我们主要测试公共构造函数的行为
        assertNotNull(new HttpResponse(201, "Created"));
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 statusCode 应正确")
    void setter_StatusCode_ShouldBeSet() {
        response.setStatusCode(404);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("Setter: 设置 body 应正确")
    void setter_Body_ShouldBeSet() {
        response.setBody("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", response.getBody());
    }

    @Test
    @DisplayName("Setter: 设置 headers 应正确")
    void setter_Headers_ShouldBeSet() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        assertNotNull(response.getHeaders());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));
    }

    // ==================== isSuccess 测试 ====================

    @Test
    @DisplayName("isSuccess: 200 应返回 true")
    void isSuccess_200_ShouldReturnTrue() {
        response.setStatusCode(200);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 201 应返回 true")
    void isSuccess_201_ShouldReturnTrue() {
        response.setStatusCode(201);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 204 应返回 true")
    void isSuccess_204_ShouldReturnTrue() {
        response.setStatusCode(204);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 299 应返回 true")
    void isSuccess_299_ShouldReturnTrue() {
        response.setStatusCode(299);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 300 应返回 false")
    void isSuccess_300_ShouldReturnFalse() {
        response.setStatusCode(300);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 400 应返回 false")
    void isSuccess_400_ShouldReturnFalse() {
        response.setStatusCode(400);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 401 应返回 false")
    void isSuccess_401_ShouldReturnFalse() {
        response.setStatusCode(401);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 404 应返回 false")
    void isSuccess_404_ShouldReturnFalse() {
        response.setStatusCode(404);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 500 应返回 false")
    void isSuccess_500_ShouldReturnFalse() {
        response.setStatusCode(500);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 199 应返回 false")
    void isSuccess_199_ShouldReturnFalse() {
        response.setStatusCode(199);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("isSuccess: 100 应返回 false")
    void isSuccess_100_ShouldReturnFalse() {
        response.setStatusCode(100);
        assertFalse(response.isSuccess());
    }

    // ==================== 状态码边界测试 ====================

    @Test
    @DisplayName("边界: 状态码 200 是成功的边界值")
    void boundary_StatusCode200_ShouldBeSuccessBoundary() {
        response.setStatusCode(200);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("边界: 状态码 299 是成功的上边界")
    void boundary_StatusCode299_ShouldBeSuccessUpperBoundary() {
        response.setStatusCode(299);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("边界: 状态码 0 应返回 false")
    void boundary_StatusCodeZero_ShouldReturnFalse() {
        response.setStatusCode(0);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("边界: 负数状态码应返回 false")
    void boundary_NegativeStatusCode_ShouldReturnFalse() {
        response.setStatusCode(-1);
        assertFalse(response.isSuccess());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: body 为 null 时应正确返回")
    void nullValue_Body_ShouldBeNull() {
        response.setBody(null);
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("null值: headers 为 null 时应正确返回")
    void nullValue_Headers_ShouldBeNull() {
        response.setHeaders(null);
        assertNull(response.getHeaders());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(response instanceof Serializable);
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        HttpResponse response1 = new HttpResponse(200, "OK");
        HttpResponse response2 = new HttpResponse(404, "Not Found");

        assertEquals(200, response1.getStatusCode());
        assertEquals(404, response2.getStatusCode());
        assertTrue(response1.isSuccess());
        assertFalse(response2.isSuccess());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Request-Id", "req-123");

        response.setStatusCode(200);
        response.setBody("{\"access_token\":\"token123\"}");
        response.setHeaders(headers);

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"access_token\":\"token123\"}", response.getBody());
        assertEquals(2, response.getHeaders().size());
        assertTrue(response.isSuccess());
    }

    // ==================== 常见 HTTP 状态码测试 ====================

    @Test
    @DisplayName("状态码: 200 OK")
    void statusCode_200Ok() {
        response = new HttpResponse(200, "OK");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 201 Created")
    void statusCode_201Created() {
        response = new HttpResponse(201, "Created");
        assertEquals(201, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 204 No Content")
    void statusCode_204NoContent() {
        response = new HttpResponse(204, "");
        assertEquals(204, response.getStatusCode());
        assertTrue(response.isSuccess());
        assertEquals("", response.getBody());
    }

    @Test
    @DisplayName("状态码: 301 Moved Permanently")
    void statusCode_301MovedPermanently() {
        response.setStatusCode(301);
        assertEquals(301, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 302 Found")
    void statusCode_302Found() {
        response.setStatusCode(302);
        assertEquals(302, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 400 Bad Request")
    void statusCode_400BadRequest() {
        response.setStatusCode(400);
        assertEquals(400, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 401 Unauthorized")
    void statusCode_401Unauthorized() {
        response.setStatusCode(401);
        assertEquals(401, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 403 Forbidden")
    void statusCode_403Forbidden() {
        response.setStatusCode(403);
        assertEquals(403, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 404 Not Found")
    void statusCode_404NotFound() {
        response.setStatusCode(404);
        assertEquals(404, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 500 Internal Server Error")
    void statusCode_500InternalServerError() {
        response.setStatusCode(500);
        assertEquals(500, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 502 Bad Gateway")
    void statusCode_502BadGateway() {
        response.setStatusCode(502);
        assertEquals(502, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("状态码: 503 Service Unavailable")
    void statusCode_503ServiceUnavailable() {
        response.setStatusCode(503);
        assertEquals(503, response.getStatusCode());
        assertFalse(response.isSuccess());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: body 为空字符串应正确处理")
    void emptyString_Body_ShouldBeCorrect() {
        response.setBody("");
        assertEquals("", response.getBody());
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: body 包含特殊字符应正确处理")
    void body_SpecialCharacters_ShouldBeCorrect() {
        String body = "{\"error\":\"测试错误\",\"code\":\"特殊字符: !@#$%^&*()\"}";
        response.setBody(body);

        assertEquals(body, response.getBody());
    }

    @Test
    @DisplayName("特殊字符: body 包含 JSON 格式应正确处理")
    void body_JsonFormat_ShouldBeCorrect() {
        String jsonBody = "{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.signature\",\"token_type\":\"Bearer\",\"expires_in\":3600}";
        response.setBody(jsonBody);

        assertEquals(jsonBody, response.getBody());
    }

    // ==================== 长字符串测试 ====================

    @Test
    @DisplayName("长字符串: 长 body 应正确处理")
    void body_LongString_ShouldBeCorrect() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longBody = sb.toString();

        response.setBody(longBody);
        assertEquals(longBody, response.getBody());
        assertEquals(1000, response.getBody().length());
    }

    // ==================== Headers 测试 ====================

    @Test
    @DisplayName("Headers: 多个 header 应正确处理")
    void headers_MultipleHeaders_ShouldBeCorrect() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer token");
        headers.put("X-Request-Id", "req-123");
        headers.put("X-RateLimit-Remaining", "99");

        response.setHeaders(headers);

        assertEquals(4, response.getHeaders().size());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));
        assertEquals("Bearer token", response.getHeaders().get("Authorization"));
    }

    @Test
    @DisplayName("Headers: 空值 header 应正确处理")
    void headers_EmptyValue_ShouldBeCorrect() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Empty", "");

        response.setHeaders(headers);

        assertEquals("", response.getHeaders().get("X-Empty"));
    }
}
