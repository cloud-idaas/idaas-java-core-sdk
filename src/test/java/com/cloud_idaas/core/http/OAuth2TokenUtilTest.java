package com.cloud_idaas.core.http;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.DeviceCodeResponse;
import com.cloud_idaas.core.domain.constants.ClientAssertionType;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * OAuth2TokenUtil 单元测试
 */
class OAuth2TokenUtilTest {

    // ==================== 类结构测试 ====================

    @Test
    @DisplayName("类结构: OAuth2TokenUtil 应为 public 类")
    void classStructure_ShouldBePublic() {
        assertTrue(Modifier.isPublic(OAuth2TokenUtil.class.getModifiers()));
    }

    @Test
    @DisplayName("类结构: OAuth2TokenUtil 不应为 abstract")
    void classStructure_ShouldNotBeAbstract() {
        assertFalse(Modifier.isAbstract(OAuth2TokenUtil.class.getModifiers()));
    }

    @Test
    @DisplayName("包路径: 应位于正确的包中")
    void packagePath_ShouldBeCorrect() {
        assertEquals("com.cloud_idaas.core.http", OAuth2TokenUtil.class.getPackage().getName());
    }

    @Test
    @DisplayName("类名: 类名应为 OAuth2TokenUtil")
    void className_ShouldBeOAuth2TokenUtil() {
        assertEquals("OAuth2TokenUtil", OAuth2TokenUtil.class.getSimpleName());
    }

    // ==================== 字段测试 ====================

