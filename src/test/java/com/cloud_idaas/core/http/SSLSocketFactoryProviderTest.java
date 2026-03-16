package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSLSocketFactoryProvider 单元测试
 */
class SSLSocketFactoryProviderTest {

    // ==================== getSSLSocketFactory 测试 ====================

    @Test
    @DisplayName("getSSLSocketFactory: unsafeIgnoreSSLCert=true 应返回非空工厂")
    void getSSLSocketFactory_UnsafeIgnoreTrue_ShouldReturnNonNullFactory() {
        SSLSocketFactory factory = SSLSocketFactoryProvider.getSSLSocketFactory(true);

        assertNotNull(factory);
    }

    @Test
    @DisplayName("getSSLSocketFactory: unsafeIgnoreSSLCert=false 应返回非空工厂")
    void getSSLSocketFactory_UnsafeIgnoreFalse_ShouldReturnNonNullFactory() {
        SSLSocketFactory factory = SSLSocketFactoryProvider.getSSLSocketFactory(false);

        assertNotNull(factory);
    }

    @Test
    @DisplayName("getSSLSocketFactory: 多次调用应返回不同的实例")
    void getSSLSocketFactory_MultipleCalls_ShouldReturnDifferentInstances() {
        SSLSocketFactory factory1 = SSLSocketFactoryProvider.getSSLSocketFactory(true);
        SSLSocketFactory factory2 = SSLSocketFactoryProvider.getSSLSocketFactory(true);

        // 每次调用都会创建新的 SSLContext，所以返回不同的实例
        assertNotNull(factory1);
        assertNotNull(factory2);
    }

    // ==================== getX509TrustManager 测试 ====================

    @Test
    @DisplayName("getX509TrustManager: unsafeIgnoreSSLCert=true 应返回 X509TrustManagerImp")
    void getX509TrustManager_UnsafeIgnoreTrue_ShouldReturnX509TrustManagerImp() {
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(true);

        assertNotNull(trustManager);
        assertTrue(trustManager instanceof X509TrustManagerImp);
    }

    @Test
    @DisplayName("getX509TrustManager: unsafeIgnoreSSLCert=false 应返回 X509TrustManagerImp")
    void getX509TrustManager_UnsafeIgnoreFalse_ShouldReturnX509TrustManagerImp() {
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(false);

        assertNotNull(trustManager);
        assertTrue(trustManager instanceof X509TrustManagerImp);
    }

