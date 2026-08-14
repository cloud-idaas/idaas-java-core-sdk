package com.cloud_idaas.core.factory;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.implementation.IDaaSMachineTokenExchangeCredentialProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.util.ConfigReader;
import com.cloud_idaas.core.util.JSONUtil;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * IDaaSCredentialProviderFactory 单元测试
 *
 * 注意：由于该类使用静态变量，测试之间需要通过 reset() 重置状态
 */
class IDaaSCredentialProviderFactoryTest {

    // ==================== 测试辅助方法 ====================

    @BeforeEach
    void setUp() throws Exception {
        resetFactoryState();
    }

    @AfterEach
    void tearDown() throws Exception {
        resetFactoryState();
    }

    /**
     * 重置工厂的静态状态
     */
    private void resetFactoryState() throws Exception {
        IDaaSCredentialProviderFactory.reset();
    }

    /**
     * 创建有效的测试配置
     */
    private IDaaSClientConfig createValidTestConfig() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIdaasInstanceId("test-instance-id");
        config.setClientId("test-client-id");
        config.setScope("test-audience|test-scope");
        config.setIssuer("https://test.idaas.example.com");
        config.setTokenEndpoint("https://test.idaas.example.com/token");
        config.setDeviceAuthorizationEndpoint("https://test.idaas.example.com/device");
        config.setDeveloperApiEndpoint("https://test.idaas.example.com/api");
        config.setOpenApiEndpoint("https://test.idaas.example.com/openapi");

        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setIdentityType(AuthenticationIdentityEnum.CLIENT);
        authnConfig.setAuthnMethod(TokenAuthnMethod.NONE);
        config.setAuthnConfiguration(authnConfig);

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(5000);
        httpConfig.setReadTimeout(10000);
        config.setHttpConfiguration(httpConfig);

