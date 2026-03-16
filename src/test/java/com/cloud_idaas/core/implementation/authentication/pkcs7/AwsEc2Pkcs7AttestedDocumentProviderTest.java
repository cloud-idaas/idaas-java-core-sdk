package com.cloud_idaas.core.implementation.authentication.pkcs7;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AwsEc2Pkcs7AttestedDocumentProvider 单元测试
 */
class AwsEc2Pkcs7AttestedDocumentProviderTest {

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 应成功创建实例")
    void constructor_ShouldCreateInstance() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();

        assertNotNull(provider);
    }

    // ==================== getAttestedDocument 测试 ====================

    @Test
    @DisplayName("getAttestedDocument: 应抛出 UnsupportedOperationException")
    void getAttestedDocument_ShouldThrowUnsupportedOperationException() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () ->
                provider.getAttestedDocument()
        );

        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    @DisplayName("getAttestedDocument: 异常消息应为 'Not implemented'")
    void getAttestedDocument_ExceptionMessage_ShouldBeNotImplemented() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();

        try {
            provider.getAttestedDocument();
            fail("应抛出 UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("Not implemented", e.getMessage());
        }
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 Pkcs7AttestedDocumentProvider 接口")
    void interface_ShouldImplementPkcs7AttestedDocumentProvider() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider);
    }

    // ==================== 多次调用测试 ====================

    @Test
    @DisplayName("getAttestedDocument: 多次调用应都抛出异常")
    void getAttestedDocument_MultipleCalls_ShouldAllThrowException() {
        AwsEc2Pkcs7AttestedDocumentProvider provider = new AwsEc2Pkcs7AttestedDocumentProvider();

        // 第一次调用
        assertThrows(UnsupportedOperationException.class, () -> provider.getAttestedDocument());

        // 第二次调用
        assertThrows(UnsupportedOperationException.class, () -> provider.getAttestedDocument());

        // 第三次调用
        assertThrows(UnsupportedOperationException.class, () -> provider.getAttestedDocument());
    }

    // ==================== 多个实例测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立抛出异常")
    void multipleInstances_ShouldAllThrowException() {
        AwsEc2Pkcs7AttestedDocumentProvider provider1 = new AwsEc2Pkcs7AttestedDocumentProvider();
        AwsEc2Pkcs7AttestedDocumentProvider provider2 = new AwsEc2Pkcs7AttestedDocumentProvider();

        assertThrows(UnsupportedOperationException.class, () -> provider1.getAttestedDocument());
        assertThrows(UnsupportedOperationException.class, () -> provider2.getAttestedDocument());
    }
}
