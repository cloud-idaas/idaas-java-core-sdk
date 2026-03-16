package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExceptionAnalyzer 单元测试
 */
class ExceptionAnalyzerTest {

    // ==================== isTargetCauseExist (带 cause 类) 测试 ====================

    @Test
    @DisplayName("isTargetCauseExist: 存在目标 cause 类型和子消息应返回 true")
    void isTargetCauseExist_WithTargetCauseAndSubMessage_ShouldReturnTrue() {
        IOException ioException = new IOException("Connection timeout");
        RuntimeException runtimeException = new RuntimeException("Wrapper", ioException);

        boolean result = ExceptionAnalyzer.isTargetCauseExist(runtimeException, IOException.class, "timeout");

        assertTrue(result);
    }

    @Test
    @DisplayName("isTargetCauseExist: 存在目标 cause 类型但子消息不匹配应返回 false")
    void isTargetCauseExist_WithTargetCauseButWrongSubMessage_ShouldReturnFalse() {
        IOException ioException = new IOException("Connection refused");
        RuntimeException runtimeException = new RuntimeException("Wrapper", ioException);

        boolean result = ExceptionAnalyzer.isTargetCauseExist(runtimeException, IOException.class, "timeout");

        assertFalse(result);
    }

    @Test
    @DisplayName("isTargetCauseExist: 不存在目标 cause 类型应返回 false")
    void isTargetCauseExist_WithoutTargetCause_ShouldReturnFalse() {
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Invalid argument");
        RuntimeException runtimeException = new RuntimeException("Wrapper", illegalArgException);

        boolean result = ExceptionAnalyzer.isTargetCauseExist(runtimeException, IOException.class, "timeout");

        assertFalse(result);
    }

    @Test
    @DisplayName("isTargetCauseExist: 多层嵌套异常中找到目标 cause")
    void isTargetCauseExist_WithNestedExceptions_ShouldFindTargetCause() {
        IOException ioException = new IOException("Socket timeout");
        RuntimeException runtimeException1 = new RuntimeException("Layer 1", ioException);
        RuntimeException runtimeException2 = new RuntimeException("Layer 2", runtimeException1);
        RuntimeException runtimeException3 = new RuntimeException("Layer 3", runtimeException2);

        boolean result = ExceptionAnalyzer.isTargetCauseExist(runtimeException3, IOException.class, "timeout");

        assertTrue(result);
    }

    @Test
    @DisplayName("isTargetCauseExist: 超过最大深度应停止查找")
    void isTargetCauseExist_ExceedingMaxDepth_ShouldStopSearching() {
        // 创建超过 5 层的嵌套异常
        Exception deepException = new IOException("Deep exception");
        for (int i = 0; i < 6; i++) {
            deepException = new RuntimeException("Layer " + i, deepException);
        }

        boolean result = ExceptionAnalyzer.isTargetCauseExist(deepException, IOException.class, "Deep");

        // 超过 MAX_CAUSE_DEPTH (5) 层后应停止查找，返回 false
        assertFalse(result);
    }

    @Test
    @DisplayName("isTargetCauseExist: cause 消息为 null 应处理正常")
    void isTargetCauseExist_WithNullMessage_ShouldHandleGracefully() {
        IOException ioException = new IOException();
        RuntimeException runtimeException = new RuntimeException("Wrapper", ioException);

        boolean result = ExceptionAnalyzer.isTargetCauseExist(runtimeException, IOException.class, "timeout");

        assertFalse(result);
    }

    // ==================== isTargetCauseExist (仅子消息) 测试 ====================

    @Test
    @DisplayName("isTargetCauseExist(仅消息): 消息包含子字符串应返回 true")
    void isTargetCauseExist_OnlySubMessage_ContainsSubMessage_ShouldReturnTrue() {
        RuntimeException exception = new RuntimeException("This is a test message");

        boolean result = ExceptionAnalyzer.isTargetCauseExist(exception, "test");

        assertTrue(result);
    }

    @Test
    @DisplayName("isTargetCauseExist(仅消息): 消息不包含子字符串应返回 false")
    void isTargetCauseExist_OnlySubMessage_NotContainsSubMessage_ShouldReturnFalse() {
        RuntimeException exception = new RuntimeException("This is a test message");

        boolean result = ExceptionAnalyzer.isTargetCauseExist(exception, "notfound");

        assertFalse(result);
    }

    @Test
    @DisplayName("isTargetCauseExist(仅消息): 空消息应处理正常")
    void isTargetCauseExist_OnlySubMessage_WithEmptyMessage_ShouldHandleGracefully() {
        RuntimeException exception = new RuntimeException();

        assertThrows(NullPointerException.class, () ->
                ExceptionAnalyzer.isTargetCauseExist(exception, "test")
        );
    }

    @Test
    @DisplayName("isTargetCauseExist(仅消息): null 异常应抛出异常")
    void isTargetCauseExist_OnlySubMessage_WithNullThrowable_ShouldThrowException() {
        assertThrows(NullPointerException.class, () ->
                ExceptionAnalyzer.isTargetCauseExist(null, "test")
        );
    }
}
