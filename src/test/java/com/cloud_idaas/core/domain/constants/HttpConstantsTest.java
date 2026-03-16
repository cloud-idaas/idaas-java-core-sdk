package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpConstants 单元测试
 */
class HttpConstantsTest {

    // ==================== 常量值测试 ====================

    @Test
    @DisplayName("HTTPS 应正确")
    void https_ShouldBeCorrect() {
        assertEquals("https", HttpConstants.HTTPS);
    }

    @Test
    @DisplayName("AUTHORIZATION_HEADER 应正确")
    void authorizationHeader_ShouldBeCorrect() {
        assertEquals("Authorization", HttpConstants.AUTHORIZATION_HEADER);
    }

    @Test
    @DisplayName("CONTENT_TYPE_HEADER 应正确")
    void contentTypeHeader_ShouldBeCorrect() {
        assertEquals("Content-Type", HttpConstants.CONTENT_TYPE_HEADER);
    }

    @Test
    @DisplayName("BEARER 应正确")
    void bearer_ShouldBeCorrect() {
        assertEquals("Bearer", HttpConstants.BEARER);
    }

    @Test
    @DisplayName("BASIC 应正确")
    void basic_ShouldBeCorrect() {
        assertEquals("Basic", HttpConstants.BASIC);
    }

    @Test
    @DisplayName("USER_AGENT 应正确")
    void userAgent_ShouldBeCorrect() {
        assertEquals("User-Agent", HttpConstants.USER_AGENT);
    }

    @Test
    @DisplayName("LOCATION 应正确")
    void location_ShouldBeCorrect() {
        assertEquals("Location", HttpConstants.LOCATION);
    }

    @Test
    @DisplayName("REDIRECT_TO 应正确")
    void redirectTo_ShouldBeCorrect() {
        assertEquals("Redirect to: ", HttpConstants.REDIRECT_TO);
    }

    @Test
    @DisplayName("X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS 应正确")
    void xAliyunEcsMetadataTokenTtlSeconds_ShouldBeCorrect() {
        assertEquals("X-aliyun-ecs-metadata-token-ttl-seconds", 
                HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS);
    }

    @Test
    @DisplayName("X_ALIYUN_ECS_METADATA_TOKEN 应正确")
    void xAliyunEcsMetadataToken_ShouldBeCorrect() {
        assertEquals("X-aliyun-ecs-metadata-token", HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN);
    }

    @Test
    @DisplayName("COLON 应正确")
    void colon_ShouldBeCorrect() {
        assertEquals(":", HttpConstants.COLON);
    }

    @Test
    @DisplayName("SPACE 应正确")
    void space_ShouldBeCorrect() {
        assertEquals(" ", HttpConstants.SPACE);
    }

    // ==================== 非空验证测试 ====================

    @Test
    @DisplayName("所有常量不应为空")
    void allConstants_ShouldNotBeNull() {
        assertNotNull(HttpConstants.HTTPS);
        assertNotNull(HttpConstants.AUTHORIZATION_HEADER);
        assertNotNull(HttpConstants.CONTENT_TYPE_HEADER);
        assertNotNull(HttpConstants.BEARER);
        assertNotNull(HttpConstants.BASIC);
        assertNotNull(HttpConstants.USER_AGENT);
        assertNotNull(HttpConstants.LOCATION);
        assertNotNull(HttpConstants.REDIRECT_TO);
        assertNotNull(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS);
        assertNotNull(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN);
        assertNotNull(HttpConstants.COLON);
        assertNotNull(HttpConstants.SPACE);
    }

    @Test
    @DisplayName("所有常量不应为空字符串")
    void allConstants_ShouldNotBeEmpty() {
        assertFalse(HttpConstants.HTTPS.isEmpty());
        assertFalse(HttpConstants.AUTHORIZATION_HEADER.isEmpty());
        assertFalse(HttpConstants.CONTENT_TYPE_HEADER.isEmpty());
        assertFalse(HttpConstants.BEARER.isEmpty());
        assertFalse(HttpConstants.BASIC.isEmpty());
        assertFalse(HttpConstants.USER_AGENT.isEmpty());
        assertFalse(HttpConstants.LOCATION.isEmpty());
        assertFalse(HttpConstants.REDIRECT_TO.isEmpty());
        assertFalse(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS.isEmpty());
        assertFalse(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN.isEmpty());
        assertFalse(HttpConstants.COLON.isEmpty());
        assertFalse(HttpConstants.SPACE.isEmpty());
    }

    // ==================== HTTP 头格式验证测试 ====================

    @Test
    @DisplayName("HTTP 头名称应包含连字符而非下划线")
    void httpHeaders_ShouldUseHyphensNotUnderscores() {
        assertTrue(HttpConstants.AUTHORIZATION_HEADER.contains("-") || 
                !HttpConstants.AUTHORIZATION_HEADER.contains("_"));
        assertTrue(HttpConstants.CONTENT_TYPE_HEADER.contains("-"));
        assertTrue(HttpConstants.USER_AGENT.contains("-"));
    }

    @Test
    @DisplayName("认证类型应首字母大写")
    void authTypes_ShouldStartWithUpperCase() {
        assertTrue(Character.isUpperCase(HttpConstants.BEARER.charAt(0)));
        assertTrue(Character.isUpperCase(HttpConstants.BASIC.charAt(0)));
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("COLON 应为单个冒号字符")
    void colon_ShouldBeSingleColonCharacter() {
        assertEquals(1, HttpConstants.COLON.length());
        assertEquals(':', HttpConstants.COLON.charAt(0));
    }

    @Test
    @DisplayName("SPACE 应为单个空格字符")
    void space_ShouldBeSingleSpaceCharacter() {
        assertEquals(1, HttpConstants.SPACE.length());
        assertEquals(' ', HttpConstants.SPACE.charAt(0));
    }

    // ==================== 阿里云特定常量测试 ====================

    @Test
    @DisplayName("阿里云 ECS 元数据相关常量应包含正确前缀")
    void aliyunEcsMetadataConstants_ShouldContainCorrectPrefix() {
        assertTrue(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS
                .startsWith("X-aliyun-ecs-metadata-token"));
        assertTrue(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN
                .startsWith("X-aliyun-ecs-metadata-token"));
    }

    // ==================== 唯一性测试 ====================

    @Test
    @DisplayName("所有常量值应唯一")
    void allConstants_ShouldBeUnique() {
        String[] values = {
            HttpConstants.HTTPS,
            HttpConstants.AUTHORIZATION_HEADER,
            HttpConstants.CONTENT_TYPE_HEADER,
            HttpConstants.BEARER,
            HttpConstants.BASIC,
            HttpConstants.USER_AGENT,
            HttpConstants.LOCATION,
            HttpConstants.REDIRECT_TO,
            HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS,
            HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN,
            HttpConstants.COLON,
            HttpConstants.SPACE
        };

        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j], 
                        "常量值不应重复: " + values[i]);
            }
        }
    }
}
