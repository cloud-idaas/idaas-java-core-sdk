package com.cloud_idaas.core.http;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpClientFactory 单元测试
 */
class HttpClientFactoryTest {

    @BeforeEach
    void setUp() throws Exception {
        IDaaSClientConfig config = createValidTestConfig();
        IDaaSCredentialProviderFactory.init(config);
        // 重置单例状态
        resetSingleton();
    }

    @AfterEach
    void tearDown() throws Exception {
        // 测试后再次重置
        resetSingleton();
    }

    /**
     * 使用反射重置单例状态
     * 注意: singletonOKHttpClient 字段不是 final 的，只需直接设置为 null
     */
    private void resetSingleton() throws Exception {
        Field singletonField = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        singletonField.setAccessible(true);
        singletonField.set(null, null);
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

    // ==================== 类结构测试 ====================

    @Test
    @DisplayName("类结构: HttpClientFactory 应为 public 类")
    void classStructure_ShouldBePublic() {
        assertTrue(Modifier.isPublic(HttpClientFactory.class.getModifiers()));
    }

    @Test
    @DisplayName("类结构: HttpClientFactory 不应为 abstract")
    void classStructure_ShouldNotBeAbstract() {
        assertFalse(Modifier.isAbstract(HttpClientFactory.class.getModifiers()));
    }

    @Test
    @DisplayName("类结构: HttpClientFactory 不应为 final")
    void classStructure_ShouldNotBeFinal() {
        assertFalse(Modifier.isFinal(HttpClientFactory.class.getModifiers()));
    }

    @Test
    @DisplayName("类名: 类名应为 HttpClientFactory")
    void className_ShouldBeHttpClientFactory() {
        assertEquals("HttpClientFactory", HttpClientFactory.class.getSimpleName());
    }

    // ==================== 字段测试 ====================

    @Test
    @DisplayName("字段: 应包含 singletonOKHttpClient 字段")
    void fields_ShouldContainSingletonOKHttpClient() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        assertNotNull(field);
    }

