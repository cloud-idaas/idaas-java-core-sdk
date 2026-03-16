package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EncodingException 单元测试
 */
class EncodingExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        EncodingException exception = new EncodingException();

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
        assertNull(exception.getErrorMessage());
    }

    @Test
    @DisplayName("单参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "Encoding failed";

        EncodingException exception = new EncodingException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "ENCODING_ERROR";
        String message = "Invalid encoding";

        EncodingException exception = new EncodingException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Encoding error";
        Throwable cause = new RuntimeException("Root cause");

        EncodingException exception = new EncodingException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "ENCODING_FAIL";
        String message = "Encoding failure";
        Throwable cause = new RuntimeException("Root cause");

        EncodingException exception = new EncodingException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应正确设置和获取 errorCode")
    void errorCode_ShouldSetAndGetCorrectly() {
        EncodingException exception = new EncodingException();
        String errorCode = "CUSTOM_ENCODING";

        exception.setErrorCode(errorCode);

        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("应正确设置和获取 errorMessage")
    void errorMessage_ShouldSetAndGetCorrectly() {
        EncodingException exception = new EncodingException();
        String errorMessage = "Custom encoding error";

        exception.setErrorMessage(errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        EncodingException exception = new EncodingException();

        assertTrue(exception instanceof RuntimeException);
    }
}
