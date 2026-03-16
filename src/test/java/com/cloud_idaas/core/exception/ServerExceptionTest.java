package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ServerException 单元测试
 */
class ServerExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        ServerException exception = new ServerException();

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
        assertNull(exception.getErrorMessage());
        assertNull(exception.getRequestId());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "SERVER_ERROR";
        String message = "Internal server error";

        ServerException exception = new ServerException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("三参数构造函数应设置错误码、消息和请求ID")
    void threeParameterConstructor_ShouldSetAll() {
        String errorCode = "INTERNAL_ERROR";
        String message = "Server encountered an error";
        String requestId = "req-server-123";

        ServerException exception = new ServerException(errorCode, message, requestId);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(requestId, exception.getRequestId());
    }

    @Test
    @DisplayName("cause构造函数应设置原因")
    void causeConstructor_ShouldSetCause() {
        Throwable cause = new RuntimeException("Database connection failed");

        ServerException exception = new ServerException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Server failure";
        Throwable cause = new RuntimeException("Service unavailable");

        ServerException exception = new ServerException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "SERVER_FAIL";
        String message = "Server operation failed";
        Throwable cause = new RuntimeException("Backend error");

        ServerException exception = new ServerException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应正确设置和获取 errorCode")
    void errorCode_ShouldSetAndGetCorrectly() {
        ServerException exception = new ServerException();
        String errorCode = "CUSTOM_SERVER_ERROR";

        exception.setErrorCode(errorCode);

        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("应正确设置和获取 errorMessage")
    void errorMessage_ShouldSetAndGetCorrectly() {
        ServerException exception = new ServerException();
        String errorMessage = "Custom server error message";

        exception.setErrorMessage(errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
    }

    @Test
    @DisplayName("应正确设置和获取 requestId")
    void requestId_ShouldSetAndGetCorrectly() {
        ServerException exception = new ServerException();
        String requestId = "req-server-456";

        exception.setRequestId(requestId);

        assertEquals(requestId, exception.getRequestId());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        ServerException exception = new ServerException();

        assertTrue(exception instanceof RuntimeException);
    }
}
