package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * IDaaSMachineTokenExchangeCredentialProvider 单元测试
 */
class IDaaSMachineTokenExchangeCredentialProviderTest {

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_SCOPE = "openid profile";
    private static final String TEST_TOKEN_ENDPOINT = "https://example.com/token";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_TOKEN_TYPE = "Bearer";
    private static final String TEST_REQUESTED_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder: 使用所有必需参数成功构建")
    void builder_WithAllRequiredParams_ShouldBuildSuccessfully() {
        IDaaSMachineTokenExchangeCredentialProvider provider = IDaaSMachineTokenExchangeCredentialProvider.builder()
                .clientId(TEST_CLIENT_ID)
                .scope(TEST_SCOPE)
                .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                .build();

        assertNotNull(provider);
        assertEquals(TEST_CLIENT_ID, provider.getClientId());
        assertEquals(TEST_SCOPE, provider.getScope());
        assertEquals(TEST_TOKEN_ENDPOINT, provider.getTokenEndpoint());
        assertEquals(TokenAuthnMethod.CLIENT_SECRET_POST, provider.getAuthnMethod());
    }

    @Test
    @DisplayName("Builder: 缺少 clientId 应抛出异常")
    void builder_WithBlankClientId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                IDaaSMachineTokenExchangeCredentialProvider.builder()
                        .clientId("")
                        .scope(TEST_SCOPE)
                        .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                        .build()
        );
        assertEquals("clientId is blank", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 缺少 scope 应抛出异常")
    void builder_WithBlankScope_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                IDaaSMachineTokenExchangeCredentialProvider.builder()
                        .clientId(TEST_CLIENT_ID)
                        .scope("")
                        .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                        .build()
        );
        assertEquals("scope is blank", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 缺少 tokenEndpoint 应抛出异常")
    void builder_WithBlankTokenEndpoint_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                IDaaSMachineTokenExchangeCredentialProvider.builder()
                        .clientId(TEST_CLIENT_ID)
                        .scope(TEST_SCOPE)
                        .tokenEndpoint("")
                        .build()
        );
        assertEquals("tokenEndpoint is blank", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 使用 null clientId 应抛出异常")
    void builder_WithNullClientId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                IDaaSMachineTokenExchangeCredentialProvider.builder()
                        .clientId(null)
                        .scope(TEST_SCOPE)
                        .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                        .build()
        );
        assertEquals("clientId is blank", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 使用自定义认证方法构建")
    void builder_WithCustomAuthnMethod_ShouldSetAuthnMethod() {
        IDaaSMachineTokenExchangeCredentialProvider provider = IDaaSMachineTokenExchangeCredentialProvider.builder()
                .clientId(TEST_CLIENT_ID)
                .scope(TEST_SCOPE)
                .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                .authnMethod(TokenAuthnMethod.CLIENT_SECRET_BASIC)
                .build();

        assertEquals(TokenAuthnMethod.CLIENT_SECRET_BASIC, provider.getAuthnMethod());
    }

    @Test
    @DisplayName("Builder: 使用所有可选参数构建")
    void builder_WithAllOptionalParams_ShouldBuildSuccessfully() {
        Supplier<String> clientSecretSupplier = () -> "secret";
        JwtClientAssertionProvider assertionProvider = mock(JwtClientAssertionProvider.class);
        Pkcs7AttestedDocumentProvider attestedDocumentProvider = mock(Pkcs7AttestedDocumentProvider.class);
        OidcTokenProvider oidcTokenProvider = mock(OidcTokenProvider.class);

        IDaaSMachineTokenExchangeCredentialProvider provider = IDaaSMachineTokenExchangeCredentialProvider.builder()
                .clientId(TEST_CLIENT_ID)
                .scope(TEST_SCOPE)
                .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                .authnMethod(TokenAuthnMethod.PRIVATE_KEY_JWT)
                .clientSecretSupplier(clientSecretSupplier)
                .clientAssertionProvider(assertionProvider)
                .applicationFederatedCredentialName("fed-cred-name")
                .attestedDocumentProvider(attestedDocumentProvider)
                .oidcTokenProvider(oidcTokenProvider)
                .clientX509Certificate("cert")
                .x509CertChains("cert-chain")
                .pluginName("test-plugin")
                .build();

        assertNotNull(provider);
        assertEquals(TokenAuthnMethod.PRIVATE_KEY_JWT, provider.getAuthnMethod());
        assertEquals(clientSecretSupplier, provider.getClientSecretSupplier());
        assertEquals(assertionProvider, provider.getClientAssertionProvider());
        assertEquals("fed-cred-name", provider.getApplicationFederatedCredentialName());
        assertEquals(attestedDocumentProvider, provider.getAttestedDocumentProvider());
        assertEquals(oidcTokenProvider, provider.getOidcTokenProvider());
        assertEquals("cert", provider.getClientX509Certificate());
        assertEquals("cert-chain", provider.getX509CertChains());
        assertEquals("test-plugin", provider.getPluginName());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Getter/Setter: authnMethod 应正确设置和获取")
    void authnMethodGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        provider.setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_BASIC);

        assertEquals(TokenAuthnMethod.CLIENT_SECRET_BASIC, provider.getAuthnMethod());
    }

    @Test
    @DisplayName("Getter/Setter: clientSecretSupplier 应正确设置和获取")
    void clientSecretSupplierGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();
        Supplier<String> supplier = () -> "test-secret";

        provider.setClientSecretSupplier(supplier);

        assertEquals(supplier, provider.getClientSecretSupplier());
    }

    @Test
    @DisplayName("Getter/Setter: clientAssertionProvider 应正确设置和获取")
    void clientAssertionProviderGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();
        JwtClientAssertionProvider assertionProvider = mock(JwtClientAssertionProvider.class);

        provider.setClientAssertionProvider(assertionProvider);

        assertEquals(assertionProvider, provider.getClientAssertionProvider());
    }

    @Test
    @DisplayName("Getter/Setter: applicationFederatedCredentialName 应正确设置和获取")
    void applicationFederatedCredentialNameGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        provider.setApplicationFederatedCredentialName("new-fed-name");

        assertEquals("new-fed-name", provider.getApplicationFederatedCredentialName());
    }

    @Test
    @DisplayName("Getter/Setter: attestedDocumentProvider 应正确设置和获取")
    void attestedDocumentProviderGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();
        Pkcs7AttestedDocumentProvider documentProvider = mock(Pkcs7AttestedDocumentProvider.class);

        provider.setAttestedDocumentProvider(documentProvider);

        assertEquals(documentProvider, provider.getAttestedDocumentProvider());
    }

    @Test
    @DisplayName("Getter/Setter: oidcTokenProvider 应正确设置和获取")
    void oidcTokenProviderGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();
        OidcTokenProvider tokenProvider = mock(OidcTokenProvider.class);

        provider.setOidcTokenProvider(tokenProvider);

        assertEquals(tokenProvider, provider.getOidcTokenProvider());
    }

    @Test
    @DisplayName("Getter/Setter: clientX509Certificate 应正确设置和获取")
    void clientX509CertificateGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        provider.setClientX509Certificate("new-cert");

        assertEquals("new-cert", provider.getClientX509Certificate());
    }

    @Test
    @DisplayName("Getter/Setter: x509CertChains 应正确设置和获取")
    void x509CertChainsGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        provider.setX509CertChains("new-cert-chain");

        assertEquals("new-cert-chain", provider.getX509CertChains());
    }

    @Test
    @DisplayName("Getter/Setter: pluginName 应正确设置和获取")
    void pluginNameGetterSetter_ShouldWorkCorrectly() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        provider.setPluginName("new-plugin");

        assertEquals("new-plugin", provider.getPluginName());
    }

    // ==================== getCredential 测试 ====================

    @Test
    @DisplayName("getCredential: 使用 actor token 时参数为空应抛出异常")
    void getCredential_WithActorToken_BlankParams_ShouldThrowException() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                provider.getCredential(TEST_ACCESS_TOKEN, TEST_TOKEN_TYPE, TEST_REQUESTED_TOKEN_TYPE, TEST_SCOPE, "", "type")
        );
        assertEquals("actor Token or actor Token Type is blank", exception.getMessage());
    }

    @Test
    @DisplayName("getCredential: 使用 actor token 时 token type 为空应抛出异常")
    void getCredential_WithActorToken_BlankTokenType_ShouldThrowException() {
        IDaaSMachineTokenExchangeCredentialProvider provider = createBasicProvider();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                provider.getCredential(TEST_ACCESS_TOKEN, TEST_TOKEN_TYPE, TEST_REQUESTED_TOKEN_TYPE, TEST_SCOPE, "token", "")
        );
        assertEquals("actor Token or actor Token Type is blank", exception.getMessage());
    }

    // ==================== 辅助方法 ====================

    private IDaaSMachineTokenExchangeCredentialProvider createBasicProvider() {
        return IDaaSMachineTokenExchangeCredentialProvider.builder()
                .clientId(TEST_CLIENT_ID)
                .scope(TEST_SCOPE)
                .tokenEndpoint(TEST_TOKEN_ENDPOINT)
                .build();
    }
}
