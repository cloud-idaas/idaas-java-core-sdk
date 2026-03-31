package com.cloud_idaas.core.util;

import com.cloud_idaas.core.config.IDaaSClientConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * NormalizeUtil 单元测试
 */
class NormalizeUtilTest {

    // ==================== normalizeEndpoints: null/空值场景 ====================

    @Test
    @DisplayName("normalizeEndpoints: 所有 endpoint 为 null 时应保持 null")
    void normalizeEndpoints_AllNull_ShouldRemainNull() {
        IDaaSClientConfig config = new IDaaSClientConfig();

        NormalizeUtil.normalizeEndpoints(config);

        assertNull(config.getIssuer());
        assertNull(config.getTokenEndpoint());
        assertNull(config.getDeviceAuthorizationEndpoint());
        assertNull(config.getDeveloperApiEndpoint());
        assertNull(config.getOpenApiEndpoint());
    }

    @Test
    @DisplayName("normalizeEndpoints: 空字符串 endpoint 应保持不变")
    void normalizeEndpoints_AllEmpty_ShouldRemainEmpty() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer("");
        config.setTokenEndpoint("");
        config.setDeviceAuthorizationEndpoint("");
        config.setDeveloperApiEndpoint("");
        config.setOpenApiEndpoint("");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("", config.getIssuer());
        assertEquals("", config.getTokenEndpoint());
        assertEquals("", config.getDeviceAuthorizationEndpoint());
        assertEquals("", config.getDeveloperApiEndpoint());
        assertEquals("", config.getOpenApiEndpoint());
    }

    // ==================== normalizeEndpoints: https:// 场景 ====================

    @Test
    @DisplayName("normalizeEndpoints: 已经是 https:// 的 endpoint 应保持不变")
    void normalizeEndpoints_AllHttps_ShouldRemainUnchanged() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer("https://issuer.example.com");
        config.setTokenEndpoint("https://token.example.com/oauth/token");
        config.setDeviceAuthorizationEndpoint("https://device.example.com/oauth/device");
        config.setDeveloperApiEndpoint("https://dev.example.com/api");
        config.setOpenApiEndpoint("https://open.example.com/api");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://issuer.example.com", config.getIssuer());
        assertEquals("https://token.example.com/oauth/token", config.getTokenEndpoint());
        assertEquals("https://device.example.com/oauth/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://dev.example.com/api", config.getDeveloperApiEndpoint());
        assertEquals("https://open.example.com/api", config.getOpenApiEndpoint());
    }

    // ==================== normalizeEndpoints: http:// 转换场景 ====================

    @Test
    @DisplayName("normalizeEndpoints: http:// 的 endpoint 应被转换为 https://")
    void normalizeEndpoints_AllHttp_ShouldConvertToHttps() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer("http://issuer.example.com");
        config.setTokenEndpoint("http://token.example.com/oauth/token");
        config.setDeviceAuthorizationEndpoint("http://device.example.com/oauth/device");
        config.setDeveloperApiEndpoint("http://dev.example.com/api");
        config.setOpenApiEndpoint("http://open.example.com/api");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://issuer.example.com", config.getIssuer());
        assertEquals("https://token.example.com/oauth/token", config.getTokenEndpoint());
        assertEquals("https://device.example.com/oauth/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://dev.example.com/api", config.getDeveloperApiEndpoint());
        assertEquals("https://open.example.com/api", config.getOpenApiEndpoint());
    }

    @Test
    @DisplayName("normalizeEndpoints: http:// 转换应正确保留路径部分")
    void normalizeEndpoints_HttpWithPath_ShouldPreservePath() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setTokenEndpoint("http://example.com/path/to/token?param=value");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://example.com/path/to/token?param=value", config.getTokenEndpoint());
    }

    // ==================== normalizeEndpoints: 无协议前缀场景 ====================

    @Test
    @DisplayName("normalizeEndpoints: 无协议前缀的 endpoint 应自动补充 https://")
    void normalizeEndpoints_NoScheme_ShouldPrependHttps() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer("issuer.example.com");
        config.setTokenEndpoint("token.example.com/oauth/token");
        config.setDeviceAuthorizationEndpoint("device.example.com/oauth/device");
        config.setDeveloperApiEndpoint("dev.example.com/api");
        config.setOpenApiEndpoint("open.example.com/api");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://issuer.example.com", config.getIssuer());
        assertEquals("https://token.example.com/oauth/token", config.getTokenEndpoint());
        assertEquals("https://device.example.com/oauth/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://dev.example.com/api", config.getDeveloperApiEndpoint());
        assertEquals("https://open.example.com/api", config.getOpenApiEndpoint());
    }

    // ==================== normalizeEndpoints: 混合场景 ====================

    @Test
    @DisplayName("normalizeEndpoints: 混合协议场景应各自正确处理")
    void normalizeEndpoints_MixedSchemes_ShouldHandleEachCorrectly() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer(null);
        config.setTokenEndpoint("https://token.example.com/oauth/token");
        config.setDeviceAuthorizationEndpoint("http://device.example.com/oauth/device");
        config.setDeveloperApiEndpoint("dev.example.com/api");
        config.setOpenApiEndpoint("");

        NormalizeUtil.normalizeEndpoints(config);

        assertNull(config.getIssuer());
        assertEquals("https://token.example.com/oauth/token", config.getTokenEndpoint());
        assertEquals("https://device.example.com/oauth/device", config.getDeviceAuthorizationEndpoint());
        assertEquals("https://dev.example.com/api", config.getDeveloperApiEndpoint());
        assertEquals("", config.getOpenApiEndpoint());
    }

    // ==================== normalizeEndpoints: 各字段独立覆盖 ====================

    @Test
    @DisplayName("normalizeEndpoints: issuer 字段 http:// 应被转换")
    void normalizeEndpoints_IssuerHttp_ShouldConvert() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIssuer("http://issuer.example.com");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://issuer.example.com", config.getIssuer());
    }

    @Test
    @DisplayName("normalizeEndpoints: tokenEndpoint 字段无协议应补充 https://")
    void normalizeEndpoints_TokenEndpointNoScheme_ShouldPrepend() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setTokenEndpoint("token.example.com");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://token.example.com", config.getTokenEndpoint());
    }

    @Test
    @DisplayName("normalizeEndpoints: developerApiEndpoint 字段 http:// 应被转换")
    void normalizeEndpoints_DeveloperApiEndpointHttp_ShouldConvert() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setDeveloperApiEndpoint("http://dev.example.com");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://dev.example.com", config.getDeveloperApiEndpoint());
    }

    @Test
    @DisplayName("normalizeEndpoints: openApiEndpoint 字段 https:// 应保持不变")
    void normalizeEndpoints_OpenApiEndpointHttps_ShouldRemainUnchanged() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setOpenApiEndpoint("https://open.example.com");

        NormalizeUtil.normalizeEndpoints(config);

        assertEquals("https://open.example.com", config.getOpenApiEndpoint());
    }
}
