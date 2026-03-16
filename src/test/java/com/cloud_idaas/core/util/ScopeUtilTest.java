package com.cloud_idaas.core.util;

import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScopeUtil 单元测试
 */
class ScopeUtilTest {

    // ==================== validateScope 测试 ====================

    @Test
    @DisplayName("validateScope: 有效的 scope 应通过验证")
    void validateScope_WithValidScope_ShouldPass() {
        assertDoesNotThrow(() -> ScopeUtil.validateScope("app1|openid app1|profile"));
    }

    @Test
    @DisplayName("validateScope: 单个有效的 scope 应通过验证")
    void validateScope_WithSingleValidScope_ShouldPass() {
        assertDoesNotThrow(() -> ScopeUtil.validateScope("app1|openid"));
    }

    @Test
    @DisplayName("validateScope: 空 scope 应抛出异常")
    void validateScope_WithEmptyScope_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                ScopeUtil.validateScope("")
        );
        assertEquals(ErrorCode.INVALID_SCOPE.getCode(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Scope is empty"));
    }

    @Test
    @DisplayName("validateScope: null scope 应抛出异常")
    void validateScope_WithNullScope_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                ScopeUtil.validateScope(null)
        );
        assertEquals(ErrorCode.INVALID_SCOPE.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("validateScope: 无效的 scope 格式应抛出异常")
    void validateScope_WithInvalidScopeFormat_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                ScopeUtil.validateScope("invalid_scope")
        );
        assertEquals(ErrorCode.INVALID_SCOPE.getCode(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid scope"));
    }

    @Test
    @DisplayName("validateScope: 多个 audience 应抛出异常")
    void validateScope_WithMultipleAudiences_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class, () ->
                ScopeUtil.validateScope("app1|openid app2|profile")
        );
        assertEquals(ErrorCode.MULTIPLE_AUDIENCE_NOT_SUPPORTED.getCode(), exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Multiple audiences are not supported"));
    }

    // ==================== splitScope 测试 ====================

    @Test
    @DisplayName("splitScope: 正常分割 scope 字符串")
    void splitScope_WithValidScope_ShouldReturnSortedList() {
        List<String> result = ScopeUtil.splitScope("app1|profile app1|openid");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("app1|openid", result.get(0)); // 按字母顺序排序
        assertEquals("app1|profile", result.get(1));
    }

    @Test
    @DisplayName("splitScope: 空字符串应返回空列表")
    void splitScope_WithEmptyString_ShouldReturnEmptyList() {
        List<String> result = ScopeUtil.splitScope("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("splitScope: null 应返回空列表")
    void splitScope_WithNull_ShouldReturnEmptyList() {
        List<String> result = ScopeUtil.splitScope(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("splitScope: 多个空格应正确处理")
    void splitScope_WithMultipleSpaces_ShouldHandleCorrectly() {
        List<String> result = ScopeUtil.splitScope("app1|openid   app1|profile    app1|email");

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("splitScope: 前后空格应被去除")
    void splitScope_WithLeadingTrailingSpaces_ShouldTrim() {
        List<String> result = ScopeUtil.splitScope("  app1|openid  ");

        assertEquals(1, result.size());
        assertEquals("app1|openid", result.get(0));
    }

    // ==================== isValidScope 测试 ====================

    @ParameterizedTest
    @CsvSource({
            "app|openid, true",
            "app|profile, true",
            "app|scope1, true",
            "my-app|read, true",
            "app|openid|extra, false",
            "invalid, false",
            "|openid, false",
            "app|, false",
            "appopenid, false"
    })
    @DisplayName("isValidScope: 验证 scope 格式")
    void isValidScope_ShouldValidateFormat(String scope, boolean expected) {
        boolean result = ScopeUtil.isValidScope(scope);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("isValidScope: 空字符串应返回 false")
    void isValidScope_WithEmptyString_ShouldReturnFalse() {
        assertFalse(ScopeUtil.isValidScope(""));
    }

    @Test
    @DisplayName("isValidScope: null 应返回 false")
    void isValidScope_WithNull_ShouldReturnFalse() {
        assertFalse(ScopeUtil.isValidScope(null));
    }
}
