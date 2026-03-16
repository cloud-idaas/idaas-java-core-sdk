package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpRequest 单元测试
 */
class HttpRequestTest {

    private HttpRequest request;

    @BeforeEach
    void setUp() {
        request = new HttpRequest();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: 所有字段默认应为 null")
    void defaultValue_AllFields_ShouldBeNull() {
        assertNull(request.getMethod());
        assertNull(request.getUrl());
        assertNull(request.getHeaders());
        assertNull(request.getBody());
        assertNull(request.getFormBody());
        assertNull(request.getContentType());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 method 应正确")
    void setter_Method_ShouldBeSet() {
        request.setMethod(HttpMethod.POST);
        assertEquals(HttpMethod.POST, request.getMethod());
    }

    @Test
    @DisplayName("Setter: 设置 url 应正确")
    void setter_Url_ShouldBeSet() {
        request.setUrl("https://api.example.com");
        assertEquals("https://api.example.com", request.getUrl());
    }

    @Test
    @DisplayName("Setter: 设置 headers 应正确")
    void setter_Headers_ShouldBeSet() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        request.setHeaders(headers);

        assertNotNull(request.getHeaders());
        assertEquals("application/json", request.getHeaders().get("Content-Type").get(0));
    }

    @Test
    @DisplayName("Setter: 设置 body 应正确")
    void setter_Body_ShouldBeSet() {
        request.setBody("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", request.getBody());
    }

    @Test
    @DisplayName("Setter: 设置 formBody 应正确")
    void setter_FormBody_ShouldBeSet() {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put("grant_type", Collections.singletonList("client_credentials"));
        request.setFormBody(formBody);

        assertNotNull(request.getFormBody());
        assertEquals("client_credentials", request.getFormBody().get("grant_type").get(0));
    }

    @Test
    @DisplayName("Setter: 设置 contentType 应正确")
    void setter_ContentType_ShouldBeSet() {
        request.setContentType(ContentType.JSON);
        assertEquals(ContentType.JSON, request.getContentType());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置字段为 null 应正确")
    void nullValue_AllFields_ShouldBeSet() {
        request.setMethod(HttpMethod.GET);
        request.setMethod(null);
        assertNull(request.getMethod());

        request.setUrl("https://test.com");
        request.setUrl(null);
        assertNull(request.getUrl());

        request.setBody("body");
        request.setBody(null);
        assertNull(request.getBody());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 应正确构建 HttpRequest")
    void builder_ShouldBuildHttpRequest() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Collections.singletonList("Bearer token"));

        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put("scope", Collections.singletonList("read"));

        HttpRequest builtRequest = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://api.example.com/token")
                .headers(headers)
                .body("{\"key\":\"value\"}")
                .formBody(formBody)
                .contentType(ContentType.FORM)
                .build();

        assertEquals(HttpMethod.POST, builtRequest.getMethod());
        assertEquals("https://api.example.com/token", builtRequest.getUrl());
        assertNotNull(builtRequest.getHeaders());
        assertEquals("{\"key\":\"value\"}", builtRequest.getBody());
        assertNotNull(builtRequest.getFormBody());
        assertEquals(ContentType.FORM, builtRequest.getContentType());
    }

    @Test
    @DisplayName("Builder: 最小配置应正确构建")
    void builder_MinimalConfig_ShouldBuild() {
        HttpRequest builtRequest = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://api.example.com")
                .build();

        assertEquals(HttpMethod.GET, builtRequest.getMethod());
        assertEquals("https://api.example.com", builtRequest.getUrl());
        assertNull(builtRequest.getHeaders());
        assertNull(builtRequest.getBody());
    }

    @Test
    @DisplayName("Builder: 方法链应正常工作")
    void builder_MethodChaining_ShouldWork() {
        HttpRequest builtRequest = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://api.example.com")
                .contentType(ContentType.JSON)
                .body("{\"test\":true}")
                .build();

        assertEquals(HttpMethod.POST, builtRequest.getMethod());
        assertEquals("https://api.example.com", builtRequest.getUrl());
        assertEquals(ContentType.JSON, builtRequest.getContentType());
        assertEquals("{\"test\":true}", builtRequest.getBody());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(request instanceof Serializable);
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        HttpRequest request1 = new HttpRequest();
        HttpRequest request2 = new HttpRequest();

        request1.setUrl("https://api1.example.com");
        request2.setUrl("https://api2.example.com");

        assertEquals("https://api1.example.com", request1.getUrl());
        assertEquals("https://api2.example.com", request2.getUrl());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Authorization", Arrays.asList("Bearer", "token"));

        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put("grant_type", Collections.singletonList("client_credentials"));
        formBody.put("scope", Collections.singletonList("read write"));

        request.setMethod(HttpMethod.POST);
        request.setUrl("https://idaas.example.com/oauth2/token");
        request.setHeaders(headers);
        request.setBody("{\"additional\":\"data\"}");
        request.setFormBody(formBody);
        request.setContentType(ContentType.JSON);

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("https://idaas.example.com/oauth2/token", request.getUrl());
        assertEquals(2, request.getHeaders().size());
        assertEquals("{\"additional\":\"data\"}", request.getBody());
        assertEquals(2, request.getFormBody().size());
        assertEquals(ContentType.JSON, request.getContentType());
    }

    // ==================== 多次设置测试 ====================

    @Test
    @DisplayName("多次设置: 多次设置同一字段应使用最后一次的值")
    void multipleSet_SameField_ShouldUseLastValue() {
        request.setUrl("https://url1.com");
        request.setUrl("https://url2.com");
        request.setUrl("https://url3.com");

        assertEquals("https://url3.com", request.getUrl());
    }

    // ==================== 不同 HTTP 方法测试 ====================

    @Test
    @DisplayName("HTTP方法: GET 请求应正确设置")
    void httpMethod_Get_ShouldBeCorrect() {
        request.setMethod(HttpMethod.GET);
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    @DisplayName("HTTP方法: POST 请求应正确设置")
    void httpMethod_Post_ShouldBeCorrect() {
        request.setMethod(HttpMethod.POST);
        assertEquals(HttpMethod.POST, request.getMethod());
    }

    @Test
    @DisplayName("HTTP方法: PUT 请求应正确设置")
    void httpMethod_Put_ShouldBeCorrect() {
        request.setMethod(HttpMethod.PUT);
        assertEquals(HttpMethod.PUT, request.getMethod());
    }

    // ==================== 不同 Content-Type 测试 ====================

    @Test
    @DisplayName("Content-Type: JSON 应正确设置")
    void contentType_Json_ShouldBeCorrect() {
        request.setContentType(ContentType.JSON);
        assertEquals(ContentType.JSON, request.getContentType());
    }

    @Test
    @DisplayName("Content-Type: FORM 应正确设置")
    void contentType_Form_ShouldBeCorrect() {
        request.setContentType(ContentType.FORM);
        assertEquals(ContentType.FORM, request.getContentType());
    }

    @Test
    @DisplayName("Content-Type: XML 应正确设置")
    void contentType_Xml_ShouldBeCorrect() {
        request.setContentType(ContentType.XML);
        assertEquals(ContentType.XML, request.getContentType());
    }

    @Test
    @DisplayName("Content-Type: RAW 应正确设置")
    void contentType_Raw_ShouldBeCorrect() {
        request.setContentType(ContentType.RAW);
        assertEquals(ContentType.RAW, request.getContentType());
    }

    // ==================== Headers 多值测试 ====================

    @Test
    @DisplayName("Headers: 应支持多值 header")
    void headers_MultipleValues_ShouldBeSupported() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Accept", Arrays.asList("application/json", "application/xml"));
        request.setHeaders(headers);

        assertEquals(2, request.getHeaders().get("Accept").size());
        assertTrue(request.getHeaders().get("Accept").contains("application/json"));
        assertTrue(request.getHeaders().get("Accept").contains("application/xml"));
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: URL 包含特殊字符应正确处理")
    void url_SpecialCharacters_ShouldBeCorrect() {
        String url = "https://api.example.com/path?param=value&other=测试";
        request.setUrl(url);

        assertEquals(url, request.getUrl());
    }

    @Test
    @DisplayName("特殊字符: Body 包含特殊字符应正确处理")
    void body_SpecialCharacters_ShouldBeCorrect() {
        String body = "{\"name\":\"测试用户\",\"description\":\"特殊字符: !@#$%^&*()\"}";
        request.setBody(body);

        assertEquals(body, request.getBody());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: 设置空字符串应正确")
    void emptyString_ShouldBeSet() {
        request.setUrl("");
        assertEquals("", request.getUrl());

        request.setBody("");
        assertEquals("", request.getBody());
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

        request.setBody(longBody);
        assertEquals(longBody, request.getBody());
        assertEquals(1000, request.getBody().length());
    }
}