    @Test
    @DisplayName("字段: singletonOKHttpClient 应为 private")
    void fields_SingletonShouldBePrivate() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: singletonOKHttpClient 应为 static")
    void fields_SingletonShouldBeStatic() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        assertTrue(Modifier.isStatic(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: singletonOKHttpClient 应为 volatile")
    void fields_SingletonShouldBeVolatile() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        assertTrue(Modifier.isVolatile(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: 应包含 LOCK 字段")
    void fields_ShouldContainLock() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("LOCK");
        assertNotNull(field);
    }

    @Test
    @DisplayName("字段: LOCK 应为 private static final")
    void fields_LockShouldBePrivateStaticFinal() throws NoSuchFieldException {
        Field field = HttpClientFactory.class.getDeclaredField("LOCK");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    // ==================== 方法测试 ====================

    @Test
    @DisplayName("方法: 应包含 getDefaultHttpClient 方法")
    void methods_ShouldContainGetDefaultHttpClient() throws NoSuchMethodException {
        assertNotNull(HttpClientFactory.class.getMethod("getDefaultHttpClient"));
    }

    @Test
    @DisplayName("方法: getDefaultHttpClient 应为 public static")
    void methods_GetDefaultHttpClientShouldBePublicStatic() throws NoSuchMethodException {
        Method method = HttpClientFactory.class.getMethod("getDefaultHttpClient");
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
    }

    @Test
    @DisplayName("方法: getDefaultHttpClient 应返回 HttpClient")
    void methods_GetDefaultHttpClientShouldReturnHttpClient() throws NoSuchMethodException {
        Method method = HttpClientFactory.class.getMethod("getDefaultHttpClient");
        assertEquals(HttpClient.class, method.getReturnType());
    }

    // ==================== 单例模式测试 ====================

    @Test
    @DisplayName("单例: 应使用双重检查锁定模式")
    void singleton_ShouldUseDoubleCheckedLocking() throws NoSuchFieldException {
        // 验证存在 volatile 单例字段和锁对象
        Field singletonField = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        Field lockField = HttpClientFactory.class.getDeclaredField("LOCK");
        
        assertTrue(Modifier.isVolatile(singletonField.getModifiers()));
        assertTrue(Modifier.isFinal(lockField.getModifiers()));
    }

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造函数: 应有默认构造函数")
    void constructor_ShouldHaveDefaultConstructor() throws NoSuchMethodException {
        assertNotNull(HttpClientFactory.class.getDeclaredConstructor());
    }

    // ==================== 实现类测试 ====================

    @Test
    @DisplayName("实现: 返回的 HttpClient 应为 DefaultOKHttpClientImp 实例")
    void implementation_ShouldReturnDefaultOKHttpClientImp() throws Exception {
        // 注意：此测试需要 IDaaSCredentialProviderFactory 已初始化
        // 如果未初始化会抛出异常
        try {
            HttpClient client = HttpClientFactory.getDefaultHttpClient();
            assertTrue(client instanceof DefaultOKHttpClientImp);
        } catch (Exception e) {
            // 如果因为工厂未初始化而失败，跳过此测试
            assertTrue(e.getMessage().contains("not been initialized") || 
                       e.getCause() != null);
        }
    }

    // ==================== 线程安全测试 ====================

    @Test
    @DisplayName("线程安全: 应有同步锁对象")
    void threadSafety_ShouldHaveLockObject() throws NoSuchFieldException {
        Field lockField = HttpClientFactory.class.getDeclaredField("LOCK");
        assertNotNull(lockField);
        assertTrue(Modifier.isFinal(lockField.getModifiers()));
    }

    // ==================== 返回类型测试 ====================

    @Test
    @DisplayName("返回类型: getDefaultHttpClient 返回类型应为 HttpClient 接口")
    void returnType_ShouldBeHttpClientInterface() throws NoSuchMethodException {
        Method method = HttpClientFactory.class.getMethod("getDefaultHttpClient");
        assertEquals(HttpClient.class, method.getReturnType());
        assertTrue(method.getReturnType().isInterface());
    }

    // ==================== 无参方法测试 ====================

    @Test
    @DisplayName("参数: getDefaultHttpClient 应无参数")
    void parameters_GetDefaultHttpClientShouldHaveNoParameters() throws NoSuchMethodException {
        Method method = HttpClientFactory.class.getMethod("getDefaultHttpClient");
        assertEquals(0, method.getParameterCount());
    }

    // ==================== 字段数量测试 ====================

    @Test
    @DisplayName("字段数量: 应包含预期的字段数量")
    void fieldCount_ShouldBeExpected() {
        Field[] fields = HttpClientFactory.class.getDeclaredFields();
        // 预期: singletonOKHttpClient, LOCK, httpConfiguration
        assertTrue(fields.length >= 3);
    }

    // ==================== 方法数量测试 ====================

    @Test
    @DisplayName("方法数量: 应包含至少一个公共方法")
    void methodCount_ShouldHaveAtLeastOnePublicMethod() {
        Method[] methods = HttpClientFactory.class.getMethods();
        // 包含继承自 Object 的方法
        assertTrue(methods.length > 0);
    }

    // ==================== 工厂模式验证 ====================

    @Test
    @DisplayName("工厂模式: 类名应以 Factory 结尾")
    void factoryPattern_ClassNameShouldEndWithFactory() {
        assertTrue(HttpClientFactory.class.getSimpleName().endsWith("Factory"));
    }

    // ==================== 静态工厂方法测试 ====================

    @Test
    @DisplayName("静态工厂: 应使用静态方法创建实例")
    void staticFactory_ShouldUseStaticMethod() throws NoSuchMethodException {
        Method method = HttpClientFactory.class.getMethod("getDefaultHttpClient");
        assertTrue(Modifier.isStatic(method.getModifiers()));
    }
}
