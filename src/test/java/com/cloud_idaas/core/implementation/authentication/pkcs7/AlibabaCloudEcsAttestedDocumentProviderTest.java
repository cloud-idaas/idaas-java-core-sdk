package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.http.HttpUtils;
import com.cloud_idaas.core.implementation.authentication.pkcs7.AlibabaCloudEcsAttestedDocumentProvider;
import com.cloud_idaas.core.util.JSONUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * AlibabaCloudECSAttestedDocumentProvider#getAttestedDocument 单元测试
 */
public class AlibabaCloudEcsAttestedDocumentProviderTest {

    private AlibabaCloudEcsAttestedDocumentProvider provider;
    private MockedStatic<HttpUtils> mockedHttpUtils;
    private MockedStatic<System> mockedSystem;

    /**
     * TC01: 初次调用 -> 应触发HTTP请求并返回结果
     */
    @Test
    void testGetAttestedDocument_FirstCall_ShouldMakeHttpRequest() throws UnsupportedEncodingException {
        long now = 1700000000L; // 固定时间戳

        Map<String, Object> audienceValue = new HashMap<>();
        audienceValue.put("aud", "test-instance-id");
        audienceValue.put("signingTime", now);
        String encodedAudience = URLEncoder.encode(JSONUtil.toJSONString(audienceValue), StandardCharsets.UTF_8.toString());

        String expectedUrl = String.format("http://100.100.100.200/latest/dynamic/instance-identity/pkcs7?audience=%s", encodedAudience);
        String mockResponse = "mock-pkcs7-document-response";


    }

    /**
     * TC02: 再次调用且未过期 -> 应返回缓存值而不发起新请求
     */
    @Test
    void testGetAttestedDocument_SecondCallWithinHalfLife_ShouldReturnCachedValue() throws UnsupportedEncodingException {
        long now = 1700000000L;
        mockedSystem.when(System::currentTimeMillis).thenReturn(now * 1000);

        Map<String, Object> audienceValue = new HashMap<>();
        audienceValue.put("aud", "test-instance-id");
        audienceValue.put("signingTime", now);
        String encodedAudience = URLEncoder.encode(JSONUtil.toJSONString(audienceValue), StandardCharsets.UTF_8.toString());

        String expectedUrl = String.format("http://100.100.100.200/latest/dynamic/instance-identity/pkcs7?audience=%s", encodedAudience);
        String mockResponse = "mock-pkcs7-document-response";

        mockedHttpUtils.when(() -> HttpUtils.anonymousGet(expectedUrl)).thenReturn(mockResponse);

        // 第一次调用
        String firstResult = provider.getAttestedDocument();

        // 时间只过了半小时（小于half-life）
        mockedSystem.when(System::currentTimeMillis).thenReturn((now + 900) * 1000); // 加900秒 < 1800秒(half-life)

        // 第二次调用
        String secondResult = provider.getAttestedDocument();

        assertEquals(firstResult, secondResult);
        mockedHttpUtils.verify(() -> HttpUtils.anonymousGet(expectedUrl), times(1)); // 只有一次调用
    }

    /**
     * TC03: 缓存过期后再次调用 -> 应重新请求并更新缓存
     */
    @Test
    void testGetAttestedDocument_AfterExpiry_ShouldReFetchAndCacheNewValue() throws UnsupportedEncodingException {
        long now = 1700000000L;
        mockedSystem.when(System::currentTimeMillis).thenReturn(now * 1000);

        Map<String, Object> audienceValue = new HashMap<>();
        audienceValue.put("aud", "test-instance-id");
        audienceValue.put("signingTime", now);
        String encodedAudience = URLEncoder.encode(JSONUtil.toJSONString(audienceValue), StandardCharsets.UTF_8.toString());

        String expectedUrl = String.format("http://100.100.100.200/latest/dynamic/instance-identity/pkcs7?audience=%s", encodedAudience);
        String firstResponse = "first-mock-pkcs7-document";
        String secondResponse = "second-mock-pkcs7-document";

        mockedHttpUtils.when(() -> HttpUtils.anonymousGet(expectedUrl)).thenReturn(firstResponse);

        // 第一次调用
        String firstResult = provider.getAttestedDocument();

        // 时间过了两小时（大于half-life）
        mockedSystem.when(System::currentTimeMillis).thenReturn((now + 3601) * 1000); // 超出half-life

        // 修改返回值模拟第二次请求
        mockedHttpUtils.when(() -> HttpUtils.anonymousGet(expectedUrl)).thenReturn(secondResponse);

        // 第二次调用
        String secondResult = provider.getAttestedDocument();

        assertEquals(firstResponse, firstResult);
        assertEquals(secondResponse, secondResult);
        mockedHttpUtils.verify(() -> HttpUtils.anonymousGet(expectedUrl), times(2)); // 两次调用
    }
}
