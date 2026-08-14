package com.cloud_idaas.core.implementation.authentication.jwt;

import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaticPrivateKeyAssertionProvider 单元测试
 */
class StaticPrivateKeyAssertionProviderTest {

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_TOKEN_ENDPOINT = "https://example.com/token";
    private static final String TEST_SCOPE = "openid profile";

    private static String RSA_2048_PEM;
    private static String RSA_3072_PEM;
    private static String RSA_4096_PEM;
    private static String EC_P256_PEM;
    private static String EC_P384_PEM;
    private static String EC_P521_PEM;

    @BeforeAll
    static void generateTestKeys() throws Exception {
        RSA_2048_PEM = generateRsaKeyPem(2048);
        RSA_3072_PEM = generateRsaKeyPem(3072);
        RSA_4096_PEM = generateRsaKeyPem(4096);
        EC_P256_PEM = generateEcKeyPem("secp256r1");
        EC_P384_PEM = generateEcKeyPem("secp384r1");
        EC_P521_PEM = generateEcKeyPem("secp521r1");
    }

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 使用有效的 RSA 2048 密钥应成功创建实例")
    void constructor_WithValidRsa2048Key_ShouldCreateInstance() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        assertNotNull(provider);
    }

    @Test
    @DisplayName("构造函数: 使用有效的 EC P-256 密钥应成功创建实例")
    void constructor_WithValidEcP256Key_ShouldCreateInstance() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(EC_P256_PEM);
        assertNotNull(provider);
    }

    @Test
    @DisplayName("构造函数: 使用无效密钥字符串应抛出异常")
    void constructor_WithInvalidKey_ShouldThrowException() {
        assertThrows(Exception.class, () ->
                new StaticPrivateKeyAssertionProvider("invalid_key_string"));
    }

    @Test
    @DisplayName("构造函数: 使用空字符串密钥应抛出异常")
    void constructor_WithEmptyKey_ShouldThrowException() {
        assertThrows(Exception.class, () ->
                new StaticPrivateKeyAssertionProvider(""));
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Getter/Setter: clientId 初始值应为 null")
    void clientId_InitialValue_ShouldBeNull() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        assertNull(provider.getClientId());
    }

    @Test
    @DisplayName("Getter/Setter: clientId 应正确设置和获取")
    void clientId_GetterSetter_ShouldWorkCorrectly() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setClientId(TEST_CLIENT_ID);
        assertEquals(TEST_CLIENT_ID, provider.getClientId());
    }

    @Test
    @DisplayName("Getter/Setter: tokenEndpoint 初始值应为 null")
    void tokenEndpoint_InitialValue_ShouldBeNull() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        assertNull(provider.getTokenEndpoint());
    }

    @Test
    @DisplayName("Getter/Setter: tokenEndpoint 应正确设置和获取")
    void tokenEndpoint_GetterSetter_ShouldWorkCorrectly() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);
        assertEquals(TEST_TOKEN_ENDPOINT, provider.getTokenEndpoint());
    }

    @Test
    @DisplayName("Getter/Setter: scope 初始值应为 null")
    void scope_InitialValue_ShouldBeNull() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        assertNull(provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: scope 应正确设置和获取")
    void scope_GetterSetter_ShouldWorkCorrectly() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setScope(TEST_SCOPE);
        assertEquals(TEST_SCOPE, provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: 应支持 null 值")
    void getterSetter_WithNull_ShouldWorkCorrectly() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
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
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setClientId("");
        provider.setTokenEndpoint("");
        provider.setScope("");
        assertEquals("", provider.getClientId());
        assertEquals("", provider.getTokenEndpoint());
        assertEquals("", provider.getScope());
    }

    @Test
    @DisplayName("Getter/Setter: 应支持多次更新并取最新值")
    void getterSetter_MultipleUpdates_ShouldUseLastValue() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setClientId("id-1");
        provider.setClientId("id-2");
        provider.setClientId("id-3");
        assertEquals("id-3", provider.getClientId());
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 JwtClientAssertionProvider 接口")
    void interface_ShouldImplementJwtClientAssertionProvider() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        assertTrue(provider instanceof JwtClientAssertionProvider);
    }

    // ==================== getClientAssertion 基础测试 ====================

    @Test
    @DisplayName("getClientAssertion: 配置完整时应返回有效 JWT（三段式）")
    void getClientAssertion_WithCompleteConfig_ShouldReturnValidJwt() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(RSA_2048_PEM);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        assertFalse(assertion.isEmpty());
        String[] parts = assertion.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("getClientAssertion: 多次调用应返回不同的 JWT（不同的 jti）")
    void getClientAssertion_MultipleCalls_ShouldReturnDifferentJwt() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(RSA_2048_PEM);

        String assertion1 = provider.getClientAssertion();
        String assertion2 = provider.getClientAssertion();

        assertNotNull(assertion1);
        assertNotNull(assertion2);
        assertNotEquals(assertion1, assertion2);
    }

    @Test
    @DisplayName("getClientAssertion: 未设置 clientId 时应仍能生成 assertion")
    void getClientAssertion_WithoutClientId_ShouldGenerateAssertion() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        String[] parts = assertion.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("getClientAssertion: 未设置 tokenEndpoint 时应仍能生成 assertion")
    void getClientAssertion_WithoutTokenEndpoint_ShouldGenerateAssertion() {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        provider.setClientId(TEST_CLIENT_ID);

        String assertion = provider.getClientAssertion();

        assertNotNull(assertion);
        String[] parts = assertion.split("\\.");
        assertEquals(3, parts.length);
    }

    // ==================== 算法选择测试 ====================

    @Test
    @DisplayName("算法选择: RSA 2048 密钥应使用 RS256 算法")
    void algorithmSelection_Rsa2048_ShouldUseRs256() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(RSA_2048_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("RS256", extractAlgFromJwt(assertion));
    }

    @Test
    @DisplayName("算法选择: RSA 3072 密钥应使用 RS384 算法")
    void algorithmSelection_Rsa3072_ShouldUseRs384() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(RSA_3072_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("RS384", extractAlgFromJwt(assertion));
    }

    @Test
    @DisplayName("算法选择: RSA 4096 密钥应使用 RS512 算法")
    void algorithmSelection_Rsa4096_ShouldUseRs512() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(RSA_4096_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("RS512", extractAlgFromJwt(assertion));
    }

    @Test
    @DisplayName("算法选择: EC P-256 (secp256r1) 密钥应使用 ES256 算法")
    void algorithmSelection_EcP256_ShouldUseEs256() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(EC_P256_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("ES256", extractAlgFromJwt(assertion));
    }

    @Test
    @DisplayName("算法选择: EC P-384 (secp384r1) 密钥应使用 ES384 算法")
    void algorithmSelection_EcP384_ShouldUseEs384() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(EC_P384_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("ES384", extractAlgFromJwt(assertion));
    }

    @Test
    @DisplayName("算法选择: EC P-521 (secp521r1) 密钥应使用 ES512 算法")
    void algorithmSelection_EcP521_ShouldUseEs512() {
        StaticPrivateKeyAssertionProvider provider = createConfiguredProvider(EC_P521_PEM);

        String assertion = provider.getClientAssertion();

        assertEquals("ES512", extractAlgFromJwt(assertion));
    }

    // ==================== 多实例独立性测试 ====================

    @Test
    @DisplayName("多实例: 不同实例应独立维护各自的配置")
    void multipleInstances_ShouldMaintainIndependentConfig() {
        StaticPrivateKeyAssertionProvider provider1 = new StaticPrivateKeyAssertionProvider(RSA_2048_PEM);
        StaticPrivateKeyAssertionProvider provider2 = new StaticPrivateKeyAssertionProvider(EC_P256_PEM);

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
    @DisplayName("多实例: RSA 实例与 EC 实例分别生成正确算法的 JWT")
    void multipleInstances_RsaAndEc_ShouldGenerateCorrectAlgorithm() {
        StaticPrivateKeyAssertionProvider rsaProvider = createConfiguredProvider(RSA_2048_PEM);
        StaticPrivateKeyAssertionProvider ecProvider = createConfiguredProvider(EC_P256_PEM);

        String rsaAssertion = rsaProvider.getClientAssertion();
        String ecAssertion = ecProvider.getClientAssertion();

        assertEquals("RS256", extractAlgFromJwt(rsaAssertion));
        assertEquals("ES256", extractAlgFromJwt(ecAssertion));
    }

    // ==================== 辅助方法 ====================

    private StaticPrivateKeyAssertionProvider createConfiguredProvider(String pem) {
        StaticPrivateKeyAssertionProvider provider = new StaticPrivateKeyAssertionProvider(pem);
        provider.setClientId(TEST_CLIENT_ID);
        provider.setTokenEndpoint(TEST_TOKEN_ENDPOINT);
        return provider;
    }

    /**
     * 从 JWT compact 序列化字符串中提取 header 中的 alg 字段值
     */
    private static String extractAlgFromJwt(String jwt) {
        String[] parts = jwt.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        // header JSON 格式: {"alg":"RS256","..."}
        int algIndex = headerJson.indexOf("\"alg\":\"");
        if (algIndex < 0) return null;
        int start = algIndex + 7;
        int end = headerJson.indexOf('"', start);
        return headerJson.substring(start, end);
    }

    private static String generateRsaKeyPem(int keySize) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize);
        KeyPair kp = kpg.generateKeyPair();
        return toPkcs8Pem(kp.getPrivate());
    }

    private static String generateEcKeyPem(String curveName) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec(curveName));
        KeyPair kp = kpg.generateKeyPair();
        return toPkcs8Pem(kp.getPrivate());
    }

    private static String toPkcs8Pem(PrivateKey key) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(key.getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----";
    }
}
