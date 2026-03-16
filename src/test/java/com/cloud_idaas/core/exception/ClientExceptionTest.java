package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientException 单元测试
 */
class ClientExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        ClientException exception = new ClientException();

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
        assertNull(exception.getErrorMessage());
        assertNull(exception.getRequestId());
    }

    @Test
    @DisplayName("单参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "Client error occurred";

        ClientException exception = new ClientException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "CLIENT_ERROR";
        String message = "Invalid client configuration";

        ClientException exception = new ClientException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("三参数构造函数应设置错误码、消息和请求ID")
    void threeParameterConstructor_ShouldSetAll() {
        String errorCode = "REQUEST_FAILED";
        String message = "Request failed";
        String requestId = "req-12345";

        ClientException exception = new ClientException(errorCode, message, requestId);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(requestId, exception.getRequestId());
    }

    @Test
    @DisplayName("cause构造函数应设置原因")
    void causeConstructor_ShouldSetCause() {
        Throwable cause = new RuntimeException("Root cause");

        ClientException exception = new ClientException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Client error";
        Throwable cause = new RuntimeException("Root cause");

        ClientException exception = new ClientException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("errorCode、message和cause构造函数应设置三者")
    void errorCodeMessageAndCauseConstructor_ShouldSetAll() {
        String errorCode = "CLIENT_FAIL";
        String message = "Client failure";
        Throwable cause = new RuntimeException("Root cause");

        ClientException exception = new ClientException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "FULL_ERROR";
        String message = "Complete error";
        String requestId = "req-67890";
        Throwable cause = new RuntimeException("Root cause");

        ClientException exception = new ClientException(errorCode, message, requestId, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(requestId, exception.getRequestId());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应正确设置和获取 errorCode")
    void errorCode_ShouldSetAndGetCorrectly() {
        ClientException exception = new ClientException();
        String errorCode = "CUSTOM_CODE";

        exception.setErrorCode(errorCode);

        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("应正确设置和获取 errorMessage")
    void errorMessage_ShouldSetAndGetCorrectly() {
        ClientException exception = new ClientException();
        String errorMessage = "Custom message";

        exception.setErrorMessage(errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
    }

    @Test
    @DisplayName("应正确设置和获取 requestId")
    void requestId_ShouldSetAndGetCorrectly() {
        ClientException exception = new ClientException();
        String requestId = "req-abc-123";

        exception.setRequestId(requestId);

        assertEquals(requestId, exception.getRequestId());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        ClientException exception = new ClientException();

        assertTrue(exception instanceof RuntimeException);
    }
}