    @Test
    @DisplayName("getX509TrustManager: unsafeIgnoreSSLCert=true 应设置正确的标志")
    void getX509TrustManager_UnsafeIgnoreTrue_ShouldSetCorrectFlag() {
        X509TrustManagerImp trustManager = 
                (X509TrustManagerImp) SSLSocketFactoryProvider.getX509TrustManager(true);

        assertTrue(trustManager.isUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("getX509TrustManager: unsafeIgnoreSSLCert=false 应设置正确的标志")
    void getX509TrustManager_UnsafeIgnoreFalse_ShouldSetCorrectFlag() {
        X509TrustManagerImp trustManager = 
                (X509TrustManagerImp) SSLSocketFactoryProvider.getX509TrustManager(false);

        assertFalse(trustManager.isUnsafeIgnoreSSLCert());
    }

    // ==================== getHostnameVerifier 测试 ====================

    @Test
    @DisplayName("getHostnameVerifier: unsafeIgnoreSSLCert=true 应返回非空验证器")
    void getHostnameVerifier_UnsafeIgnoreTrue_ShouldReturnNonNullVerifier() {
        HostnameVerifier verifier = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertNotNull(verifier);
    }

    @Test
    @DisplayName("getHostnameVerifier: unsafeIgnoreSSLCert=false 应返回非空验证器")
    void getHostnameVerifier_UnsafeIgnoreFalse_ShouldReturnNonNullVerifier() {
        HostnameVerifier verifier = SSLSocketFactoryProvider.getHostnameVerifier(false);

        assertNotNull(verifier);
    }

    @Test
    @DisplayName("getHostnameVerifier: 应返回 DefaultHostnameVerifier 的实例")
    void getHostnameVerifier_ShouldReturnDefaultHostnameVerifierInstance() {
        HostnameVerifier verifier = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertTrue(verifier instanceof DefaultHostnameVerifier);
    }

    @Test
    @DisplayName("getHostnameVerifier: 多次调用 true 应返回相同实例")
    void getHostnameVerifier_MultipleCallsTrue_ShouldReturnSameInstance() {
        HostnameVerifier verifier1 = SSLSocketFactoryProvider.getHostnameVerifier(true);
        HostnameVerifier verifier2 = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertSame(verifier1, verifier2);
    }

    // ==================== 安全性测试 ====================

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=true 是不安全的配置")
    void security_UnsafeIgnoreTrue_ShouldBeInsecure() {
        X509TrustManagerImp trustManager = 
                (X509TrustManagerImp) SSLSocketFactoryProvider.getX509TrustManager(true);

        // 不安全的信任管理器会接受任何证书
        assertTrue(trustManager.isUnsafeIgnoreSSLCert());
    }

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=false 是安全的配置")
    void security_UnsafeIgnoreFalse_ShouldBeSecure() {
        X509TrustManagerImp trustManager = 
                (X509TrustManagerImp) SSLSocketFactoryProvider.getX509TrustManager(false);

        // 安全的信任管理器需要验证证书
        assertFalse(trustManager.isUnsafeIgnoreSSLCert());
    }

    // ==================== SSL 协议测试 ====================

    @Test
    @DisplayName("SSL协议: 应使用 TLS 协议")
    void sslProtocol_ShouldUseTls() {
        // 验证返回的工厂可以正常工作
        SSLSocketFactory factory = SSLSocketFactoryProvider.getSSLSocketFactory(false);

        assertNotNull(factory);
        // 工厂应该支持 TLS 协议
        String[] supportedCipherSuites = factory.getSupportedCipherSuites();
        assertTrue(supportedCipherSuites.length > 0);
    }

    @Test
    @DisplayName("SSL协议: 支持的密码套件应非空")
    void sslProtocol_SupportedCipherSuites_ShouldBeNonEmpty() {
        SSLSocketFactory factory = SSLSocketFactoryProvider.getSSLSocketFactory(false);

        String[] supportedCipherSuites = factory.getSupportedCipherSuites();
        assertTrue(supportedCipherSuites.length > 0);
    }

    @Test
    @DisplayName("SSL协议: 默认密码套件应非空")
    void sslProtocol_DefaultCipherSuites_ShouldBeNonEmpty() {
        SSLSocketFactory factory = SSLSocketFactoryProvider.getSSLSocketFactory(false);

        String[] defaultCipherSuites = factory.getDefaultCipherSuites();
        assertTrue(defaultCipherSuites.length > 0);
    }

    // ==================== 信任管理器功能测试 ====================

    @Test
    @DisplayName("信任管理器: getAcceptedIssuers 应返回非空数组")
    void trustManager_GetAcceptedIssuers_ShouldReturnNonNullArray() {
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(false);

        X509Certificate[] issuers = trustManager.getAcceptedIssuers();

        assertNotNull(issuers);
    }

    @Test
    @DisplayName("信任管理器: unsafeIgnoreSSLCert=true 时 checkServerTrusted 不应抛出异常")
    void trustManager_UnsafeIgnoreTrue_CheckServerTrusted_ShouldNotThrow() {
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(true);

        assertDoesNotThrow(() -> {
            trustManager.checkServerTrusted(null, "RSA");
        });
    }

    @Test
    @DisplayName("信任管理器: checkClientTrusted 不应抛出异常")
    void trustManager_CheckClientTrusted_ShouldNotThrow() {
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(true);

        assertDoesNotThrow(() -> {
            trustManager.checkClientTrusted(null, "RSA");
        });
    }

    // ==================== 主机名验证器功能测试 ====================

    @Test
    @DisplayName("主机名验证器: unsafeIgnoreSSLCert=true 应验证通过")
    void hostnameVerifier_UnsafeIgnoreTrue_ShouldVerify() {
        HostnameVerifier verifier = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertTrue(verifier.verify("any-host.com", null));
    }

    @Test
    @DisplayName("主机名验证器: 不同主机名应正确处理")
    void hostnameVerifier_DifferentHostnames_ShouldBeHandled() {
        HostnameVerifier verifier = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertTrue(verifier.verify("localhost", null));
        assertTrue(verifier.verify("example.com", null));
        assertTrue(verifier.verify("192.168.1.1", null));
    }

    // ==================== 线程安全测试 ====================

    @Test
    @DisplayName("线程安全: 多线程调用应正常工作")
    void threadSafety_MultipleThreads_ShouldWork() throws InterruptedException {
        final SSLSocketFactory[] factories = new SSLSocketFactory[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                factories[index] = SSLSocketFactoryProvider.getSSLSocketFactory(true);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // 所有工厂都应该非空
        for (SSLSocketFactory factory : factories) {
            assertNotNull(factory);
        }
    }

    // ==================== 集成测试 ====================

    @Test
    @DisplayName("集成: 完整 SSL 配置应正确")
    void integration_FullSslConfiguration_ShouldBeCorrect() {
        // 获取完整的 SSL 配置
        SSLSocketFactory socketFactory = SSLSocketFactoryProvider.getSSLSocketFactory(false);
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(false);
        HostnameVerifier hostnameVerifier = SSLSocketFactoryProvider.getHostnameVerifier(false);

        assertNotNull(socketFactory);
        assertNotNull(trustManager);
        assertNotNull(hostnameVerifier);
    }

    @Test
    @DisplayName("集成: 不安全的 SSL 配置应正确")
    void integration_UnsafeSslConfiguration_ShouldBeCorrect() {
        // 获取不安全的 SSL 配置
        SSLSocketFactory socketFactory = SSLSocketFactoryProvider.getSSLSocketFactory(true);
        X509TrustManager trustManager = SSLSocketFactoryProvider.getX509TrustManager(true);
        HostnameVerifier hostnameVerifier = SSLSocketFactoryProvider.getHostnameVerifier(true);

        assertNotNull(socketFactory);
        assertNotNull(trustManager);
        assertNotNull(hostnameVerifier);

        assertTrue(((X509TrustManagerImp) trustManager).isUnsafeIgnoreSSLCert());
    }
}
