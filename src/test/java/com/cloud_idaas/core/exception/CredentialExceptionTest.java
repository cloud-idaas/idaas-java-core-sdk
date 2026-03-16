package com.cloud_idaas.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CredentialException 单元测试
 */
class CredentialExceptionTest {

    @Test
    @DisplayName("默认构造函数应创建空异常")
    void defaultConstructor_ShouldCreateEmptyException() {
        CredentialException exception = new CredentialException();

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("单参数构造函数应设置消息")
    void singleMessageConstructor_ShouldSetMessage() {
        String message = "Credential validation failed";

        CredentialException exception = new CredentialException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("双参数构造函数应设置错误码和消息")
    void errorCodeAndMessageConstructor_ShouldSetBoth() {
        String errorCode = "CREDENTIAL_ERROR";
        String message = "Invalid credentials";

        CredentialException exception = new CredentialException(errorCode, message);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("cause构造函数应设置原因")
    void causeConstructor_ShouldSetCause() {
        Throwable cause = new RuntimeException("Root cause");

        CredentialException exception = new CredentialException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("message和cause构造函数应设置两者")
    void messageAndCauseConstructor_ShouldSetBoth() {
        String message = "Credential error";
        Throwable cause = new RuntimeException("Root cause");

        CredentialException exception = new CredentialException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("完整构造函数应设置所有字段")
    void fullConstructor_ShouldSetAllFields() {
        String errorCode = "CREDENTIAL_INVALID";
        String message = "Credentials invalid";
        Throwable cause = new RuntimeException("Root cause");

        CredentialException exception = new CredentialException(errorCode, message, cause);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("应正确获取 errorCode")
    void errorCode_ShouldGetCorrectly() {
        String errorCode = "CUSTOM_CREDENTIAL";
        CredentialException exception = new CredentialException(errorCode, "message");

        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("getMessage 应返回父类消息")
    void getMessage_ShouldReturnParentMessage() {
        String message = "Test message";
        CredentialException exception = new CredentialException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("应继承 RuntimeException")
    void shouldExtendRuntimeException() {
        CredentialException exception = new CredentialException();

        assertTrue(exception instanceof RuntimeException);
    }
}
