package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContentType 单元测试
 */
class ContentTypeTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: XML 应存在")
    void enumValue_Xml_ShouldExist() {
        ContentType xml = ContentType.XML;

        assertNotNull(xml);
        assertEquals("XML", xml.name());
    }

    @Test
    @DisplayName("枚举值: JSON 应存在")
    void enumValue_Json_ShouldExist() {
        ContentType json = ContentType.JSON;

        assertNotNull(json);
        assertEquals("JSON", json.name());
    }

    @Test
    @DisplayName("枚举值: RAW 应存在")
    void enumValue_Raw_ShouldExist() {
        ContentType raw = ContentType.RAW;

        assertNotNull(raw);
        assertEquals("RAW", raw.name());
    }

    @Test
    @DisplayName("枚举值: FORM 应存在")
    void enumValue_Form_ShouldExist() {
        ContentType form = ContentType.FORM;

        assertNotNull(form);
        assertEquals("FORM", form.name());
    }

    // ==================== getType() 测试 ====================

    @Test
    @DisplayName("getType: XML 应返回 'application/xml'")
    void getType_Xml_ShouldReturnApplicationXml() {
        assertEquals("application/xml", ContentType.XML.getType());
    }

    @Test
    @DisplayName("getType: JSON 应返回 'application/json'")
    void getType_Json_ShouldReturnApplicationJson() {
        assertEquals("application/json", ContentType.JSON.getType());
    }

    @Test
    @DisplayName("getType: RAW 应返回 'application/octet-stream'")
    void getType_Raw_ShouldReturnApplicationOctetStream() {
        assertEquals("application/octet-stream", ContentType.RAW.getType());
    }

    @Test
    @DisplayName("getType: FORM 应返回 'application/x-www-form-urlencoded'")
    void getType_Form_ShouldReturnApplicationFormUrlencoded() {
        assertEquals("application/x-www-form-urlencoded", ContentType.FORM.getType());
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回 4 个枚举值")
    void values_ShouldReturnFourValues() {
        ContentType[] values = ContentType.values();

        assertEquals(4, values.length);
    }

    @Test
    @DisplayName("values: 应包含所有枚举值")
    void values_ShouldContainAllValues() {
        ContentType[] values = ContentType.values();

        assertTrue(containsValue(values, ContentType.XML));
        assertTrue(containsValue(values, ContentType.JSON));
        assertTrue(containsValue(values, ContentType.RAW));
        assertTrue(containsValue(values, ContentType.FORM));
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 'XML' 应返回 XML 枚举值")
    void valueOf_XmlString_ShouldReturnXmlEnum() {
        ContentType xml = ContentType.valueOf("XML");

        assertEquals(ContentType.XML, xml);
    }

    @Test
    @DisplayName("valueOf: 'JSON' 应返回 JSON 枚举值")
    void valueOf_JsonString_ShouldReturnJsonEnum() {
        ContentType json = ContentType.valueOf("JSON");

        assertEquals(ContentType.JSON, json);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ContentType.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("valueOf: null 应抛出 NullPointerException")
    void valueOf_NullName_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            ContentType.valueOf(null);
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: XML 的序号应为 0")
    void ordinal_Xml_ShouldBeZero() {
        assertEquals(0, ContentType.XML.ordinal());
    }

    @Test
    @DisplayName("ordinal: JSON 的序号应为 1")
    void ordinal_Json_ShouldBeOne() {
        assertEquals(1, ContentType.JSON.ordinal());
    }

    @Test
    @DisplayName("ordinal: RAW 的序号应为 2")
    void ordinal_Raw_ShouldBeTwo() {
        assertEquals(2, ContentType.RAW.ordinal());
    }

    @Test
    @DisplayName("ordinal: FORM 的序号应为 3")
    void ordinal_Form_ShouldBeThree() {
        assertEquals(3, ContentType.FORM.ordinal());
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: 各枚举值名称应正确")
    void name_AllValues_ShouldBeCorrect() {
        assertEquals("XML", ContentType.XML.name());
        assertEquals("JSON", ContentType.JSON.name());
        assertEquals("RAW", ContentType.RAW.name());
        assertEquals("FORM", ContentType.FORM.name());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("XML", ContentType.XML.toString());
        assertEquals("JSON", ContentType.JSON.toString());
        assertEquals("RAW", ContentType.RAW.toString());
        assertEquals("FORM", ContentType.FORM.toString());
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        ContentType json1 = ContentType.JSON;
        ContentType json2 = ContentType.JSON;

        assertEquals(json1, json2);
        assertSame(json1, json2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(ContentType.XML, ContentType.JSON);
        assertNotEquals(ContentType.JSON, ContentType.RAW);
        assertNotEquals(ContentType.RAW, ContentType.FORM);
    }

    // ==================== MIME 类型格式验证测试 ====================

    @Test
    @DisplayName("MIME格式: 所有类型应以 'application/' 开头")
    void mimeFormat_AllTypes_ShouldStartWithApplication() {
        for (ContentType type : ContentType.values()) {
            assertTrue(type.getType().startsWith("application/"),
                    "类型 " + type.name() + " 应以 'application/' 开头");
        }
    }

    @Test
    @DisplayName("MIME格式: 所有类型不应为空")
    void mimeFormat_AllTypes_ShouldNotBeEmpty() {
        for (ContentType type : ContentType.values()) {
            assertNotNull(type.getType());
            assertFalse(type.getType().isEmpty());
        }
    }

    // ==================== switch 使用测试 ====================

    @Test
    @DisplayName("switch: 在 switch 语句中应正确匹配")
    void switchStatement_ShouldCorrectlyMatch() {
        assertEquals("xml", getContentTypeDescription(ContentType.XML));
        assertEquals("json", getContentTypeDescription(ContentType.JSON));
        assertEquals("raw", getContentTypeDescription(ContentType.RAW));
        assertEquals("form", getContentTypeDescription(ContentType.FORM));
    }

    // ==================== 辅助方法 ====================

    private boolean containsValue(ContentType[] values, ContentType target) {
        for (ContentType value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    private String getContentTypeDescription(ContentType contentType) {
        switch (contentType) {
            case XML:
                return "xml";
            case JSON:
                return "json";
            case RAW:
                return "raw";
            case FORM:
                return "form";
            default:
                return "unknown";
        }
    }
}
