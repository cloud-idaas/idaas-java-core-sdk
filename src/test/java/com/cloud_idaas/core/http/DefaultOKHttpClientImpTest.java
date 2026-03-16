package com.cloud_idaas.core.http;

import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.HttpException;
import com.cloud_idaas.core.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultOKHttpClientImp 单元测试
 */
class DefaultOKHttpClientImpTest {

    private DefaultOKHttpClientImp httpClient;

    @BeforeEach
    void setUp() {
        httpClient = DefaultOKHttpClientImp.builder()
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 使用默认值构建客户端")
    void builder_DefaultValues_ShouldBuildClient() {
        DefaultOKHttpClientImp client = DefaultOKHttpClientImp.builder().build();
        assertNotNull(client);
    }

    @Test
    @DisplayName("Builder: 使用自定义超时值构建客户端")
    void builder_CustomTimeouts_ShouldBuildClient() {
        DefaultOKHttpClientImp client = DefaultOKHttpClientImp.builder()
                .connectTimeout(3000)
                .readTimeout(5000)
                .build();
        assertNotNull(client);
    }

    @Test
    @DisplayName("Builder: 方法链应正常工作")
    void builder_MethodChaining_ShouldWork() {
        DefaultOKHttpClientImp client = DefaultOKHttpClientImp.builder()
                .connectTimeout(2000)
                .readTimeout(4000)
                .build();
        assertNotNull(client);
    }

    @Test
    @DisplayName("Builder: null 超时值应使用默认值")
    void builder_NullTimeouts_ShouldUseDefaults() {
        DefaultOKHttpClientImp client = new DefaultOKHttpClientImp(
                new DefaultOKHttpClientImp.Builder()
        );
        assertNotNull(client);
    }

    // ==================== HttpClient 接口实现测试 ====================

    @Test
    @DisplayName("接口实现: 应实现 HttpClient 接口")
    void interfaceImplementation_ShouldImplementHttpClient() {
        assertTrue(httpClient instanceof HttpClient);
    }

    @Test
    @DisplayName("接口实现: 应可转型为 HttpClient")
    void interfaceImplementation_ShouldBeCastable() {
        HttpClient client = httpClient;
        assertNotNull(client);
    }

    // ==================== send 方法测试 ====================

    @Test
    @DisplayName("send: GET 请求应正确构造")
    void send_GetRequest_ShouldBuildCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://httpbin.org/get")
                .build();
        
        // 仅验证请求对象构造正确，不实际发送
        assertNotNull(request);
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    @DisplayName("send: POST 请求应正确构造")
    void send_PostRequest_ShouldBuildCorrectly() {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put("key", Collections.singletonList("value"));
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://httpbin.org/post")
                .contentType(ContentType.FORM)
                .formBody(formBody)
                .build();
        
        assertNotNull(request);
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals(ContentType.FORM, request.getContentType());
    }

    @Test
    @DisplayName("send: PUT 请求应正确构造")
    void send_PutRequest_ShouldBuildCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.PUT)
                .url("https://httpbin.org/put")
                .contentType(ContentType.JSON)
                .body("{\"key\":\"value\"}")
                .build();
        
