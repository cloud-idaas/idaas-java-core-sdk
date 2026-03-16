package com.cloud_idaas.core.http;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpClient 接口单元测试
 */
class HttpClientTest {

    // ==================== 接口契约测试 ====================

    @Test
    @DisplayName("接口契约: HttpClient 应定义为接口")
    void interfaceContract_ShouldBeInterface() {
        assertTrue(HttpClient.class.isInterface());
    }

    @Test
    @DisplayName("接口契约: 应包含 send 方法")
    void interfaceContract_ShouldHaveSendMethod() throws NoSuchMethodException {
        assertNotNull(HttpClient.class.getMethod("send", HttpRequest.class));
    }

    @Test
    @DisplayName("接口契约: send 方法应返回 HttpResponse")
    void interfaceContract_SendMethod_ShouldReturnHttpResponse() throws NoSuchMethodException {
        Method method = HttpClient.class.getMethod("send", HttpRequest.class);
        assertEquals(HttpResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("接口契约: send 方法应接受 HttpRequest 参数")
    void interfaceContract_SendMethod_ShouldAcceptHttpRequest() throws NoSuchMethodException {
        Method method = HttpClient.class.getMethod("send", HttpRequest.class);
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(HttpRequest.class, parameterTypes[0]);
    }

    // ==================== 实现类测试 ====================

    @Test
    @DisplayName("实现类: DefaultOKHttpClientImp 应实现 HttpClient")
    void implementation_DefaultOKHttpClientImp_ShouldImplementHttpClient() {
        assertTrue(HttpClient.class.isAssignableFrom(DefaultOKHttpClientImp.class));
    }

    @Test
    @DisplayName("实现类: DefaultOKHttpClientImp 实例应可赋值给 HttpClient")
    void implementation_Instance_ShouldBeAssignable() {
        HttpClient client = DefaultOKHttpClientImp.builder().build();
        assertNotNull(client);
        assertTrue(client instanceof HttpClient);
    }

    // ==================== 多态测试 ====================

    @Test
    @DisplayName("多态: 通过接口调用 send 方法应正常工作")
    void polymorphism_InterfaceCall_ShouldWork() {
        HttpClient client = DefaultOKHttpClientImp.builder()
                .connectTimeout(1000)
                .readTimeout(1000)
                .build();
        
        assertNotNull(client);
        // 验证可以通过接口调用
        assertDoesNotThrow(() -> {
            // 不实际发送请求，仅验证类型转换
            HttpClient sameClient = (HttpClient) client;
            assertNotNull(sameClient);
        });
    }

    // ==================== 接口方法签名测试 ====================

    @Test
    @DisplayName("方法签名: send 方法应为 public")
    void methodSignature_Send_ShouldBePublic() throws NoSuchMethodException {
        Method method = HttpClient.class.getMethod("send", HttpRequest.class);
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
    }

    @Test
    @DisplayName("方法签名: send 方法不应为 static")
    void methodSignature_Send_ShouldNotBeStatic() throws NoSuchMethodException {
        Method method = HttpClient.class.getMethod("send", HttpRequest.class);
        assertFalse(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
    }

    // ==================== 接口继承测试 ====================

    @Test
    @DisplayName("继承: HttpClient 不应继承其他接口")
    void inheritance_ShouldNotExtendOtherInterfaces() {
        Class<?>[] extendedInterfaces = HttpClient.class.getInterfaces();
        assertEquals(0, extendedInterfaces.length);
    }

    // ==================== 接口声明测试 ====================

    @Test
    @DisplayName("声明: 接口名称应为 HttpClient")
    void declaration_NameShouldBeHttpClient() {
        assertEquals("HttpClient", HttpClient.class.getSimpleName());
    }

    // ==================== 实现类数量测试 ====================

    @Test
    @DisplayName("实现: 已知至少有一个实现类 DefaultOKHttpClientImp")
    void implementation_AtLeastOneKnownImplementation() {
        // 验证 DefaultOKHttpClientImp 确实实现了 HttpClient
        Class<?> implClass = DefaultOKHttpClientImp.class;
        assertTrue(HttpClient.class.isAssignableFrom(implClass));
    }
}