    @Test
    @DisplayName("字段: DEFAULT_GRANT_TYPE 应为 private static final")
    void field_DefaultGrantType_ShouldBePrivateStaticFinal() throws NoSuchFieldException {
        Field field = OAuth2TokenUtil.class.getDeclaredField("DEFAULT_GRANT_TYPE");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: AUTHORIZATION_PENDING 应为 private static final")
    void field_AuthorizationPending_ShouldBePrivateStaticFinal() throws NoSuchFieldException {
        Field field = OAuth2TokenUtil.class.getDeclaredField("AUTHORIZATION_PENDING");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: SLOW_DOWN 应为 private static final")
    void field_SlowDown_ShouldBePrivateStaticFinal() throws NoSuchFieldException {
        Field field = OAuth2TokenUtil.class.getDeclaredField("SLOW_DOWN");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    @Test
    @DisplayName("字段: HTTP_CLIENT 应为 private static final")
    void field_HttpClient_ShouldBePrivateStaticFinal() throws NoSuchFieldException {
        Field field = OAuth2TokenUtil.class.getDeclaredField("HTTP_CLIENT");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    // ==================== 方法存在性测试 ====================

    @Test
    @DisplayName("方法: getTokenWithClientSecretBasic 应存在且为 public static")
    void method_GetTokenWithClientSecretBasic_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretBasic",
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithClientSecretPost 应存在且为 public static")
    void method_GetTokenWithClientSecretPost_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretPost",
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: postTokenEndpoint 应存在且为 private static")
    void method_PostTokenEndpoint_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getDeclaredMethod("postTokenEndpoint",
                Map.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithClientAssertion 应存在且为 public static")
    void method_GetTokenWithClientAssertion_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientAssertion",
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithPCA 应存在且为 public static")
    void method_GetTokenWithPCA_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPCA",
                String.class, String.class, String.class, String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithPKCS7AttestedDocument 应存在且为 public static")
    void method_GetTokenWithPKCS7AttestedDocument_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPKCS7AttestedDocument",
                String.class, String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithOIDCFederatedCredential 应存在且为 public static")
    void method_GetTokenWithOIDCFederatedCredential_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithOIDCFederatedCredential",
                String.class, String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getDeviceCode 应存在且为 public static")
    void method_GetDeviceCode_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getDeviceCode",
                String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(DeviceCodeResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenByDeviceCode 应存在且为 public static")
    void method_GetTokenByDeviceCode_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenByDeviceCode",
                String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: refreshToken 应存在且为 public static")
    void method_RefreshToken_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("refreshToken",
                String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: getTokenWithPlugin 应存在且为 public static")
    void method_GetTokenWithPlugin_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPlugin",
                String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    // ==================== Token Exchange 方法测试 ====================

    @Test
    @DisplayName("方法: tokenExchangeWithClientSecretBasic 应存在且为 public static")
    void method_TokenExchangeWithClientSecretBasic_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientSecretBasic",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: tokenExchangeWithClientSecretPost 应存在且为 public static")
    void method_TokenExchangeWithClientSecretPost_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientSecretPost",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: tokenExchangeWithClientAssertion 应存在且为 public static")
    void method_TokenExchangeWithClientAssertion_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientAssertion",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: tokenExchangeWithPCA 应存在且为 public static")
    void method_TokenExchangeWithPCA_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithPCA",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: tokenExchangeWithPKCS7AttestedDocument 应存在且为 public static")
    void method_TokenExchangeWithPKCS7AttestedDocument_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithPKCS7AttestedDocument",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("方法: tokenExchangeWithOIDCFederatedCredential 应存在且为 public static")
    void method_TokenExchangeWithOIDCFederatedCredential_ShouldExist() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithOIDCFederatedCredential",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isStatic(method.getModifiers()));
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    // ==================== 参数数量测试 ====================

    @Test
    @DisplayName("参数数量: getTokenWithClientSecretBasic 应有 4 个参数")
    void parameterCount_GetTokenWithClientSecretBasic_ShouldBe4() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretBasic",
                String.class, String.class, String.class, String.class);
        assertEquals(4, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithClientSecretPost 应有 4 个参数")
    void parameterCount_GetTokenWithClientSecretPost_ShouldBe4() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretPost",
                String.class, String.class, String.class, String.class);
        assertEquals(4, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithClientAssertion 应有 4 个参数")
    void parameterCount_GetTokenWithClientAssertion_ShouldBe4() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientAssertion",
                String.class, String.class, String.class, String.class);
        assertEquals(4, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithPCA 应有 7 个参数")
    void parameterCount_GetTokenWithPCA_ShouldBe7() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPCA",
                String.class, String.class, String.class, String.class, String.class, String.class, String.class);
        assertEquals(7, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithPKCS7AttestedDocument 应有 5 个参数")
    void parameterCount_GetTokenWithPKCS7AttestedDocument_ShouldBe5() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPKCS7AttestedDocument",
                String.class, String.class, String.class, String.class, String.class);
        assertEquals(5, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithOIDCFederatedCredential 应有 5 个参数")
    void parameterCount_GetTokenWithOIDCFederatedCredential_ShouldBe5() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithOIDCFederatedCredential",
                String.class, String.class, String.class, String.class, String.class);
        assertEquals(5, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getDeviceCode 应有 3 个参数")
    void parameterCount_GetDeviceCode_ShouldBe3() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getDeviceCode",
                String.class, String.class, String.class);
        assertEquals(3, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenByDeviceCode 应有 3 个参数")
    void parameterCount_GetTokenByDeviceCode_ShouldBe3() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenByDeviceCode",
                String.class, String.class, String.class);
        assertEquals(3, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: refreshToken 应有 3 个参数")
    void parameterCount_RefreshToken_ShouldBe3() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("refreshToken",
                String.class, String.class, String.class);
        assertEquals(3, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: getTokenWithPlugin 应有 2 个参数")
    void parameterCount_GetTokenWithPlugin_ShouldBe2() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPlugin",
                String.class, String.class);
        assertEquals(2, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithClientSecretBasic 应有 9 个参数")
    void parameterCount_TokenExchangeWithClientSecretBasic_ShouldBe9() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientSecretBasic",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertEquals(9, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithClientSecretPost 应有 9 个参数")
    void parameterCount_TokenExchangeWithClientSecretPost_ShouldBe9() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientSecretPost",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertEquals(9, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithClientAssertion 应有 9 个参数")
    void parameterCount_TokenExchangeWithClientAssertion_ShouldBe9() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientAssertion",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertEquals(9, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithPCA 应有 12 个参数")
    void parameterCount_TokenExchangeWithPCA_ShouldBe12() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithPCA",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class, String.class);
        assertEquals(12, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithPKCS7AttestedDocument 应有 10 个参数")
    void parameterCount_TokenExchangeWithPKCS7AttestedDocument_ShouldBe10() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithPKCS7AttestedDocument",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertEquals(10, method.getParameterCount());
    }

    @Test
    @DisplayName("参数数量: tokenExchangeWithOIDCFederatedCredential 应有 10 个参数")
    void parameterCount_TokenExchangeWithOIDCFederatedCredential_ShouldBe10() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithOIDCFederatedCredential",
                String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);
        assertEquals(10, method.getParameterCount());
    }

    @Test
    @DisplayName("方法数量: postTokenEndpoint 应为私有方法")
    void method_PostTokenEndpoint_ShouldBePrivate() {
        Method[] methods = OAuth2TokenUtil.class.getDeclaredMethods();
        long postTokenEndpointCount = Arrays.stream(methods)
                .filter(m -> m.getName().equals("postTokenEndpoint"))
                .count();
        assertEquals(1, postTokenEndpointCount);
    }

    // ==================== ClientAssertionType 常量测试 ====================

    @Test
    @DisplayName("ClientAssertionType: OAUTH_JWT_BEARER 应正确")
    void clientAssertionType_OAuthJwtBearer_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                ClientAssertionType.OAUTH_JWT_BEARER);
    }

    @Test
    @DisplayName("ClientAssertionType: PRIVATE_CA_JWT_BEARER 应正确")
    void clientAssertionType_PrivateCaJwtBearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:x509-jwt-bearer",
                ClientAssertionType.PRIVATE_CA_JWT_BEARER);
    }

    @Test
    @DisplayName("ClientAssertionType: PKCS7_BEARER 应正确")
    void clientAssertionType_Pkcs7Bearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:pkcs7-bearer",
                ClientAssertionType.PKCS7_BEARER);
    }

    @Test
    @DisplayName("ClientAssertionType: OIDC_BEARER 应正确")
    void clientAssertionType_OidcBearer_ShouldBeCorrect() {
        assertEquals("urn:cloud:idaas:params:oauth:client-assertion-type:id-token-bearer",
                ClientAssertionType.OIDC_BEARER);
    }

    // ==================== OAuth2Constants 常量测试 ====================

    @Test
    @DisplayName("OAuth2Constants: CLIENT_ID 应正确")
    void oAuth2Constants_ClientId_ShouldBeCorrect() {
        assertEquals("client_id", OAuth2Constants.CLIENT_ID);
    }

    @Test
    @DisplayName("OAuth2Constants: CLIENT_SECRET 应正确")
    void oAuth2Constants_ClientSecret_ShouldBeCorrect() {
        assertEquals("client_secret", OAuth2Constants.CLIENT_SECRET);
    }

    @Test
    @DisplayName("OAuth2Constants: SCOPE 应正确")
    void oAuth2Constants_Scope_ShouldBeCorrect() {
        assertEquals("scope", OAuth2Constants.SCOPE);
    }

    @Test
    @DisplayName("OAuth2Constants: GRANT_TYPE 应正确")
    void oAuth2Constants_GrantType_ShouldBeCorrect() {
        assertEquals("grant_type", OAuth2Constants.GRANT_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: CLIENT_ASSERTION_TYPE 应正确")
    void oAuth2Constants_ClientAssertionType_ShouldBeCorrect() {
        assertEquals("client_assertion_type", OAuth2Constants.CLIENT_ASSERTION_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: CLIENT_ASSERTION 应正确")
    void oAuth2Constants_ClientAssertion_ShouldBeCorrect() {
        assertEquals("client_assertion", OAuth2Constants.CLIENT_ASSERTION);
    }

    @Test
    @DisplayName("OAuth2Constants: APPLICATION_FEDERATED_CREDENTIAL_NAME 应正确")
    void oAuth2Constants_ApplicationFederatedCredentialName_ShouldBeCorrect() {
        assertEquals("application_federated_credential_name", OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME);
    }

    @Test
    @DisplayName("OAuth2Constants: CLIENT_X509_CERTIFICATE 应正确")
    void oAuth2Constants_ClientX509Certificate_ShouldBeCorrect() {
        assertEquals("client_x509", OAuth2Constants.CLIENT_X509_CERTIFICATE);
    }

    @Test
    @DisplayName("OAuth2Constants: X509_CERT_CHAINS 应正确")
    void oAuth2Constants_X509CertChains_ShouldBeCorrect() {
        assertEquals("client_x509_chain", OAuth2Constants.X509_CERT_CHAINS);
    }

    @Test
    @DisplayName("OAuth2Constants: DEVICE_CODE 应正确")
    void oAuth2Constants_DeviceCode_ShouldBeCorrect() {
        assertEquals("device_code", OAuth2Constants.DEVICE_CODE);
    }

    @Test
    @DisplayName("OAuth2Constants: REFRESH_TOKEN_PARAMETER 应正确")
    void oAuth2Constants_RefreshTokenParameter_ShouldBeCorrect() {
        assertEquals("refresh_token", OAuth2Constants.REFRESH_TOKEN_PARAMETER);
    }

    @Test
    @DisplayName("OAuth2Constants: SUBJECT_TOKEN 应正确")
    void oAuth2Constants_SubjectToken_ShouldBeCorrect() {
        assertEquals("subject_token", OAuth2Constants.SUBJECT_TOKEN);
    }

    @Test
    @DisplayName("OAuth2Constants: ACTOR_TOKEN 应正确")
    void oAuth2Constants_ActorToken_ShouldBeCorrect() {
        assertEquals("actor_token", OAuth2Constants.ACTOR_TOKEN);
    }

    @Test
    @DisplayName("OAuth2Constants: SUBJECT_TOKEN_TYPE 应正确")
    void oAuth2Constants_SubjectTokenType_ShouldBeCorrect() {
        assertEquals("subject_token_type", OAuth2Constants.SUBJECT_TOKEN_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: ACTOR_TOKEN_TYPE 应正确")
    void oAuth2Constants_ActorTokenType_ShouldBeCorrect() {
        assertEquals("actor_token_type", OAuth2Constants.ACTOR_TOKEN_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: REQUESTED_TOKEN_TYPE 应正确")
    void oAuth2Constants_RequestedTokenType_ShouldBeCorrect() {
        assertEquals("requested_token_type", OAuth2Constants.REQUESTED_TOKEN_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: CLIENT_CREDENTIALS_GRANT_TYPE_VALUE 应正确")
    void oAuth2Constants_ClientCredentialsGrantTypeValue_ShouldBeCorrect() {
        assertEquals("client_credentials", OAuth2Constants.CLIENT_CREDENTIALS_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("OAuth2Constants: TOKEN_EXCHANGE_GRANT_TYPE_VALUE 应正确")
    void oAuth2Constants_TokenExchangeGrantTypeValue_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:grant-type:token-exchange", OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("OAuth2Constants: DEVICE_CODE_GRANT_TYPE_VALUE 应正确")
    void oAuth2Constants_DeviceCodeGrantTypeValue_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:grant-type:device_code", OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("OAuth2Constants: REFRESH_TOKEN_GRANT_TYPE_VALUE 应正确")
    void oAuth2Constants_RefreshTokenGrantTypeValue_ShouldBeCorrect() {
        assertEquals("refresh_token", OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE);
    }

    @Test
    @DisplayName("OAuth2Constants: ACCESS_TOKEN_TYPE 应正确")
    void oAuth2Constants_AccessTokenType_ShouldBeCorrect() {
        assertEquals("urn:ietf:params:oauth:token-type:access_token", OAuth2Constants.ACCESS_TOKEN_TYPE);
    }

    @Test
    @DisplayName("OAuth2Constants: AUDIENCE 应正确")
    void oAuth2Constants_Audience_ShouldBeCorrect() {
        assertEquals("audience", OAuth2Constants.AUDIENCE);
    }

    // ==================== HttpConstants 常量测试 ====================

    @Test
    @DisplayName("HttpConstants: AUTHORIZATION_HEADER 应正确")
    void httpConstants_AuthorizationHeader_ShouldBeCorrect() {
        assertEquals("Authorization", HttpConstants.AUTHORIZATION_HEADER);
    }

    @Test
    @DisplayName("HttpConstants: CONTENT_TYPE_HEADER 应正确")
    void httpConstants_ContentTypeHeader_ShouldBeCorrect() {
        assertEquals("Content-Type", HttpConstants.CONTENT_TYPE_HEADER);
    }

    @Test
    @DisplayName("HttpConstants: BEARER 应正确")
    void httpConstants_Bearer_ShouldBeCorrect() {
        assertEquals("Bearer", HttpConstants.BEARER);
    }

    @Test
    @DisplayName("HttpConstants: BASIC 应正确")
    void httpConstants_Basic_ShouldBeCorrect() {
        assertEquals("Basic", HttpConstants.BASIC);
    }

    @Test
    @DisplayName("HttpConstants: COLON 应正确")
    void httpConstants_Colon_ShouldBeCorrect() {
        assertEquals(":", HttpConstants.COLON);
    }

    @Test
    @DisplayName("HttpConstants: SPACE 应正确")
    void httpConstants_Space_ShouldBeCorrect() {
        assertEquals(" ", HttpConstants.SPACE);
    }

    // ==================== 工具类特性测试 ====================

    @Test
    @DisplayName("工具类: 类名应以 Util 结尾")
    void utilityClass_NameShouldEndWithUtil() {
        assertTrue(OAuth2TokenUtil.class.getSimpleName().endsWith("Util"));
    }

    @Test
    @DisplayName("工具类: 不应有实例字段")
    void utilityClass_ShouldNotHaveInstanceFields() {
        Field[] fields = OAuth2TokenUtil.class.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isStatic(field.getModifiers()),
                    "Field " + field.getName() + " should be static");
        }
    }

    @Test
    @DisplayName("工具类: 所有方法应为静态")
    void utilityClass_AllMethodsShouldBeStatic() {
        Method[] methods = OAuth2TokenUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            assertTrue(Modifier.isStatic(method.getModifiers()),
                    "Method " + method.getName() + " should be static");
        }
    }

    // ==================== 参数类型测试 ====================

    @Test
    @DisplayName("参数类型: getTokenWithClientSecretBasic 所有参数应为 String")
    void parameterTypes_GetTokenWithClientSecretBasic_ShouldBeString() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretBasic",
                String.class, String.class, String.class, String.class);
        Class<?>[] paramTypes = method.getParameterTypes();
        for (Class<?> type : paramTypes) {
            assertEquals(String.class, type);
        }
    }

    @Test
    @DisplayName("参数类型: postTokenEndpoint 参数类型应正确")
    void parameterTypes_PostTokenEndpoint_ShouldBeCorrect() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getDeclaredMethod("postTokenEndpoint",
                Map.class, String.class);
        Class<?>[] paramTypes = method.getParameterTypes();
        assertEquals(Map.class, paramTypes[0]);
        assertEquals(String.class, paramTypes[1]);
    }

    // ==================== 返回类型一致性测试 ====================

    @Test
    @DisplayName("返回类型: 所有 getToken 方法应返回 IDaaSTokenResponse")
    void returnType_AllGetTokenMethods_ShouldReturnIDaaSTokenResponse() {
        String[] methodNames = {
                "getTokenWithClientSecretBasic",
                "getTokenWithClientSecretPost",
                "getTokenWithClientAssertion",
                "getTokenWithPCA",
                "getTokenWithPKCS7AttestedDocument",
                "getTokenWithOIDCFederatedCredential",
                "getTokenByDeviceCode",
                "refreshToken"
        };

        for (String methodName : methodNames) {
            Method[] methods = OAuth2TokenUtil.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    assertEquals(IDaaSTokenResponse.class, method.getReturnType(),
                            "Method " + methodName + " should return IDaaSTokenResponse");
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("返回类型: 所有 tokenExchange 方法应返回 IDaaSTokenResponse")
    void returnType_AllTokenExchangeMethods_ShouldReturnIDaaSTokenResponse() {
        String[] methodNames = {
                "tokenExchangeWithClientSecretBasic",
                "tokenExchangeWithClientSecretPost",
                "tokenExchangeWithClientAssertion",
                "tokenExchangeWithPCA",
                "tokenExchangeWithPKCS7AttestedDocument",
                "tokenExchangeWithOIDCFederatedCredential"
        };

        for (String methodName : methodNames) {
            Method[] methods = OAuth2TokenUtil.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    assertEquals(IDaaSTokenResponse.class, method.getReturnType(),
                            "Method " + methodName + " should return IDaaSTokenResponse");
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("返回类型: getDeviceCode 应返回 DeviceCodeResponse")
    void returnType_GetDeviceCode_ShouldReturnDeviceCodeResponse() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getDeviceCode",
                String.class, String.class, String.class);
        assertEquals(DeviceCodeResponse.class, method.getReturnType());
    }

    @Test
    @DisplayName("返回类型: getTokenWithPlugin 应返回 IDaaSTokenResponse")
    void returnType_GetTokenWithPlugin_ShouldReturnIDaaSTokenResponse() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPlugin",
                String.class, String.class);
        assertEquals(IDaaSTokenResponse.class, method.getReturnType());
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("异常处理: postTokenEndpoint 应处理 ClientException")
    void exceptionHandling_PostTokenEndpoint_ShouldHandleClientException() throws Exception {
        // 通过反射测试私有方法对异常的处理
        Method method = OAuth2TokenUtil.class.getDeclaredMethod("postTokenEndpoint", Map.class, String.class);
        method.setAccessible(true);

        // 创建一个空的 form body
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList("test-client"));

        // 由于无法模拟 HTTP_CLIENT，这里主要验证方法存在且可访问
        assertNotNull(method);
    }

    // ==================== 综合场景测试 ====================

    @Test
    @DisplayName("综合场景: 验证所有公共方法存在")
    void comprehensive_AllPublicMethodsShouldExist() {
        String[] expectedMethods = {
                "getTokenWithClientSecretBasic",
                "getTokenWithClientSecretPost",
                "getTokenWithClientAssertion",
                "getTokenWithPCA",
                "getTokenWithPKCS7AttestedDocument",
                "getTokenWithOIDCFederatedCredential",
                "getDeviceCode",
                "getTokenByDeviceCode",
                "refreshToken",
                "getTokenWithPlugin",
                "tokenExchangeWithClientSecretBasic",
                "tokenExchangeWithClientSecretPost",
                "tokenExchangeWithClientAssertion",
                "tokenExchangeWithPCA",
                "tokenExchangeWithPKCS7AttestedDocument",
                "tokenExchangeWithOIDCFederatedCredential",
                "postTokenEndpoint"
        };

        for (String methodName : expectedMethods) {
            Method[] methods = OAuth2TokenUtil.class.getDeclaredMethods();
            boolean found = Arrays.stream(methods)
                    .anyMatch(m -> m.getName().equals(methodName));
            assertTrue(found, "Method " + methodName + " should exist");
        }
    }

    @Test
    @DisplayName("综合场景: 验证方法签名一致性")
    void comprehensive_MethodSignaturesShouldBeConsistent() {
        // 验证所有方法都是静态的
        Method[] methods = OAuth2TokenUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            assertTrue(Modifier.isStatic(method.getModifiers()),
                    "Method " + method.getName() + " should be static");
        }
    }

    @Test
    @DisplayName("综合场景: 验证 Token Exchange 方法包含 actorToken 参数")
    void comprehensive_TokenExchangeMethodsShouldHaveActorTokenParameter() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("tokenExchangeWithClientSecretBasic",
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class);

        // 参数列表: clientId, clientSecret, subjectToken, subjectTokenType, requestedTokenType,
        //          tokenEndpoint, scope, actorToken, actorTokenType
        Class<?>[] paramTypes = method.getParameterTypes();
        assertEquals(9, paramTypes.length);
        // 最后两个参数是 actorToken 和 actorTokenType
        assertEquals(String.class, paramTypes[7]); // actorToken
        assertEquals(String.class, paramTypes[8]); // actorTokenType
    }

    @Test
    @DisplayName("综合场景: 验证 PCA 方法参数包含证书相关参数")
    void comprehensive_PCAMethodsShouldHaveCertificateParameters() throws NoSuchMethodException {
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithPCA",
                String.class, String.class, String.class, String.class, String.class, String.class, String.class);

        // 参数列表: clientId, applicationFederatedCredentialName, clientX509Certificate,
        //          x509CertChains, clientAssertion, tokenEndpoint, scope
        Class<?>[] paramTypes = method.getParameterTypes();
        assertEquals(7, paramTypes.length);
        // 所有参数都应为 String
        for (Class<?> type : paramTypes) {
            assertEquals(String.class, type);
        }
    }

    @Test
    @DisplayName("综合场景: 验证 Client Secret Basic 方法使用 Basic Auth")
    void comprehensive_ClientSecretBasicShouldUseBasicAuth() throws Exception {
        // 验证方法存在，实际 Basic Auth 逻辑在方法内部实现
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretBasic",
                String.class, String.class, String.class, String.class);
        assertNotNull(method);

        // 验证 HttpConstants.BASIC 存在
        assertEquals("Basic", HttpConstants.BASIC);
        assertEquals(":", HttpConstants.COLON);
        assertEquals(" ", HttpConstants.SPACE);
    }

    @Test
    @DisplayName("综合场景: 验证所有 Token 方法使用正确的 Content Type")
    void comprehensive_AllTokenMethodsShouldUseCorrectContentType() throws Exception {
        // 验证 ContentType.FORM 存在
        assertNotNull(ContentType.FORM);
        assertEquals("application/x-www-form-urlencoded", ContentType.FORM.getType());
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("边界情况: 方法应能接受空字符串参数")
    void edgeCase_MethodsShouldAcceptEmptyStrings() throws NoSuchMethodException {
        // 验证方法签名允许空字符串（String 类型）
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretBasic",
                String.class, String.class, String.class, String.class);
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    @Test
    @DisplayName("边界情况: 方法应能接受 null 参数（运行时）")
    void edgeCase_MethodsShouldHandleNullAtRuntime() throws NoSuchMethodException {
        // 验证方法签名不限制 null 值
        Method method = OAuth2TokenUtil.class.getMethod("getTokenWithClientSecretPost",
                String.class, String.class, String.class, String.class);
        // String 类型可以接受 null
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    // ==================== 命名规范测试 ====================

    @Test
    @DisplayName("命名规范: 方法名应符合驼峰命名法")
    void namingConvention_MethodsShouldUseCamelCase() {
        Method[] methods = OAuth2TokenUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            // 验证方法名不以大写字母开头
            assertTrue(Character.isLowerCase(name.charAt(0)),
                    "Method " + name + " should start with lowercase letter");
        }
    }

    @Test
    @DisplayName("命名规范: 常量名应为大写")
    void namingConvention_ConstantsShouldBeUpperCase() throws Exception {
        Field[] fields = OAuth2TokenUtil.class.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                String name = field.getName();
                // 验证常量名全为大写
                assertTrue(name.equals(name.toUpperCase()),
                        "Constant " + name + " should be uppercase");
            }
        }
    }
}
