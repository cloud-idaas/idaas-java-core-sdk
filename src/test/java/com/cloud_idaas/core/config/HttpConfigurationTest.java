package com.cloud_idaas.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpConfiguration 单元测试
 */
class HttpConfigurationTest {

    private HttpConfiguration config;

    @BeforeEach
    void setUp() {
        config = new HttpConfiguration();
    }

    // ==================== 默认值测试 ====================

    @Test
    @DisplayName("默认值: connectTimeout 默认应为 5000")
    void defaultValue_ConnectTimeout_ShouldBe5000() {
        assertEquals(5000, config.getConnectTimeout());
    }

    @Test
    @DisplayName("默认值: readTimeout 默认应为 10000")
    void defaultValue_ReadTimeout_ShouldBe10000() {
        assertEquals(10000, config.getReadTimeout());
    }

    @Test
    @DisplayName("默认值: unsafeIgnoreSSLCert 默认应为 false")
    void defaultValue_UnsafeIgnoreSSLCert_ShouldBeFalse() {
        assertFalse(config.getUnsafeIgnoreSSLCert());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Setter: 设置 connectTimeout 应正确")
    void setter_ConnectTimeout_ShouldBeSet() {
        config.setConnectTimeout(3000);
        assertEquals(3000, config.getConnectTimeout());
    }

    @Test
    @DisplayName("Setter: 设置 readTimeout 应正确")
    void setter_ReadTimeout_ShouldBeSet() {
        config.setReadTimeout(5000);
        assertEquals(5000, config.getReadTimeout());
    }

    @Test
    @DisplayName("Setter: 设置 unsafeIgnoreSSLCert 为 true 应正确")
    void setter_UnsafeIgnoreSSLCertTrue_ShouldBeSet() {
        config.setUnsafeIgnoreSSLCert(true);
        assertTrue(config.getUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("Setter: 设置 unsafeIgnoreSSLCert 为 false 应正确")
    void setter_UnsafeIgnoreSSLCertFalse_ShouldBeSet() {
        config.setUnsafeIgnoreSSLCert(true);
        config.setUnsafeIgnoreSSLCert(false);
        assertFalse(config.getUnsafeIgnoreSSLCert());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("边界值: connectTimeout 设为 0 应正确")
    void boundary_ConnectTimeoutZero_ShouldBeSet() {
        config.setConnectTimeout(0);
        assertEquals(0, config.getConnectTimeout());
    }

    @Test
    @DisplayName("边界值: readTimeout 设为 0 应正确")
    void boundary_ReadTimeoutZero_ShouldBeSet() {
        config.setReadTimeout(0);
        assertEquals(0, config.getReadTimeout());
    }

    @Test
    @DisplayName("边界值: connectTimeout 设为负数应正确")
    void boundary_ConnectTimeoutNegative_ShouldBeSet() {
        config.setConnectTimeout(-1);
        assertEquals(-1, config.getConnectTimeout());
    }

    @Test
    @DisplayName("边界值: readTimeout 设为负数应正确")
    void boundary_ReadTimeoutNegative_ShouldBeSet() {
        config.setReadTimeout(-1);
        assertEquals(-1, config.getReadTimeout());
    }

    @Test
    @DisplayName("边界值: connectTimeout 设为大值应正确")
    void boundary_ConnectTimeoutLargeValue_ShouldBeSet() {
        config.setConnectTimeout(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, config.getConnectTimeout());
    }

    // ==================== copy 方法测试 ====================

    @Test
    @DisplayName("copy: 复制非空对象应返回正确的副本")
    void copy_NonNullSource_ShouldReturnCorrectCopy() {
        HttpConfiguration source = new HttpConfiguration();
        source.setConnectTimeout(3000);
        source.setReadTimeout(6000);
        source.setUnsafeIgnoreSSLCert(true);

        HttpConfiguration copy = HttpConfiguration.copy(source);

        assertNotNull(copy);
        assertEquals(3000, copy.getConnectTimeout());
        assertEquals(6000, copy.getReadTimeout());
        assertTrue(copy.getUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("copy: 复制默认值对象应返回正确的副本")
    void copy_DefaultValues_ShouldReturnCorrectCopy() {
        HttpConfiguration source = new HttpConfiguration();

        HttpConfiguration copy = HttpConfiguration.copy(source);

        assertNotNull(copy);
        assertEquals(5000, copy.getConnectTimeout());
        assertEquals(10000, copy.getReadTimeout());
        assertFalse(copy.getUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("copy: 复制 null 应返回 null")
    void copy_NullSource_ShouldReturnNull() {
        HttpConfiguration copy = HttpConfiguration.copy(null);

        assertNull(copy);
    }

    @Test
    @DisplayName("copy: 副本应与原对象是不同实例")
    void copy_ShouldReturnDifferentInstance() {
        HttpConfiguration source = new HttpConfiguration();
        source.setConnectTimeout(3000);

        HttpConfiguration copy = HttpConfiguration.copy(source);

        assertNotSame(source, copy);
    }

    @Test
    @DisplayName("copy: 修改副本不应影响原对象")
    void copy_ModifyingCopy_ShouldNotAffectOriginal() {
        HttpConfiguration source = new HttpConfiguration();
        source.setConnectTimeout(3000);
        source.setReadTimeout(6000);

        HttpConfiguration copy = HttpConfiguration.copy(source);
        copy.setConnectTimeout(9999);
        copy.setReadTimeout(8888);
        copy.setUnsafeIgnoreSSLCert(true);

        assertEquals(3000, source.getConnectTimeout());
        assertEquals(6000, source.getReadTimeout());
        assertFalse(source.getUnsafeIgnoreSSLCert());
    }

    // ==================== Serializable 测试 ====================

    @Test
    @DisplayName("Serializable: 应实现 Serializable 接口")
    void serializable_ShouldImplementSerializable() {
        assertTrue(config instanceof Serializable);
    }

    @Test
    @DisplayName("Serializable: 应有 serialVersionUID")
    void serializable_ShouldHaveSerialVersionUID() throws Exception {
        long serialVersionUID = 785896788594865623L;
        // 验证类存在 serialVersionUID 字段
        assertNotNull(HttpConfiguration.class.getDeclaredField("serialVersionUID"));
    }

    // ==================== 多次设置测试 ====================

    @Test
    @DisplayName("多次设置: 多次设置 connectTimeout 应使用最后一次的值")
    void multipleSet_ConnectTimeout_ShouldUseLastValue() {
        config.setConnectTimeout(1000);
        config.setConnectTimeout(2000);
        config.setConnectTimeout(3000);

        assertEquals(3000, config.getConnectTimeout());
    }

    @Test
    @DisplayName("多次设置: 多次设置 readTimeout 应使用最后一次的值")
    void multipleSet_ReadTimeout_ShouldUseLastValue() {
        config.setReadTimeout(1000);
        config.setReadTimeout(2000);
        config.setReadTimeout(3000);

        assertEquals(3000, config.getReadTimeout());
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        HttpConfiguration config1 = new HttpConfiguration();
        HttpConfiguration config2 = new HttpConfiguration();

        config1.setConnectTimeout(1000);
        config2.setConnectTimeout(2000);

        assertEquals(1000, config1.getConnectTimeout());
        assertEquals(2000, config2.getConnectTimeout());
    }
}