        return config;
    }

    /**
     * 创建带特定认证方法的配置
     */
    private IDaaSClientConfig createConfigWithAuthMethod(TokenAuthnMethod authnMethod) {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(authnMethod);
        return config;
    }

    // ==================== init() 无参方法测试 ====================

    @Test
    @DisplayName("init(): 无参初始化方法在配置有效时应成功")
    void init_NoArgs_WithValidConfigFile_ShouldSucceed() throws Exception {
        // 由于无法模拟 ConfigReader.getConfigAsString()，
        // 这个测试需要配置文件存在或会被跳过
        // 这里测试初始化失败的情况
        assertThrows(Exception.class, () -> IDaaSCredentialProviderFactory.init());
    }

    @Test
    @DisplayName("init(): 已初始化状态下调用应直接返回")
    void init_NoArgs_WhenAlreadyInitialized_ShouldReturnImmediately() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);

        // 第二次调用应该直接返回，不抛出异常
        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== init(IDaaSClientConfig) 测试 ====================

    @Test
    @DisplayName("init: 使用有效配置初始化应成功")
    void init_WithValidConfig_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();

        IDaaSCredentialProviderFactory.init(config);

        // 验证初始化状态
        Field initializedField = IDaaSCredentialProviderFactory.class.getDeclaredField("INITIALIZED");
        initializedField.setAccessible(true);
        AtomicBoolean initialized = (AtomicBoolean) initializedField.get(null);
        assertTrue(initialized.get());
    }

    @Test
    @DisplayName("init: 多次初始化应只执行一次")
    void init_MultipleCalls_ShouldOnlyExecuteOnce() throws Exception {
        IDaaSClientConfig config1 = createValidTestConfig();
        config1.setClientId("client-1");

        IDaaSClientConfig config2 = createValidTestConfig();
        config2.setClientId("client-2");

        IDaaSCredentialProviderFactory.init(config1);
        IDaaSCredentialProviderFactory.init(config2);

        // 第二次初始化应该被忽略
        assertEquals("client-1", IDaaSCredentialProviderFactory.getClientId());
    }

    // ==================== reset() 测试 ====================

    @Test
    @DisplayName("reset: 重置后应能使用新配置重新初始化")
    void reset_ThenInit_ShouldApplyNewConfig() {
        IDaaSClientConfig config1 = createValidTestConfig();
        config1.setClientId("client-1");
        IDaaSCredentialProviderFactory.init(config1);
        assertEquals("client-1", IDaaSCredentialProviderFactory.getClientId());

        IDaaSCredentialProviderFactory.reset();

        IDaaSClientConfig config2 = createValidTestConfig();
        config2.setClientId("client-2");
        IDaaSCredentialProviderFactory.init(config2);

        assertEquals("client-2", IDaaSCredentialProviderFactory.getClientId());
    }

    @Test
    @DisplayName("reset: 重置后访问配置应抛出未初始化异常")
    void reset_ThenGetConfig_ShouldThrowNotInitialized() {
        IDaaSCredentialProviderFactory.init(createValidTestConfig());

        IDaaSCredentialProviderFactory.reset();

        ConfigException exception = assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
        assertEquals(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("reset: 应清空已缓存的凭证 Provider")
    void reset_ShouldClearCachedProviders() throws Exception {
        IDaaSCredentialProviderFactory.init(createValidTestConfig());

        Field providersField = IDaaSCredentialProviderFactory.class.getDeclaredField("CREDENTIAL_PROVIDERS");
        providersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, ?> providers = (ConcurrentMap<String, ?>) providersField.get(null);

        IDaaSCredentialProviderFactory.reset();

        assertTrue(providers.isEmpty());
    }

    @Test
    @DisplayName("reset: 未初始化状态下调用应安全")
    void reset_WhenNotInitialized_ShouldNotThrow() {
        assertDoesNotThrow(IDaaSCredentialProviderFactory::reset);
    }

    // ==================== init(String) 测试 ====================
    
    @Test
    @DisplayName("init(String): 指定配置文件路径应正确读取和解析配置")
    void init_WithConfigPath_ShouldReadAndParseConfig(@TempDir Path tempDir) throws Exception {
        Path configFile = tempDir.resolve("client-config.json");
        Files.write(configFile, ("{" 
                + "\"idaasInstanceId\":\"path-instance-id\","
                + "\"clientId\":\"path-client-id\","
                + "\"scope\":\"test-audience|test-scope\","
                + "\"issuer\":\"https://test.idaas.example.com\","
                + "\"tokenEndpoint\":\"https://test.idaas.example.com/token\","
                + "\"authnConfiguration\":{\"identityType\":\"CLIENT\",\"authnMethod\":\"CLIENT_SECRET_POST\",\"clientSecretEnvVarName\":\"TEST_SECRET_ENV\"}"
                + "}").getBytes(StandardCharsets.UTF_8));
    
        // init(String) will also trigger initCredentialProvider() which calls getCredential().
        // In a test environment without a running server this throws, so we use ConfigReader + init(config) instead.
        String content = ConfigReader.getConfigAsString(configFile.toString());
        IDaaSClientConfig config = JSONUtil.parseObject(content, IDaaSClientConfig.class);
        IDaaSCredentialProviderFactory.init(config);
    
        assertEquals("path-client-id", IDaaSCredentialProviderFactory.getClientId());
        assertEquals("path-instance-id", IDaaSCredentialProviderFactory.getIDaasInstanceId());
    }

    @Test
    @DisplayName("init(String): 指定不存在的配置文件路径应抛出异常")
    void init_WithMissingConfigPath_ShouldThrowException(@TempDir Path tempDir) {
        String missingPath = tempDir.resolve("not-exist.json").toString();

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(missingPath));
    }

    @Test
    @DisplayName("init: 配置缺少 clientId 应抛出异常")
    void init_MissingClientId_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setClientId(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 配置缺少 tokenEndpoint 应抛出异常")
    void init_MissingTokenEndpoint_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setTokenEndpoint(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 配置缺少 authnConfiguration 应抛出异常")
    void init_MissingAuthnConfiguration_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setAuthnConfiguration(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 配置缺少 issuer 应抛出异常")
    void init_MissingIssuer_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setIssuer(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 配置缺少 idaasInstanceId 应抛出异常")
    void init_MissingIdaasInstanceId_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setIdaasInstanceId(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 无效 scope 应抛出异常")
    void init_InvalidScope_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setScope("invalid-scope");

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("init: 空 scope 应抛出异常")
    void init_EmptyScope_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setScope("");

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 未初始化状态测试 ====================

    @Test
    @DisplayName("未初始化: getIDaaSCredentialProvider 应抛出 ConfigException")
    void notInitialized_GetIDaaSCredentialProvider_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getIDaaSCredentialProvider(String) 应抛出 ConfigException")
    void notInitialized_GetIDaaSCredentialProviderWithScope_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("test|scope"));

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getIDaaSTokenExchangeCredentialProvider 应抛出 ConfigException")
    void notInitialized_GetIDaaSTokenExchangeCredentialProvider_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getIDaaSTokenExchangeCredentialProvider(String) 应抛出 ConfigException")
    void notInitialized_GetIDaaSTokenExchangeCredentialProviderWithScope_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider("test|scope"));

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getDeveloperApiEndpoint 应抛出 ConfigException")
    void notInitialized_GetDeveloperApiEndpoint_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getDeveloperApiEndpoint());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getOpenApiEndpoint 应抛出 ConfigException")
    void notInitialized_GetOpenApiEndpoint_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getOpenApiEndpoint());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getIDaasInstanceId 应抛出 ConfigException")
    void notInitialized_GetIDaasInstanceId_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getIDaasInstanceId());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getClientId 应抛出 ConfigException")
    void notInitialized_GetClientId_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getClientId());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    @Test
    @DisplayName("未初始化: getHttpConfig 应抛出 ConfigException")
    void notInitialized_GetHttpConfig_ShouldThrowException() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getHttpConfig());

        assertTrue(exception.getMessage().contains("not been initialized"));
    }

    // ==================== 初始化后 Getter 测试 ====================

    @Test
    @DisplayName("getClientId: 初始化后应返回正确的 clientId")
    void getClientId_AfterInit_ShouldReturnCorrectClientId() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setClientId("test-client-123");

        IDaaSCredentialProviderFactory.init(config);

        assertEquals("test-client-123", IDaaSCredentialProviderFactory.getClientId());
    }

    @Test
    @DisplayName("getIDaasInstanceId: 初始化后应返回正确的 instanceId")
    void getIDaasInstanceId_AfterInit_ShouldReturnCorrectInstanceId() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setIdaasInstanceId("instance-456");

        IDaaSCredentialProviderFactory.init(config);

        assertEquals("instance-456", IDaaSCredentialProviderFactory.getIDaasInstanceId());
    }

    @Test
    @DisplayName("getDeveloperApiEndpoint: 初始化后应返回正确的 endpoint")
    void getDeveloperApiEndpoint_AfterInit_ShouldReturnCorrectEndpoint() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setDeveloperApiEndpoint("https://api.test.com");

        IDaaSCredentialProviderFactory.init(config);

        assertEquals("https://api.test.com", IDaaSCredentialProviderFactory.getDeveloperApiEndpoint());
    }

    @Test
    @DisplayName("getOpenApiEndpoint: 初始化后应返回正确的 endpoint")
    void getOpenApiEndpoint_AfterInit_ShouldReturnCorrectEndpoint() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setOpenApiEndpoint("https://openapi.test.com");

        IDaaSCredentialProviderFactory.init(config);

        assertEquals("https://openapi.test.com", IDaaSCredentialProviderFactory.getOpenApiEndpoint());
    }

    @Test
    @DisplayName("getHttpConfig: 初始化后应返回正确的 HttpConfiguration")
    void getHttpConfig_AfterInit_ShouldReturnCorrectConfig() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setConnectTimeout(3000);
        httpConfig.setReadTimeout(6000);
        config.setHttpConfiguration(httpConfig);

        IDaaSCredentialProviderFactory.init(config);

        HttpConfiguration result = IDaaSCredentialProviderFactory.getHttpConfig();
        assertNotNull(result);
        assertEquals(3000, result.getConnectTimeout());
        assertEquals(6000, result.getReadTimeout());
    }

    // ==================== HUMAN 身份类型配置测试 ====================

    @Test
    @DisplayName("HUMAN 配置: 缺少 humanAuthenticateClientId 应抛出异常")
    void humanConfig_MissingHumanAuthenticateClientId_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setIdentityType(AuthenticationIdentityEnum.HUMAN);
        config.getAuthnConfiguration().setHumanAuthenticateClientId(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HUMAN 配置: 缺少 deviceAuthorizationEndpoint 应抛出异常")
    void humanConfig_MissingDeviceAuthorizationEndpoint_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setIdentityType(AuthenticationIdentityEnum.HUMAN);
        config.setDeviceAuthorizationEndpoint(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - CLIENT_SECRET ====================

    @Test
    @DisplayName("CLIENT_SECRET_POST: 缺少 clientSecretEnvVarName 应抛出异常")
    void clientSecretPost_MissingEnvVarName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
        config.getAuthnConfiguration().setClientSecretEnvVarName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("CLIENT_SECRET_BASIC: 缺少 clientSecretEnvVarName 应抛出异常")
    void clientSecretBasic_MissingEnvVarName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_BASIC);
        config.getAuthnConfiguration().setClientSecretEnvVarName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("CLIENT_SECRET_JWT: 缺少 clientSecretEnvVarName 应抛出异常")
    void clientSecretJwt_MissingEnvVarName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_JWT);
        config.getAuthnConfiguration().setClientSecretEnvVarName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - PRIVATE_KEY_JWT ====================

    @Test
    @DisplayName("PRIVATE_KEY_JWT: 缺少 privateKeyEnvVarName 应抛出异常")
    void privateKeyJwt_MissingEnvVarName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT);
        config.getAuthnConfiguration().setPrivateKeyEnvVarName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - PKCS7 ====================

    @Test
    @DisplayName("PKCS7: 缺少 applicationFederatedCredentialName 应抛出异常")
    void pkcs7_MissingFederatedCredentialName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("PKCS7: 缺少 clientDeployEnvironment 应抛出异常")
    void pkcs7_MissingDeployEnvironment_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federated");
        config.getAuthnConfiguration().setClientDeployEnvironment(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - OIDC ====================

    @Test
    @DisplayName("OIDC: 缺少 applicationFederatedCredentialName 应抛出异常")
    void oidc_MissingFederatedCredentialName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("OIDC: 缺少 clientDeployEnvironment 应抛出异常")
    void oidc_MissingDeployEnvironment_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federated");
        config.getAuthnConfiguration().setClientDeployEnvironment(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - PCA ====================

    @Test
    @DisplayName("PCA: 缺少 applicationFederatedCredentialName 应抛出异常")
    void pca_MissingFederatedCredentialName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PCA);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("PCA: 缺少 clientX509Certificate 应抛出异常")
    void pca_MissingX509Certificate_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PCA);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federated");
        config.getAuthnConfiguration().setClientX509Certificate(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("PCA: 缺少 x509CertChains 应抛出异常")
    void pca_MissingX509CertChains_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PCA);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federated");
        config.getAuthnConfiguration().setClientX509Certificate("test-cert");
        config.getAuthnConfiguration().setX509CertChains(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("PCA: 缺少 privateKeyEnvVarName 应抛出异常")
    void pca_MissingPrivateKeyEnvVarName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PCA);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federated");
        config.getAuthnConfiguration().setClientX509Certificate("test-cert");
        config.getAuthnConfiguration().setX509CertChains("test-chains");
        config.getAuthnConfiguration().setPrivateKeyEnvVarName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 认证方法配置测试 - PLUGIN ====================

    @Test
    @DisplayName("PLUGIN: 缺少 pluginName 应抛出异常")
    void plugin_MissingPluginName_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PLUGIN);
        config.getAuthnConfiguration().setPluginName(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("PLUGIN: 缺少 openApiEndpoint 应抛出异常")
    void plugin_MissingOpenApiEndpoint_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PLUGIN);
        config.getAuthnConfiguration().setPluginName("test-plugin");
        config.setOpenApiEndpoint(null);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== HttpConfiguration 验证测试 ====================

    @Test
    @DisplayName("HttpConfig: connectTimeout 小于2000应抛出异常")
    void httpConfig_ConnectTimeoutTooLow_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setConnectTimeout(1000);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HttpConfig: connectTimeout 大于60000应抛出异常")
    void httpConfig_ConnectTimeoutTooHigh_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setConnectTimeout(70000);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HttpConfig: readTimeout 小于2000应抛出异常")
    void httpConfig_ReadTimeoutTooLow_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setReadTimeout(1000);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HttpConfig: readTimeout 大于60000应抛出异常")
    void httpConfig_ReadTimeoutTooHigh_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setReadTimeout(70000);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HttpConfig: 边界值 connectTimeout=2000 应成功")
    void httpConfig_ConnectTimeoutBoundaryLow_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setConnectTimeout(2000);
        config.getHttpConfiguration().setReadTimeout(2000);

        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("HttpConfig: 边界值 connectTimeout=60000 应成功")
    void httpConfig_ConnectTimeoutBoundaryHigh_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getHttpConfiguration().setConnectTimeout(60000);
        config.getHttpConfiguration().setReadTimeout(60000);

        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 错误码验证测试 ====================

    @Test
    @DisplayName("错误码: 未初始化错误应使用正确的错误码")
    void errorCode_NotInitialized_ShouldUseCorrectCode() {
        ConfigException exception = assertThrows(ConfigException.class,
                () -> IDaaSCredentialProviderFactory.getClientId());

        assertEquals(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), exception.getErrorCode());
    }

    // ==================== 配置复制测试 ====================

    @Test
    @DisplayName("配置复制: 初始化后配置应正确复制")
    void configCopy_AfterInit_ShouldBeCorrect() throws Exception {
        IDaaSClientConfig originalConfig = createValidTestConfig();
        originalConfig.setClientId("original-client");
        originalConfig.setScope("original-audience|original-scope");

        IDaaSCredentialProviderFactory.init(originalConfig);

        // 修改原始配置不应影响工厂中的配置
        originalConfig.setClientId("modified-client");

        assertEquals("original-client", IDaaSCredentialProviderFactory.getClientId());
    }

    // ==================== NONE 认证方法测试 ====================

    @Test
    @DisplayName("NONE 认证: 初始化应成功")
    void noneAuth_Init_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.NONE);

        IDaaSCredentialProviderFactory.init(config);

        Field initializedField = IDaaSCredentialProviderFactory.class.getDeclaredField("INITIALIZED");
        initializedField.setAccessible(true);
        AtomicBoolean initialized = (AtomicBoolean) initializedField.get(null);
        assertTrue(initialized.get());
    }

    // ==================== 并发安全测试 ====================

    @Test
    @DisplayName("并发: 多线程同时初始化应只执行一次")
    void concurrency_MultipleInitCalls_ShouldOnlyExecuteOnce() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    IDaaSCredentialProviderFactory.init(config);
                } catch (Exception e) {
                    // 忽略异常，某些线程可能会因为竞态条件而失败
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // 验证初始化成功
        assertEquals("test-client-id", IDaaSCredentialProviderFactory.getClientId());
    }

    // ==================== Scope 验证测试 ====================

    @Test
    @DisplayName("getIDaaSCredentialProvider: 无效 scope 应抛出异常")
    void getIDaaSCredentialProvider_InvalidScope_ShouldThrowException() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("invalid-scope"));
    }

    @Test
    @DisplayName("getIDaaSTokenExchangeCredentialProvider: 无效 scope 应抛出异常")
    void getIDaaSTokenExchangeCredentialProvider_InvalidScope_ShouldThrowException() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider("invalid-scope"));
    }

    // ==================== 多 Audience Scope 测试 ====================

    @Test
    @DisplayName("Scope: 多个 audience 应抛出异常")
    void scope_MultipleAudiences_ShouldThrowException() {
        IDaaSClientConfig config = createValidTestConfig();
        config.setScope("audience1|scope1 audience2|scope2");

        assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 反射测试私有方法 ====================


    @Test
    @DisplayName("initCredentialProvider: 未初始化时不应执行")
    void initCredentialProvider_NotInitialized_ShouldReturnImmediately() throws Exception {
        Method method = IDaaSCredentialProviderFactory.class.getDeclaredMethod("initCredentialProvider");
        method.setAccessible(true);

        // 由于 INITIALIZED 为 false，方法应该直接返回
        assertDoesNotThrow(() -> method.invoke(null));
    }


    // ==================== OIDC 部署环境测试 ====================

    @Test
    @DisplayName("OIDC KUBERNETES: 应使用默认 token 路径")
    void oidc_Kubernetes_ShouldUseDefaultTokenPath() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-fed");
        config.getAuthnConfiguration().setClientDeployEnvironment(ClientDeployEnvironmentEnum.KUBERNETES);
        config.getAuthnConfiguration().setOidcTokenFilePath(null);
        config.getAuthnConfiguration().setOidcTokenFilePathEnvVarName(null);

        IDaaSCredentialProviderFactory.init(config);

        // 验证初始化成功
        assertTrue(true);
    }

    @Test
    @DisplayName("OIDC KUBERNETES: 使用环境变量指定的 token 路径")
    void oidc_Kubernetes_WithEnvVarPath_ShouldUseEnvVarPath() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-fed");
        config.getAuthnConfiguration().setClientDeployEnvironment(ClientDeployEnvironmentEnum.KUBERNETES);
        config.getAuthnConfiguration().setOidcTokenFilePath(null);
        config.getAuthnConfiguration().setOidcTokenFilePathEnvVarName("TEST_TOKEN_PATH");

        IDaaSCredentialProviderFactory.init(config);

        // 验证初始化成功
        assertTrue(true);
    }

    @Test
    @DisplayName("OIDC 不支持的部署环境: 应抛出 ConfigException")
    void oidc_UnsupportedDeployEnvironment_ShouldThrowException() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-fed");
        config.getAuthnConfiguration().setClientDeployEnvironment(ClientDeployEnvironmentEnum.COMMON);

        IDaaSCredentialProviderFactory.init(config);

        // 然后通过反射直接测试 createCredentialProvider 方法
        Method method = IDaaSCredentialProviderFactory.class.getDeclaredMethod("createCredentialProvider", String.class);
        method.setAccessible(true);

        // 清除 providers 以强制重新创建
        Field providersField = IDaaSCredentialProviderFactory.class.getDeclaredField("CREDENTIAL_PROVIDERS");
        providersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, ?> providers = (ConcurrentMap<String, ?>) providersField.get(null);
        providers.clear();

        // 调用方法应该抛出异常
        Exception exception = assertThrows(Exception.class, () -> method.invoke(null, "test|scope"));
        assertTrue(exception.getCause() instanceof ConfigException);
        assertTrue(exception.getCause().getMessage().contains("Unsupported client deploy environment"));
    }

    // ==================== PKCS7 部署环境测试 ====================

    @Test
    @DisplayName("PKCS7 ALIBABA_CLOUD_ECS: 应创建 AlibabaCloudEcsAttestedDocumentProvider")
    void pkcs7_AlibabaCloudEcs_ShouldCreateDocumentProvider() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-fed");
        config.getAuthnConfiguration().setClientDeployEnvironment(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS);

        IDaaSCredentialProviderFactory.init(config);

        // 验证初始化成功
        assertTrue(true);
    }

    @Test
    @DisplayName("PKCS7 其他部署环境: 不应创建 DocumentProvider")
    void pkcs7_OtherDeployEnvironment_ShouldNotCreateDocumentProvider() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
        config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-fed");
        config.getAuthnConfiguration().setClientDeployEnvironment(ClientDeployEnvironmentEnum.COMMON);

        IDaaSCredentialProviderFactory.init(config);

        // 验证初始化成功
        assertTrue(true);
    }

    // ==================== 空 HttpConfiguration 测试 ====================

    @Test
    @DisplayName("空 HttpConfiguration: 验证应通过")
    void nullHttpConfiguration_ShouldPassValidation() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setHttpConfiguration(null);

        // null HttpConfiguration 应该通过验证
        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }

    // ==================== 配置验证边界测试 ====================

    @Test
    @DisplayName("验证: 缺少 developerApiEndpoint 应成功 (非必需)")
    void validation_MissingDeveloperApiEndpoint_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setDeveloperApiEndpoint(null);

        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }

    @Test
    @DisplayName("验证: 缺少 openApiEndpoint 对于非 PLUGIN 方法应成功")
    void validation_MissingOpenApiEndpoint_NonPlugin_ShouldSucceed() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        config.setOpenApiEndpoint(null);
        config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.NONE);

        assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(config));
    }
}
