package com.cloud_idaas.core.implementation.authentication.pkcs7;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaticPkcs7AttestedDocumentProvider 单元测试
 */
class StaticPkcs7AttestedDocumentProviderTest {

    private static final String TEST_DOCUMENT = "test-pkcs7-document-content";

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 无参构造应创建实例")
    void constructor_NoArgs_ShouldCreateInstance() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("构造函数: 带参构造应设置文档内容")
    void constructor_WithArgs_ShouldSetDocument() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(TEST_DOCUMENT);

        assertNotNull(provider);
        assertEquals(TEST_DOCUMENT, provider.getAttestedDocument());
    }

    @Test
    @DisplayName("构造函数: 带参构造应接受 null 值")
    void constructor_WithNull_ShouldAcceptNull() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(null);

        assertNotNull(provider);
        assertNull(provider.getAttestedDocument());
    }

    @Test
    @DisplayName("构造函数: 带参构造应接受空字符串")
    void constructor_WithEmptyString_ShouldAcceptEmptyString() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider("");

        assertNotNull(provider);
        assertEquals("", provider.getAttestedDocument());
    }

    // ==================== getAttestedDocument 测试 ====================

    @Test
    @DisplayName("getAttestedDocument: 无参构造后应返回 null")
    void getAttestedDocument_AfterNoArgsConstructor_ShouldReturnNull() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider();

        assertNull(provider.getAttestedDocument());
    }

    @Test
    @DisplayName("getAttestedDocument: 应返回设置的文档内容")
    void getAttestedDocument_ShouldReturnDocument() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(TEST_DOCUMENT);

        String result = provider.getAttestedDocument();

        assertEquals(TEST_DOCUMENT, result);
    }

    @Test
    @DisplayName("getAttestedDocument: 多次调用应返回相同值")
    void getAttestedDocument_MultipleCalls_ShouldReturnSameValue() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(TEST_DOCUMENT);

        String result1 = provider.getAttestedDocument();
        String result2 = provider.getAttestedDocument();
        String result3 = provider.getAttestedDocument();

        assertEquals(TEST_DOCUMENT, result1);
        assertEquals(TEST_DOCUMENT, result2);
        assertEquals(TEST_DOCUMENT, result3);
        assertSame(result1, result2);
        assertSame(result2, result3);
    }

    // ==================== setAttestedDocument 测试 ====================

    @Test
    @DisplayName("setAttestedDocument: 应正确设置文档内容")
    void setAttestedDocument_ShouldSetDocument() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider();

        provider.setAttestedDocument(TEST_DOCUMENT);

        assertEquals(TEST_DOCUMENT, provider.getAttestedDocument());
    }

    @Test
    @DisplayName("setAttestedDocument: 应支持更新文档内容")
    void setAttestedDocument_ShouldSupportUpdate() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider("initial-document");

        provider.setAttestedDocument("updated-document");

        assertEquals("updated-document", provider.getAttestedDocument());
    }

    @Test
    @DisplayName("setAttestedDocument: 应支持设置为 null")
    void setAttestedDocument_WithNull_ShouldSetNull() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(TEST_DOCUMENT);

        provider.setAttestedDocument(null);

        assertNull(provider.getAttestedDocument());
    }

    @Test
    @DisplayName("setAttestedDocument: 应支持设置为空字符串")
    void setAttestedDocument_WithEmptyString_ShouldSetEmptyString() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(TEST_DOCUMENT);

        provider.setAttestedDocument("");

        assertEquals("", provider.getAttestedDocument());
    }

    @Test
    @DisplayName("setAttestedDocument: 应支持多次设置")
    void setAttestedDocument_MultipleSets_ShouldUseLastValue() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider();

        provider.setAttestedDocument("doc-1");
        provider.setAttestedDocument("doc-2");
        provider.setAttestedDocument("doc-3");

        assertEquals("doc-3", provider.getAttestedDocument());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 Pkcs7AttestedDocumentProvider 接口")
    void interface_ShouldImplementPkcs7AttestedDocumentProvider() {
        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider();

        assertTrue(provider instanceof com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider);
    }

    // ==================== 功能测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护自己的文档")
    void multipleInstances_ShouldMaintainIndependentDocuments() {
        StaticPkcs7AttestedDocumentProvider provider1 = new StaticPkcs7AttestedDocumentProvider("doc-1");
        StaticPkcs7AttestedDocumentProvider provider2 = new StaticPkcs7AttestedDocumentProvider("doc-2");

        assertEquals("doc-1", provider1.getAttestedDocument());
        assertEquals("doc-2", provider2.getAttestedDocument());

        provider1.setAttestedDocument("new-doc-1");

        assertEquals("new-doc-1", provider1.getAttestedDocument());
        assertEquals("doc-2", provider2.getAttestedDocument());
    }

    @Test
    @DisplayName("功能: 长文档内容应正确存储和返回")
    void longDocumentContent_ShouldBeStoredCorrectly() {
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("PKCS7DocumentContent");
        }
        String longDocument = longContent.toString();

        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(longDocument);

        assertEquals(longDocument, provider.getAttestedDocument());
        assertEquals(longDocument.length(), provider.getAttestedDocument().length());
    }

    @Test
    @DisplayName("功能: 特殊字符文档内容应正确存储和返回")
    void specialCharactersDocument_ShouldBeStoredCorrectly() {
        String specialDoc = "PKCS7\nDocument\tWith\"Special'Chars<>";

        StaticPkcs7AttestedDocumentProvider provider = new StaticPkcs7AttestedDocumentProvider(specialDoc);

        assertEquals(specialDoc, provider.getAttestedDocument());
    }
}
