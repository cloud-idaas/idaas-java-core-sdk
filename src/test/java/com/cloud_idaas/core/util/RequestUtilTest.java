package com.cloud_idaas.core.util;

import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.exception.EncodingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RequestUtil 单元测试
 */
class RequestUtilTest {

    // ==================== getISO8601Time 测试 ====================

    @Test
    @DisplayName("getISO8601Time: 将日期转换为 ISO8601 格式")
    void getISO8601Time_WithDate_ShouldReturnIso8601Format() {
        Date date = new Date(1609459200000L); // 2021-01-01 00:00:00 UTC

        String result = RequestUtil.getISO8601Time(date);

        assertNotNull(result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z"));
        assertTrue(result.contains("2021-01-01"));
    }

    // ==================== getUTCDate 测试 ====================

    @Test
    @DisplayName("getUTCDate: 将 ISO8601 字符串解析为日期")
    void getUTCDate_WithValidIso8601String_ShouldReturnDate() {
        String iso8601String = "2021-01-01T00:00:00Z";

        Date result = RequestUtil.getUTCDate(iso8601String);

        assertNotNull(result);
        assertEquals(1609459200000L, result.getTime());
    }

    @Test
    @DisplayName("getUTCDate: 无效的日期格式应抛出异常")
    void getUTCDate_WithInvalidFormat_ShouldThrowException() {
        String invalidDate = "invalid-date";

        assertThrows(CredentialException.class, () ->
                RequestUtil.getUTCDate(invalidDate)
        );
    }

    // ==================== getUniqueNonce 测试 ====================

    @Test
    @DisplayName("getUniqueNonce: 应返回 32 位十六进制字符串")
    void getUniqueNonce_ShouldReturn32CharHexString() {
        String nonce = RequestUtil.getUniqueNonce();

        assertNotNull(nonce);
        assertEquals(32, nonce.length());
        assertTrue(nonce.matches("[0-9a-f]+"));
    }

    @Test
    @DisplayName("getUniqueNonce: 多次调用应返回不同值")
    void getUniqueNonce_MultipleCalls_ShouldReturnDifferentValues() {
        String nonce1 = RequestUtil.getUniqueNonce();
        String nonce2 = RequestUtil.getUniqueNonce();

        assertNotEquals(nonce1, nonce2);
    }

    @Test
    @DisplayName("getUniqueNonce: 并发调用应返回不同值")
    void getUniqueNonce_ConcurrentCalls_ShouldReturnDifferentValues() throws InterruptedException {
        final int threadCount = 10;
        final String[] nonces = new String[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> nonces[index] = RequestUtil.getUniqueNonce());
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有 nonce 都是唯一的
        for (int i = 0; i < threadCount; i++) {
            for (int j = i + 1; j < threadCount; j++) {
                assertNotEquals(nonces[i], nonces[j]);
            }
        }
    }

    // ==================== composeUrl 测试 ====================

    @Test
    @DisplayName("composeUrl: 组合基本 URL")
    void composeUrl_WithBasicParams_ShouldReturnUrl() {
        Map<String, String> queries = new HashMap<>();
        queries.put("key1", "value1");
        queries.put("key2", "value2");

        String url = RequestUtil.composeUrl("https://example.com", "/path", queries);

        assertNotNull(url);
        assertTrue(url.startsWith("https://example.com/path?"));
        assertTrue(url.contains("key1=value1"));
        assertTrue(url.contains("key2=value2"));
    }

    @Test
    @DisplayName("composeUrl: null path 应正确处理")
    void composeUrl_WithNullPath_ShouldWork() {
        Map<String, String> queries = new HashMap<>();
        queries.put("key", "value");

        String url = RequestUtil.composeUrl("https://example.com", null, queries);

        assertNotNull(url);
        assertTrue(url.startsWith("https://example.com?"));
    }

    @Test
    @DisplayName("composeUrl: 空 path 应正确处理")
    void composeUrl_WithEmptyPath_ShouldWork() {
        Map<String, String> queries = new HashMap<>();
        queries.put("key", "value");

        String url = RequestUtil.composeUrl("https://example.com", "", queries);

        assertNotNull(url);
    }

    @Test
    @DisplayName("composeUrl: 特殊字符应被正确编码")
    void composeUrl_WithSpecialCharacters_ShouldEncode() {
        Map<String, String> queries = new HashMap<>();
        queries.put("key", "value with spaces");
        queries.put("special", "a+b=c");

        String url = RequestUtil.composeUrl("https://example.com", "/path", queries);

        assertNotNull(url);
        assertTrue(url.contains("value+with+spaces") || url.contains("value%20with%20spaces"));
    }

    @Test
    @DisplayName("composeUrl: 空查询参数应返回基本 URL")
    void composeUrl_WithEmptyQueries_ShouldReturnBaseUrl() {
        Map<String, String> queries = new HashMap<>();

        String url = RequestUtil.composeUrl("https://example.com", "/path", queries);

        assertNotNull(url);
        assertTrue(url.startsWith("https://example.com/path"));
    }
}
