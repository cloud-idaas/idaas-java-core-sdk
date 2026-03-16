package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpException 单元测试
 */
class HttpExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        HttpException exception = new HttpException();

        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("单参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "HTTP request failed";

        HttpException exception = new HttpException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("cause构造函数应设置原因")
    void causeConstructor_ShouldSetCause() {
        Throwable cause = new RuntimeException("Connection timeout");

        HttpException exception = new HttpException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "HTTP error";
        Throwable cause = new RuntimeException("Connection refused");

        HttpException exception = new HttpException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("getMessage 应返回父类消息")
    void getMessage_ShouldReturnParentMessage() {
        String message = "Test HTTP message";
        HttpException exception = new HttpException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        HttpException exception = new HttpException();

        assertTrue(exception instanceof RuntimeException);
    }
}
