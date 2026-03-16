package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConcurrentOperationException 单元测试
 */
class ConcurrentOperationExceptionTest {

    @Test
    @DisplayName("默认构造函数应设置默认错误码和消息")
    void defaultConstructor_ShouldSetDefaultErrorCodeAndMessage() {
        ConcurrentOperationException exception = new ConcurrentOperationException();

        assertEquals("ConcurrentOperationFailed", exception.getErrorCode());
        assertEquals("A concurrent operation is in progress, causing the current operation to fail.", exception.getErrorMessage());
        assertEquals("A concurrent operation is in progress, causing the current operation to fail.", exception.getMessage());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "CUSTOM_CONCURRENT";
        String message = "Custom concurrent error";

        ConcurrentOperationException exception = new ConcurrentOperationException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("带cause的构造函数应设置所有字段")
    void constructorWithCause_ShouldSetAllFields() {
        String errorCode = "CONCURRENT_FAIL";
        String message = "Concurrent operation failed";
        Throwable cause = new RuntimeException("Root cause");

        ConcurrentOperationException exception = new ConcurrentOperationException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应继承 ClientException")
    void shouldExtendClientException() {
        ConcurrentOperationException exception = new ConcurrentOperationException();

        assertTrue(exception instanceof ClientException);
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        ConcurrentOperationException exception = new ConcurrentOperationException();

        assertTrue(exception instanceof RuntimeException);
    }
}
