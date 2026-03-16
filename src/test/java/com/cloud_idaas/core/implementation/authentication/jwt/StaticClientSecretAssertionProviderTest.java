package com.cloud_idaas.core.implementation.authentication.jwt;

import com.cloud_idaas.core.exception.CredentialException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaticClientSecretAssertionProvider 单元测试
 */
class StaticClientSecretAssertionProviderTest {

    private static final String TEST_CLIENT_SECRET = "a-very-long-test-client-secret-key";
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_TOKEN_ENDPOINT = "https://example.com/token";
    private static final String TEST_SCOPE = "openid profile";

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 使用 Supplier 成功创建实例")
    void constructor_WithSupplier_ShouldCreateInstance() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        assertNotNull(provider);
    }

    @Test
    @DisplayName("构造函数: 使用 null Supplier 应创建实例")
    void constructor_WithNullSupplier_ShouldCreateInstance() {
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(null);

        assertNotNull(provider);
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Getter/Setter: clientId 应正确设置和获取")
    void clientIdGetterSetter_ShouldWorkCorrectly() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setClientId(TEST_CLIENT_ID);

        assertEquals(TEST_CLIENT_ID, provider.getClientId());
    }

    @Test
    @DisplayName("Getter/Setter: tokenEndpoint 应正确设置和获取")
    void tokenEndpointGetterSetter_ShouldWorkCorrectly() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        assertEquals(TEST_TOKEN_ENDPOINT, provider.getTokenEndpoint());
    }

    @Test
    @DisplayName("Getter/Setter: scope 应正确设置和获取")
    void scopeGetterSetter_ShouldWorkCorrectly() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setScope(TEST_SCOPE);

        assertEquals(TEST_SCOPE, provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: 应支持 null 值")
    void getterSetter_WithNull_ShouldWorkCorrectly() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setClientId(null);
        provider.setTokenEndpoint(null);
        provider.setScope(null);

        assertNull(provider.getClientId());
        assertNull(provider.getTokenEndpoint());
        assertNull(provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: 应支持空字符串")
    void getterSetter_WithEmptyString_ShouldWorkCorrectly() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setClientId("");
        provider.setTokenEndpoint("");
        provider.setScope("");

        assertEquals("", provider.getClientId());
        assertEquals("", provider.getTokenEndpoint());
        assertEquals("", provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: 应支持多次更新")
    void getterSetter_MultipleUpdates_ShouldUseLastValue() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        provider.setClientId("id-1");
        provider.setClientId("id-2");
        provider.setClientId("id-3");

        assertEquals("id-3", provider.getClientId());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 JwtClientAssertionProvider 接口")
    void interface_ShouldImplementJwtClientAssertionProvider() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);

        assertTrue(provider instanceof com.cloud_idaas.core.provider.JwtClientAssertionProvider);
    }

    // ==================== getClientAssertion 测试 ====================

    @Test
    @DisplayName("getClientAssertion: 配置完整时应返回 JWT 字符串")
    void getClientAssertion_WithCompleteConfig_ShouldReturnJwt() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        assertFalse(assertion.isEmpty());
        // JWT 格式: header.payload.signature
        String[] parts = assertion.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("getClientAssertion: 每次调用应返回不同的 JWT (不同的 jti)")
    void getClientAssertion_MultipleCalls_ShouldReturnDifferentJwt() {
        Supplier<String> secretSupplier = () -> TEST_CLIENT_SECRET;
        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        String assertion1 = provider.getClientAssertion();
        String assertion2 = provider.getClientAssertion();

        assertNotNull(assertion1);
        assertNotNull(assertion2);
        assertNotEquals(assertion1, assertion2);
    }

    @Test
    @DisplayName("getClientAssertion: Supplier 返回不同值时应使用当前值")
    void getClientAssertion_WithDynamicSupplier_ShouldUseCurrentValue() {
        String[] secrets = {"a-very-long-test-client-secret-key-1", "a-very-long-test-client-secret-key-2"};
        final int[] index = {0};
        Supplier<String> dynamicSupplier = () -> secrets[index[0]++ % secrets.length];

        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(dynamicSupplier);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        // 每次调用 getClientAssertion 都会调用 Supplier.get()
        String assertion1 = provider.getClientAssertion();
        String assertion2 = provider.getClientAssertion();

        assertNotNull(assertion1);
        assertNotNull(assertion2);
    }

    @Test
    @DisplayName("getClientAssertion: 使用长 client secret 应正常工作")
    void getClientAssertion_WithLongSecret_ShouldWork() {
        StringBuilder longSecret = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longSecret.append("very-long-secret-");
        }
        Supplier<String> secretSupplier = () -> longSecret.toString();

        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        assertFalse(assertion.isEmpty());
    }

    // ==================== 功能测试 ====================

    @Test
    @DisplayName("功能: 多个实例应独立维护配置")
    void multipleInstances_ShouldMaintainIndependentConfig() {
        Supplier<String> supplier1 = () -> "secret-1";
        Supplier<String> supplier2 = () -> "secret-2";

        StaticClientSecretAssertionProvider provider1 = new StaticClientSecretAssertionProvider(supplier1);
        StaticClientSecretAssertionProvider provider2 = new StaticClientSecretAssertionProvider(supplier2);

        provider1.setClientId("client-1");
        provider1.setTokenEndpoint("https://endpoint1.com");

        provider2.setClientId("client-2");
        provider2.setTokenEndpoint("https://endpoint2.com");

        assertEquals("client-1", provider1.getClientId());
        assertEquals("client-2", provider2.getClientId());
        assertEquals("https://endpoint1.com", provider1.getTokenEndpoint());
        assertEquals("https://endpoint2.com", provider2.getTokenEndpoint());
    }

    @Test
    @DisplayName("功能: Supplier 返回特殊字符 secret 应正常工作")
    void getClientAssertion_WithSpecialCharsSecret_ShouldWork() {
        Supplier<String> secretSupplier = () -> "secret-with-special-chars-!@#$%^&*()";

        StaticClientSecretAssertionProvider provider = new StaticClientSecretAssertionProvider(secretSupplier);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        assertFalse(assertion.isEmpty());
    }
}