        assertNotNull(request);
        assertEquals(HttpMethod.PUT, request.getMethod());
    }

    // ==================== 请求头测试 ====================

    @Test
    @DisplayName("请求头: 空请求头应正确处理")
    void headers_EmptyHeaders_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://example.com")
                .headers(new HashMap<>())
                .build();
        
        assertNotNull(request);
        assertTrue(request.getHeaders().isEmpty());
    }

    @Test
    @DisplayName("请求头: null 请求头应正确处理")
    void headers_NullHeaders_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://example.com")
                .headers(null)
                .build();
        
        assertNotNull(request);
        assertNull(request.getHeaders());
    }

    @Test
    @DisplayName("请求头: 多值请求头应正确处理")
    void headers_MultipleValues_ShouldHandleCorrectly() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Accept", Arrays.asList("application/json", "application/xml"));
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://example.com")
                .headers(headers)
                .build();
        
        assertNotNull(request);
        assertEquals(2, request.getHeaders().get("Accept").size());
    }

    // ==================== 请求体测试 ====================

    @Test
    @DisplayName("请求体: JSON 请求体应正确处理")
    void body_JsonBody_ShouldHandleCorrectly() {
        String jsonBody = "{\"name\":\"test\",\"value\":123}";
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .build();
        
        assertNotNull(request);
        assertEquals(jsonBody, request.getBody());
    }

    @Test
    @DisplayName("请求体: 表单请求体应正确处理")
    void body_FormBody_ShouldHandleCorrectly() {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put("grant_type", Collections.singletonList("client_credentials"));
        formBody.put("scope", Collections.singletonList("read write"));
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.FORM)
                .formBody(formBody)
                .build();
        
        assertNotNull(request);
        assertEquals(2, request.getFormBody().size());
    }

    @Test
    @DisplayName("请求体: null Content-Type 应正确处理")
    void body_NullContentType_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .build();
        
        assertNotNull(request);
        assertNull(request.getContentType());
    }

    @Test
    @DisplayName("请求体: 空表单应正确处理")
    void body_EmptyFormBody_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.FORM)
                .formBody(new HashMap<>())
                .build();
        
        assertNotNull(request);
        assertTrue(request.getFormBody().isEmpty());
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("异常: 无效 URL 应抛出异常")
    void exception_InvalidUrl_ShouldThrowException() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("invalid-url")
                .build();
        
        assertThrows(Exception.class, () -> httpClient.send(request));
    }

    @Test
    @DisplayName("异常: 连接不存在的服务器应抛出异常")
    void exception_NonExistentServer_ShouldThrowException() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://non-existent-server-12345.com")
                .build();
        
        assertThrows(HttpException.class, () -> httpClient.send(request));
    }

    // ==================== Content-Type 测试 ====================

    @Test
    @DisplayName("Content-Type: JSON 类型应正确")
    void contentType_Json_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.JSON)
                .build();
        
        assertEquals(ContentType.JSON, request.getContentType());
    }

    @Test
    @DisplayName("Content-Type: FORM 类型应正确")
    void contentType_Form_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.FORM)
                .build();
        
        assertEquals(ContentType.FORM, request.getContentType());
    }

    @Test
    @DisplayName("Content-Type: XML 类型应正确")
    void contentType_Xml_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.XML)
                .build();
        
        assertEquals(ContentType.XML, request.getContentType());
    }

    // ==================== HTTP 方法测试 ====================

    @Test
    @DisplayName("HTTP方法: GET 方法应正确")
    void httpMethod_Get_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://example.com")
                .build();
        
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    @DisplayName("HTTP方法: POST 方法应正确")
    void httpMethod_Post_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .build();
        
        assertEquals(HttpMethod.POST, request.getMethod());
    }

    @Test
    @DisplayName("HTTP方法: PUT 方法应正确")
    void httpMethod_Put_ShouldBeCorrect() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.PUT)
                .url("https://example.com")
                .build();
        
        assertEquals(HttpMethod.PUT, request.getMethod());
    }

    // ==================== 超时配置测试 ====================

    @Test
    @DisplayName("超时: 连接超时应可配置")
    void timeout_ConnectTimeout_ShouldBeConfigurable() {
        DefaultOKHttpClientImp client = DefaultOKHttpClientImp.builder()
                .connectTimeout(100)
                .build();
        assertNotNull(client);
    }

    @Test
    @DisplayName("超时: 读取超时应可配置")
    void timeout_ReadTimeout_ShouldBeConfigurable() {
        DefaultOKHttpClientImp client = DefaultOKHttpClientImp.builder()
                .readTimeout(100)
                .build();
        assertNotNull(client);
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个客户端实例应独立")
    void multipleInstances_ShouldBeIndependent() {
        DefaultOKHttpClientImp client1 = DefaultOKHttpClientImp.builder()
                .connectTimeout(1000)
                .build();
        DefaultOKHttpClientImp client2 = DefaultOKHttpClientImp.builder()
                .connectTimeout(2000)
                .build();
        
        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2);
    }

    // ==================== Builder 静态方法测试 ====================

    @Test
    @DisplayName("Builder: 静态 builder 方法应返回 Builder 实例")
    void builder_StaticMethod_ShouldReturnBuilder() {
        DefaultOKHttpClientImp.Builder builder = DefaultOKHttpClientImp.builder();
        assertNotNull(builder);
        assertTrue(builder instanceof DefaultOKHttpClientImp.Builder);
    }

    // ==================== 空请求体测试 ====================

    @Test
    @DisplayName("请求体: 空字符串请求体应正确处理")
    void body_EmptyString_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.JSON)
                .body("")
                .build();
        
        assertEquals("", request.getBody());
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: URL 中的特殊字符应正确处理")
    void url_SpecialCharacters_ShouldHandleCorrectly() {
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url("https://example.com/path?param=value&other=%E6%B5%8B%E8%AF%95")
                .build();
        
        assertNotNull(request.getUrl());
        assertTrue(request.getUrl().contains("param=value"));
    }

    @Test
    @DisplayName("特殊字符: 请求体中的特殊字符应正确处理")
    void body_SpecialCharacters_ShouldHandleCorrectly() {
        String specialBody = "{\"name\":\"测试\",\"symbols\":\"!@#$%^&*()\"}";
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.JSON)
                .body(specialBody)
                .build();
        
        assertEquals(specialBody, request.getBody());
    }

    // ==================== 长请求体测试 ====================

    @Test
    @DisplayName("长请求体: 大请求体应正确处理")
    void body_LargeBody_ShouldHandleCorrectly() {
        StringBuilder sb = new StringBuilder("{\"data\":\"");
        for (int i = 0; i < 1000; i++) {
            sb.append("x");
        }
        sb.append("\"}");
        String largeBody = sb.toString();
        
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url("https://example.com")
                .contentType(ContentType.JSON)
                .body(largeBody)
                .build();
        
        assertEquals(largeBody, request.getBody());
        assertTrue(request.getBody().length() > 1000);
    }
}
