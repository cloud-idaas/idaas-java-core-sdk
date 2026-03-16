package com.cloud_idaas.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ErrResponse 单元测试
 */
class ErrResponseTest {

    private ErrResponse response;

    @BeforeEach
    void setUp() {
        response = new ErrResponse();
    }

    // ==================== 默认构造函数测试 ====================

    @Test
    @DisplayName("默认构造函数: 所有字段默认应为 null")
    void defaultConstructor_AllFields_ShouldBeNull() {
        assertNull(response.getError());
        assertNull(response.getErrorDescription());
        assertNull(response.getRequestId());
    }

    // ==================== 带参数构造函数测试 ====================

    @Test
    @DisplayName("带参数构造函数: 应正确设置所有字段")
    void parameterizedConstructor_AllFields_ShouldBeSet() {
        ErrResponse errResponse = new ErrResponse("invalid_request", "The request is invalid", "req-123");

        assertEquals("invalid_request", errResponse.getError());
        assertEquals("The request is invalid", errResponse.getErrorDescription());
        assertEquals("req-123", errResponse.getRequestId());
    }

    @Test
    @DisplayName("带参数构造函数: 允许 null 参数")
    void parameterizedConstructor_NullParameters_ShouldBeSet() {
        ErrResponse errResponse = new ErrResponse(null, null, null);

        assertNull(errResponse.getError());
        assertNull(errResponse.getErrorDescription());
        assertNull(errResponse.getRequestId());
    }

    @Test
    @DisplayName("带参数构造函数: 允许部分参数为 null")
    void parameterizedConstructor_PartialNullParameters_ShouldBeSet() {
        ErrResponse errResponse = new ErrResponse("invalid_request", null, "req-123");

        assertEquals("invalid_request", errResponse.getError());
        assertNull(errResponse.getErrorDescription());
        assertEquals("req-123", errResponse.getRequestId());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 error 应正确")
    void setter_Error_ShouldBeSet() {
        response.setError("invalid_client");
        assertEquals("invalid_client", response.getError());
    }

    @Test
    @DisplayName("Setter: 设置 errorDescription 应正确")
    void setter_ErrorDescription_ShouldBeSet() {
        response.setErrorDescription("Client authentication failed");
        assertEquals("Client authentication failed", response.getErrorDescription());
    }

    @Test
    @DisplayName("Setter: 设置 requestId 应正确")
    void setter_RequestId_ShouldBeSet() {
        response.setRequestId("request-abc-123");
        assertEquals("request-abc-123", response.getRequestId());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置字段为 null 应正确")
    void nullValue_AllFields_ShouldBeSet() {
        response.setError("test");
        response.setError(null);
        assertNull(response.getError());

        response.setErrorDescription("test");
        response.setErrorDescription(null);
        assertNull(response.getErrorDescription());

        response.setRequestId("test");
        response.setRequestId(null);
        assertNull(response.getRequestId());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: 设置空字符串应正确")
    void emptyString_ShouldBeSet() {
        response.setError("");
        assertEquals("", response.getError());

        response.setErrorDescription("");
        assertEquals("", response.getErrorDescription());

        response.setRequestId("");
        assertEquals("", response.getRequestId());
    }

    // ==================== 常见错误类型测试 ====================

    @Test
    @DisplayName("错误类型: invalid_request")
    void errorType_InvalidRequest_ShouldBeSet() {
        response.setError("invalid_request");
        assertEquals("invalid_request", response.getError());
    }

    @Test
    @DisplayName("错误类型: invalid_client")
    void errorType_InvalidClient_ShouldBeSet() {
        response.setError("invalid_client");
        assertEquals("invalid_client", response.getError());
    }

    @Test
    @DisplayName("错误类型: invalid_grant")
    void errorType_InvalidGrant_ShouldBeSet() {
        response.setError("invalid_grant");
        assertEquals("invalid_grant", response.getError());
    }

    @Test
    @DisplayName("错误类型: unauthorized_client")
    void errorType_UnauthorizedClient_ShouldBeSet() {
        response.setError("unauthorized_client");
        assertEquals("unauthorized_client", response.getError());
    }

    @Test
    @DisplayName("错误类型: unsupported_grant_type")
    void errorType_UnsupportedGrantType_ShouldBeSet() {
        response.setError("unsupported_grant_type");
        assertEquals("unsupported_grant_type", response.getError());
    }

    @Test
    @DisplayName("错误类型: access_denied")
    void errorType_AccessDenied_ShouldBeSet() {
        response.setError("access_denied");
        assertEquals("access_denied", response.getError());
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
        ErrResponse response1 = new ErrResponse();
        ErrResponse response2 = new ErrResponse();

        response1.setError("error-1");
        response2.setError("error-2");

        assertEquals("error-1", response1.getError());
        assertEquals("error-2", response2.getError());
    }

    // ==================== 多次设置测试 ====================

    @Test
    @DisplayName("多次设置: 多次设置同一字段应使用最后一次的值")
    void multipleSet_SameField_ShouldUseLastValue() {
        response.setError("error-1");
        response.setError("error-2");
        response.setError("error-3");

        assertEquals("error-3", response.getError());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        response.setError("invalid_request");
        response.setErrorDescription("The request was missing a required parameter");
        response.setRequestId("req-xyz-789");

        assertEquals("invalid_request", response.getError());
        assertEquals("The request was missing a required parameter", response.getErrorDescription());
        assertEquals("req-xyz-789", response.getRequestId());
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: errorDescription 包含特殊字符应正确")
    void specialCharacters_ErrorDescription_ShouldBeSet() {
        String description = "Error: 参数 'client_id' 不能为空！\n换行测试";
        response.setErrorDescription(description);
        assertEquals(description, response.getErrorDescription());
    }

    @Test
    @DisplayName("特殊字符: requestId 包含 UUID 格式应正确")
    void specialCharacters_RequestIdUuid_ShouldBeSet() {
        String requestId = "550e8400-e29b-41d4-a716-446655440000";
        response.setRequestId(requestId);
        assertEquals(requestId, response.getRequestId());
    }

    // ==================== 长字符串测试 ====================

    @Test
    @DisplayName("长字符串: errorDescription 包含长文本应正确")
    void longString_ErrorDescription_ShouldBeSet() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longDescription = sb.toString();
        
        response.setErrorDescription(longDescription);
        assertEquals(longDescription, response.getErrorDescription());
        assertEquals(1000, response.getErrorDescription().length());
    }
}
