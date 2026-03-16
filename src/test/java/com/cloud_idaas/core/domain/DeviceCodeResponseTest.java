package com.cloud_idaas.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeviceCodeResponse 单元测试
 */
class DeviceCodeResponseTest {

    private DeviceCodeResponse response;

    @BeforeEach
    void setUp() {
        response = new DeviceCodeResponse();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: 所有字段默认应为 null")
    void defaultValue_AllFields_ShouldBeNull() {
        assertNull(response.getDeviceCode());
        assertNull(response.getUserCode());
        assertNull(response.getVerificationUri());
        assertNull(response.getVerificationUriComplete());
        assertNull(response.getExpiresIn());
        assertNull(response.getExpiresAt());
        assertNull(response.getInterval());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 deviceCode 应正确")
    void setter_DeviceCode_ShouldBeSet() {
        response.setDeviceCode("device-code-123");
        assertEquals("device-code-123", response.getDeviceCode());
    }

    @Test
    @DisplayName("Setter: 设置 userCode 应正确")
    void setter_UserCode_ShouldBeSet() {
        response.setUserCode("USER-CODE");
        assertEquals("USER-CODE", response.getUserCode());
    }

    @Test
    @DisplayName("Setter: 设置 verificationUri 应正确")
    void setter_VerificationUri_ShouldBeSet() {
        response.setVerificationUri("https://verify.example.com");
        assertEquals("https://verify.example.com", response.getVerificationUri());
    }

    @Test
    @DisplayName("Setter: 设置 verificationUriComplete 应正确")
    void setter_VerificationUriComplete_ShouldBeSet() {
        response.setVerificationUriComplete("https://verify.example.com?code=USER-CODE");
        assertEquals("https://verify.example.com?code=USER-CODE", response.getVerificationUriComplete());
    }

    @Test
    @DisplayName("Setter: 设置 expiresIn 应正确")
    void setter_ExpiresIn_ShouldBeSet() {
        response.setExpiresIn(1800L);
        assertEquals(1800L, response.getExpiresIn());
    }

    @Test
    @DisplayName("Setter: 设置 expiresAt 应正确")
    void setter_ExpiresAt_ShouldBeSet() {
        response.setExpiresAt(System.currentTimeMillis() / 1000 + 1800);
        assertNotNull(response.getExpiresAt());
    }

    @Test
    @DisplayName("Setter: 设置 interval 应正确")
    void setter_Interval_ShouldBeSet() {
        response.setInterval(5L);
        assertEquals(5L, response.getInterval());
    }

    // ==================== null 值测试 ====================

    @Test
    @DisplayName("null值: 设置字段为 null 应正确")
    void nullValue_AllFields_ShouldBeSet() {
        response.setDeviceCode("test");
        response.setDeviceCode(null);
        assertNull(response.getDeviceCode());

        response.setUserCode("test");
        response.setUserCode(null);
        assertNull(response.getUserCode());

        response.setExpiresIn(100L);
        response.setExpiresIn(null);
        assertNull(response.getExpiresIn());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("边界值: expiresIn 设为 0 应正确")
    void boundary_ExpiresInZero_ShouldBeSet() {
        response.setExpiresIn(0L);
        assertEquals(0L, response.getExpiresIn());
    }

    @Test
    @DisplayName("边界值: expiresIn 设为负数应正确")
    void boundary_ExpiresInNegative_ShouldBeSet() {
        response.setExpiresIn(-1L);
        assertEquals(-1L, response.getExpiresIn());
    }

    @Test
    @DisplayName("边界值: interval 设为 0 应正确")
    void boundary_IntervalZero_ShouldBeSet() {
        response.setInterval(0L);
        assertEquals(0L, response.getInterval());
    }

    @Test
    @DisplayName("边界值: Long 最大值应正确处理")
    void boundary_LongMaxValue_ShouldBeSet() {
        response.setExpiresIn(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getExpiresIn());

        response.setExpiresAt(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getExpiresAt());

        response.setInterval(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getInterval());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(response instanceof Serializable);
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        DeviceCodeResponse response1 = new DeviceCodeResponse();
        DeviceCodeResponse response2 = new DeviceCodeResponse();

        response1.setDeviceCode("device-1");
        response2.setDeviceCode("device-2");

        assertEquals("device-1", response1.getDeviceCode());
        assertEquals("device-2", response2.getDeviceCode());
    }

    // ==================== 完整配置测试 ====================

    @Test
    @DisplayName("完整配置: 设置所有字段应正确")
    void fullConfiguration_AllFields_ShouldBeSet() {
        response.setDeviceCode("device-code-123");
        response.setUserCode("USER-CODE");
        response.setVerificationUri("https://verify.example.com");
        response.setVerificationUriComplete("https://verify.example.com?code=USER-CODE");
        response.setExpiresIn(1800L);
        response.setExpiresAt(1704067200L);
        response.setInterval(5L);

        assertEquals("device-code-123", response.getDeviceCode());
        assertEquals("USER-CODE", response.getUserCode());
        assertEquals("https://verify.example.com", response.getVerificationUri());
        assertEquals("https://verify.example.com?code=USER-CODE", response.getVerificationUriComplete());
        assertEquals(1800L, response.getExpiresIn());
        assertEquals(1704067200L, response.getExpiresAt());
        assertEquals(5L, response.getInterval());
    }

    // ==================== 多次设置测试 ====================

    @Test
    @DisplayName("多次设置: 多次设置同一字段应使用最后一次的值")
    void multipleSet_SameField_ShouldUseLastValue() {
        response.setDeviceCode("code-1");
        response.setDeviceCode("code-2");
        response.setDeviceCode("code-3");

        assertEquals("code-3", response.getDeviceCode());
    }

    // ==================== 空字符串测试 ====================

    @Test
    @DisplayName("空字符串: 设置空字符串应正确")
    void emptyString_ShouldBeSet() {
        response.setDeviceCode("");
        assertEquals("", response.getDeviceCode());

        response.setUserCode("");
        assertEquals("", response.getUserCode());

        response.setVerificationUri("");
        assertEquals("", response.getVerificationUri());
    }
}
