package com.cloud_idaas.core.provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtClientAssertionProvider 接口单元测试
 */
class JwtClientAssertionProviderTest {

    // ==================== getClientAssertion 测试 ====================

    @Test
    @DisplayName("getClientAssertion: 应返回有效的 JWT 断言字符串")
    void getClientAssertion_ShouldReturnValidJwtAssertion() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion()).thenReturn("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");

        String result = provider.getClientAssertion();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("getClientAssertion: 可能返回 null")
    void getClientAssertion_MayReturnNull() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion()).thenReturn(null);

        String result = provider.getClientAssertion();

        assertNull(result);
    }

    @Test
    @DisplayName("getClientAssertion: 可能返回空字符串")
    void getClientAssertion_MayReturnEmptyString() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion()).thenReturn("");

        String result = provider.getClientAssertion();

        assertEquals("", result);
    }

    // ==================== 函数式接口测试 ====================

    @Test
    @DisplayName("函数式接口: 可以使用 Lambda 表达式实现")
    void functionalInterface_CanBeImplementedWithLambda() {
        JwtClientAssertionProvider provider = () -> "lambda-jwt-assertion";

        assertEquals("lambda-jwt-assertion", provider.getClientAssertion());
    }

    @Test
    @DisplayName("函数式接口: 可以使用方法引用实现")
    void functionalInterface_CanBeImplementedWithMethodReference() {
        JwtClientAssertionProvider provider = this::generateAssertion;

        assertEquals("method-ref-assertion", provider.getClientAssertion());
    }

    private String generateAssertion() {
        return "method-ref-assertion";
    }

    // ==================== JWT 格式测试 ====================

    @Test
    @DisplayName("JWT 格式: 典型的 JWT 格式应包含三个部分")
    void jwtFormat_TypicalJwtShouldHaveThreeParts() {
        String typicalJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        JwtClientAssertionProvider provider = () -> typicalJwt;

        String result = provider.getClientAssertion();
        String[] parts = result.split("\\.");

        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("JWT 格式: 可以返回非标准格式的字符串")
    void jwtFormat_CanReturnNonStandardFormat() {
        JwtClientAssertionProvider provider = () -> "not-a-standard-jwt";

        String result = provider.getClientAssertion();

        assertEquals("not-a-standard-jwt", result);
    }

    // ==================== 多次调用测试 ====================

    @Test
    @DisplayName("多次调用: 每次调用可能返回不同的断言")
    void multipleCalls_MayReturnDifferentAssertions() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion())
                .thenReturn("assertion-1")
                .thenReturn("assertion-2")
                .thenReturn("assertion-3");

        assertEquals("assertion-1", provider.getClientAssertion());
        assertEquals("assertion-2", provider.getClientAssertion());
        assertEquals("assertion-3", provider.getClientAssertion());
    }

    @Test
    @DisplayName("多次调用: 可能返回相同的断言（缓存）")
    void multipleCalls_MayReturnSameAssertion() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion()).thenReturn("cached-assertion");

        assertEquals("cached-assertion", provider.getClientAssertion());
        assertEquals("cached-assertion", provider.getClientAssertion());
        assertEquals("cached-assertion", provider.getClientAssertion());

        verify(provider, times(3)).getClientAssertion();
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("异常处理: 实现可能抛出运行时异常")
    void exceptionHandling_MayThrowRuntimeException() {
        JwtClientAssertionProvider provider = mock(JwtClientAssertionProvider.class);
        when(provider.getClientAssertion()).thenThrow(new RuntimeException("Assertion generation failed"));

        assertThrows(RuntimeException.class, provider::getClientAssertion);
    }

    // ==================== 接口契约测试 ====================

    @Test
    @DisplayName("接口契约: 是函数式接口")
    void interfaceContract_ShouldBeFunctionalInterface() {
        assertTrue(JwtClientAssertionProvider.class.isAnnotationPresent(FunctionalInterface.class));
    }

    @Test
    @DisplayName("接口契约: 只有一个抽象方法")
    void interfaceContract_ShouldHaveOnlyOneAbstractMethod() {
        long abstractMethodCount = java.util.Arrays.stream(JwtClientAssertionProvider.class.getMethods())
                .filter(m -> java.lang.reflect.Modifier.isAbstract(m.getModifiers()))
                .count();

        assertEquals(1, abstractMethodCount);
    }
}
