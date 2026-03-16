package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheException 单元测试
 */
class CacheExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        CacheException exception = new CacheException();

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
        assertNull(exception.getErrorMessage());
    }

    @Test
    @DisplayName("单参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "Cache operation failed";

        CacheException exception = new CacheException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "CACHE_ERROR";
        String message = "Cache miss";

        CacheException exception = new CacheException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("cause构造函数应设置原因")
    void causeConstructor_ShouldSetCause() {
        Throwable cause = new RuntimeException("Root cause");

        CacheException exception = new CacheException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Cache error";
        Throwable cause = new RuntimeException("Root cause");

        CacheException exception = new CacheException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "CACHE_FULL";
        String message = "Cache is full";
        Throwable cause = new RuntimeException("Root cause");

        CacheException exception = new CacheException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应正确设置和获取 errorCode")
    void errorCode_ShouldSetAndGetCorrectly() {
        CacheException exception = new CacheException();
        String errorCode = "CUSTOM_ERROR";

        exception.setErrorCode(errorCode);

        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("应正确设置和获取 errorMessage")
    void errorMessage_ShouldSetAndGetCorrectly() {
        CacheException exception = new CacheException();
        String errorMessage = "Custom error message";

        exception.setErrorMessage(errorMessage);

        assertEquals(errorMessage, exception.getErrorMessage());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        CacheException exception = new CacheException();

        assertTrue(exception instanceof RuntimeException);
    }
}
