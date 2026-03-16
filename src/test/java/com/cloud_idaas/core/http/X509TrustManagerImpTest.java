package com.cloud_idaas.core.http;

import com.cloud_idaas.core.exception.HttpException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * X509TrustManagerImp 单元测试
 */
class X509TrustManagerImpTest {

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: unsafeIgnoreSSLCert=true 应正确设置")
    void constructor_UnsafeIgnoreTrue_ShouldBeSet() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertTrue(trustManager.isUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("构造函数: unsafeIgnoreSSLCert=false 应正确设置")
    void constructor_UnsafeIgnoreFalse_ShouldBeSet() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(false);

        assertFalse(trustManager.isUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("构造函数: 使用 trustManagers 列表应正确设置")
    void constructor_WithTrustManagers_ShouldBeSet() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);

        assertFalse(trustManager.isUnsafeIgnoreSSLCert());
    }

    // ==================== isUnsafeIgnoreSSLCert 测试 ====================

    @Test
    @DisplayName("isUnsafeIgnoreSSLCert: 应返回正确的值")
    void isUnsafeIgnoreSSLCert_ShouldReturnCorrectValue() {
        X509TrustManagerImp trustManagerTrue = new X509TrustManagerImp(true);
        X509TrustManagerImp trustManagerFalse = new X509TrustManagerImp(false);

        assertTrue(trustManagerTrue.isUnsafeIgnoreSSLCert());
        assertFalse(trustManagerFalse.isUnsafeIgnoreSSLCert());
    }

    // ==================== checkClientTrusted 测试 ====================

    @Test
    @DisplayName("checkClientTrusted: 不应抛出异常")
    void checkClientTrusted_ShouldNotThrowException() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, null);
        });
    }

    @Test
    @DisplayName("checkClientTrusted: 空证书链不应抛出异常")
    void checkClientTrusted_EmptyChain_ShouldNotThrowException() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);
        X509Certificate[] emptyChain = new X509Certificate[0];

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(emptyChain, "RSA");
        });
    }

    // ==================== checkServerTrusted 测试 ====================

    @Test
    @DisplayName("checkServerTrusted: unsafeIgnoreSSLCert=true 不应抛出异常")
    void checkServerTrusted_UnsafeIgnoreTrue_ShouldNotThrowException() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkServerTrusted(null, null);
        });
    }

    @Test
    @DisplayName("checkServerTrusted: unsafeIgnoreSSLCert=true 空证书链不应抛出异常")
    void checkServerTrusted_UnsafeIgnoreTrue_EmptyChain_ShouldNotThrowException() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);
        X509Certificate[] emptyChain = new X509Certificate[0];

        assertDoesNotThrow(() -> {
            trustManager.checkServerTrusted(emptyChain, "RSA");
        });
    }

    @Test
    @DisplayName("checkServerTrusted: unsafeIgnoreSSLCert=false 空信任管理器列表应抛出异常")
    void checkServerTrusted_UnsafeIgnoreFalse_EmptyTrustManagers_ShouldThrowException() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);

        assertThrows(HttpException.class, () -> {
            trustManager.checkServerTrusted(null, "RSA");
        });
    }

    @Test
    @DisplayName("checkServerTrusted: unsafeIgnoreSSLCert=false 空证书链应抛出异常")
    void checkServerTrusted_UnsafeIgnoreFalse_EmptyChain_ShouldThrowException() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);
        X509Certificate[] emptyChain = new X509Certificate[0];

        assertThrows(HttpException.class, () -> {
            trustManager.checkServerTrusted(emptyChain, "RSA");
        });
    }

    // ==================== getAcceptedIssuers 测试 ====================

    @Test
    @DisplayName("getAcceptedIssuers: 空信任管理器列表应返回空数组")
    void getAcceptedIssuers_EmptyTrustManagers_ShouldReturnEmptyArray() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);

        X509Certificate[] issuers = trustManager.getAcceptedIssuers();

        assertNotNull(issuers);
        assertEquals(0, issuers.length);
    }

    @Test
    @DisplayName("getAcceptedIssuers: unsafeIgnoreSSLCert=true 空信任管理器列表应返回空数组")
    void getAcceptedIssuers_UnsafeIgnoreTrue_ShouldReturnEmptyArray() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        X509Certificate[] issuers = trustManager.getAcceptedIssuers();

        assertNotNull(issuers);
        assertEquals(0, issuers.length);
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 X509TrustManager 接口")
    void interface_ShouldImplementX509TrustManager() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertTrue(trustManager instanceof X509TrustManager);
    }

    // ==================== 安全性测试 ====================

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=true 是不安全的配置")
    void security_UnsafeIgnoreTrue_ShouldBeInsecure() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        // 不安全的信任管理器会接受任何证书
        assertTrue(trustManager.isUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=false 是安全的配置")
    void security_UnsafeIgnoreFalse_ShouldBeSecure() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(false);

        // 安全的信任管理器需要验证证书
        assertFalse(trustManager.isUnsafeIgnoreSSLCert());
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个实例应独立维护各自的状态")
    void multipleInstances_ShouldBeIndependent() {
        X509TrustManagerImp trustManager1 = new X509TrustManagerImp(true);
        X509TrustManagerImp trustManager2 = new X509TrustManagerImp(false);

        assertTrue(trustManager1.isUnsafeIgnoreSSLCert());
        assertFalse(trustManager2.isUnsafeIgnoreSSLCert());
    }

    // ==================== 不同认证类型测试 ====================

    @Test
    @DisplayName("认证类型: RSA 认证类型应正确处理")
    void authType_Rsa_ShouldBeHandled() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, "RSA");
            trustManager.checkServerTrusted(null, "RSA");
        });
    }

    @Test
    @DisplayName("认证类型: DSA 认证类型应正确处理")
    void authType_Dsa_ShouldBeHandled() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, "DSA");
            trustManager.checkServerTrusted(null, "DSA");
        });
    }

    @Test
    @DisplayName("认证类型: EC 认证类型应正确处理")
    void authType_Ec_ShouldBeHandled() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, "EC");
            trustManager.checkServerTrusted(null, "EC");
        });
    }

    @Test
    @DisplayName("认证类型: null 认证类型应正确处理")
    void authType_Null_ShouldBeHandled() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, null);
            trustManager.checkServerTrusted(null, null);
        });
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("边界: 空信任管理器列表应正确处理")
    void boundary_EmptyTrustManagersList_ShouldBeHandled() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);

        // getAcceptedIssuers 应返回空数组
        X509Certificate[] issuers = trustManager.getAcceptedIssuers();
        assertEquals(0, issuers.length);
    }

    // ==================== 异常消息测试 ====================

    @Test
    @DisplayName("异常消息: 验证失败时应包含有意义的消息")
    void exceptionMessage_ValidationFailed_ShouldContainMeaningfulMessage() {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagers);

        HttpException exception = assertThrows(HttpException.class, () -> {
            trustManager.checkServerTrusted(null, "RSA");
        });

        assertTrue(exception.getMessage().contains("TrustManagers") ||
                exception.getMessage().contains("certificate"));
    }
}
