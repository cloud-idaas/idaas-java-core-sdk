package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigException 单元测试
 */
class ConfigExceptionTest {

    @Test
    @DisplayName("单消息参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "Configuration error";

        ConfigException exception = new ConfigException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "CONFIG_ERROR";
        String message = "Invalid configuration";

        ConfigException exception = new ConfigException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Config load failed";
        Throwable cause = new RuntimeException("File not found");

        ConfigException exception = new ConfigException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "CONFIG_INVALID";
        String message = "Configuration invalid";
        Throwable cause = new RuntimeException("Parse error");

        ConfigException exception = new ConfigException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getErrorMessage());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应继承 ClientException")
    void shouldExtendClientException() {
        ConfigException exception = new ConfigException("test");

        assertTrue(exception instanceof ClientException);
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        ConfigException exception = new ConfigException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
