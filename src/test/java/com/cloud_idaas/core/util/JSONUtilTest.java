package com.cloud_idaas.core.util;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSONUtil 单元测试
 */
class JSONUtilTest {

    // ==================== toJSONString 测试 ====================

    @Test
    @DisplayName("toJSONString: 将对象转换为 JSON 字符串")
    void toJSONString_WithObject_ShouldReturnJsonString() {
        TestObject obj = new TestObject("test", 123);

        String json = JSONUtil.toJSONString(obj);

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"value\":123"));
    }

    @Test
    @DisplayName("toJSONString: null 对象应返回 null")
    void toJSONString_WithNull_ShouldReturnNull() {
        String json = JSONUtil.toJSONString(null);
        assertEquals("null", json);
    }

    @Test
    @DisplayName("toJSONString: 空对象应返回空 JSON")
    void toJSONString_WithEmptyObject_ShouldReturnEmptyJson() {
        String json = JSONUtil.toJSONString(new Object());
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
    }

    // ==================== toJSONBytes 测试 ====================

    @Test
    @DisplayName("toJSONBytes: 将对象转换为 JSON 字节数组")
    void toJSONBytes_WithObject_ShouldReturnJsonBytes() {
        TestObject obj = new TestObject("test", 123);

        byte[] bytes = JSONUtil.toJSONBytes(obj);

        assertNotNull(bytes);
        String json = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(json.contains("\"name\":\"test\""));
    }

    // ==================== parseObject 测试 ====================

    @Test
    @DisplayName("parseObject: 将 JSON 字符串解析为对象")
    void parseObject_WithValidJson_ShouldReturnObject() {
        String json = "{\"name\":\"test\",\"value\":123}";

        TestObject obj = JSONUtil.parseObject(json, TestObject.class);

        assertNotNull(obj);
        assertEquals("test", obj.getName());
        assertEquals(123, obj.getValue());
    }

    @Test
    @DisplayName("parseObject: 无效的 JSON 应抛出异常")
    void parseObject_WithInvalidJson_ShouldThrowException() {
        String invalidJson = "{invalid json}";

        assertThrows(JsonSyntaxException.class, () ->
                JSONUtil.parseObject(invalidJson, TestObject.class)
        );
    }

    @Test
    @DisplayName("parseObject: 使用 Type 参数解析")
    void parseObject_WithType_ShouldReturnObject() {
        String json = "{\"name\":\"test\",\"value\":123}";

        TestObject obj = JSONUtil.parseObject(json, (java.lang.reflect.Type) TestObject.class);

        assertNotNull(obj);
        assertEquals("test", obj.getName());
    }

    // ==================== parseArray 测试 ====================

    @Test
    @DisplayName("parseArray: 将 JSON 数组字符串解析为列表")
    void parseArray_WithValidJsonArray_ShouldReturnList() {
        String json = "[{\"name\":\"test1\",\"value\":1},{\"name\":\"test2\",\"value\":2}]";

        List<TestObject> list = JSONUtil.parseArray(json, TestObject.class);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("test1", list.get(0).getName());
        assertEquals("test2", list.get(1).getName());
    }

    @Test
    @DisplayName("parseArray: 空数组应返回空列表")
    void parseArray_WithEmptyArray_ShouldReturnEmptyList() {
        String json = "[]";

        List<TestObject> list = JSONUtil.parseArray(json, TestObject.class);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // ==================== parseMap 测试 ====================

    @Test
    @DisplayName("parseMap: 将 JSON 对象解析为 Map")
    void parseMap_WithValidJsonObject_ShouldReturnMap() {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        Map<String, String> map = JSONUtil.parseMap(json, String.class, String.class);

        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    @DisplayName("parseMap: 复杂值类型的 Map")
    void parseMap_WithComplexValueType_ShouldReturnMap() {
        String json = "{\"key1\":123,\"key2\":456}";

        Map<String, Integer> map = JSONUtil.parseMap(json, String.class, Integer.class);

        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals(123, map.get("key1"));
        assertEquals(456, map.get("key2"));
    }

    // ==================== 测试辅助类 ====================

    public static class TestObject {
        private String name;
        private int value;

        public TestObject() {
        }

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
