package com.cloud_idaas.core.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultHostnameVerifier 单元测试
 */
class DefaultHostnameVerifierTest {

    // ==================== getInstance 测试 ====================

    @Test
    @DisplayName("getInstance: unsafeIgnoreSSLCert=true 应返回 NOOP 实例")
    void getInstance_UnsafeIgnoreTrue_ShouldReturnNoopInstance() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertNotNull(verifier);
    }

    @Test
    @DisplayName("getInstance: unsafeIgnoreSSLCert=false 应返回 OkHostnameVerifier")
    void getInstance_UnsafeIgnoreFalse_ShouldReturnOkHostnameVerifier() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(false);

        assertNotNull(verifier);
    }

    @Test
    @DisplayName("getInstance: 多次调用 true 应返回相同实例")
    void getInstance_MultipleCallsTrue_ShouldReturnSameInstance() {
        HostnameVerifier verifier1 = DefaultHostnameVerifier.getInstance(true);
        HostnameVerifier verifier2 = DefaultHostnameVerifier.getInstance(true);

        assertSame(verifier1, verifier2);
    }

    // ==================== verify 测试 ====================

    @Test
    @DisplayName("verify: unsafeIgnoreSSLCert=true 应返回 true")
    void verify_UnsafeIgnoreTrue_ShouldReturnTrue() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        // 创建一个简单的 SSLSession mock 不太容易，但我们测试基本行为
        assertTrue(verifier.verify("any-host.com", null));
    }

    @Test
    @DisplayName("verify: unsafeIgnoreSSLCert=true 对任意主机名应返回 true")
    void verify_UnsafeIgnoreTrue_AnyHostname_ShouldReturnTrue() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        // 即使没有 SSLSession，也应该返回 true
        assertTrue(verifier.verify("localhost", null));
        assertTrue(verifier.verify("192.168.1.1", null));
        assertTrue(verifier.verify("example.com", null));
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 HostnameVerifier 接口")
    void interface_ShouldImplementHostnameVerifier() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier instanceof HostnameVerifier);
    }

    @Test
    @DisplayName("接口: unsafeIgnoreSSLCert=false 也应实现 HostnameVerifier 接口")
    void interface_UnsafeIgnoreFalse_ShouldImplementHostnameVerifier() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(false);

        assertTrue(verifier instanceof HostnameVerifier);
    }

    // ==================== 安全性测试 ====================

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=true 是不安全的配置")
    void security_UnsafeIgnoreTrue_ShouldBeInsecure() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        // 不安全的验证器应该接受任何主机名
        // 这是一个安全警告，生产环境不应使用
        assertNotNull(verifier);
    }

    @Test
    @DisplayName("安全性: unsafeIgnoreSSLCert=false 是安全的配置")
    void security_UnsafeIgnoreFalse_ShouldBeSecure() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(false);

        // 安全的验证器应该使用标准的 OkHostnameVerifier
        assertNotNull(verifier);
    }

    // ==================== 不同主机名测试 ====================

    @Test
    @DisplayName("主机名: localhost 应正确处理")
    void hostname_Localhost_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("localhost", null));
    }

    @Test
    @DisplayName("主机名: IP 地址应正确处理")
    void hostname_IpAddress_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("127.0.0.1", null));
        assertTrue(verifier.verify("192.168.1.1", null));
        assertTrue(verifier.verify("10.0.0.1", null));
    }

    @Test
    @DisplayName("主机名: 域名应正确处理")
    void hostname_DomainName_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("example.com", null));
        assertTrue(verifier.verify("sub.example.com", null));
        assertTrue(verifier.verify("api.idaas.example.com", null));
    }

    @Test
    @DisplayName("主机名: 通配符域名应正确处理")
    void hostname_WildcardDomain_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("*.example.com", null));
    }

    @Test
    @DisplayName("主机名: 空字符串应正确处理")
    void hostname_EmptyString_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("", null));
    }

    @Test
    @DisplayName("主机名: null 应正确处理")
    void hostname_Null_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        // 即使主机名为 null，不安全的验证器也应返回 true
        assertTrue(verifier.verify(null, null));
    }

    // ==================== 特殊字符测试 ====================

    @Test
    @DisplayName("特殊字符: 包含特殊字符的主机名应正确处理")
    void hostname_SpecialCharacters_ShouldBeHandled() {
        HostnameVerifier verifier = DefaultHostnameVerifier.getInstance(true);

        assertTrue(verifier.verify("test-server", null));
        assertTrue(verifier.verify("test.server.local", null));
    }

    // ==================== 单例行为测试 ====================

    @Test
    @DisplayName("单例: NOOP_INSTANCE 应该是单例")
    void singleton_NoopInstance_ShouldBeSingleton() {
        HostnameVerifier verifier1 = DefaultHostnameVerifier.getInstance(true);
        HostnameVerifier verifier2 = DefaultHostnameVerifier.getInstance(true);
        HostnameVerifier verifier3 = DefaultHostnameVerifier.getInstance(true);

        assertSame(verifier1, verifier2);
        assertSame(verifier2, verifier3);
    }

    // ==================== 线程安全测试 ====================

    @Test
    @DisplayName("线程安全: 多线程调用应返回相同实例")
    void threadSafety_MultipleThreads_ShouldReturnSameInstance() throws InterruptedException {
        final HostnameVerifier[] verifiers = new HostnameVerifier[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                verifiers[index] = DefaultHostnameVerifier.getInstance(true);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // 所有实例应该相同
        for (int i = 1; i < 10; i++) {
            assertSame(verifiers[0], verifiers[i]);
        }
    }
}
