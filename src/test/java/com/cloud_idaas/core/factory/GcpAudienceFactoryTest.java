package com.cloud_idaas.core.factory;

import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.http.HttpClient;
import com.cloud_idaas.core.http.HttpClientFactory;
import com.cloud_idaas.core.http.HttpResponse;
import com.cloud_idaas.core.implementation.IDaaSMachineCredentialProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GcpAudienceFactoryTest {
    private Object previousClient;
    private Field clientField;
    private Field configField;
    private Object previousConfig;

    @BeforeEach
    void mockMetadataAndTokenEndpoint() throws Exception {
        clientField = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
        clientField.setAccessible(true);
        previousClient = clientField.get(null);
        configField = HttpClientFactory.class.getDeclaredField("httpConfiguration");
        configField.setAccessible(true);
        previousConfig = configField.get(null);
        configField.set(null, new HttpConfiguration());
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String jwt = encoder.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + encoder.encodeToString(("{\"exp\":" + (System.currentTimeMillis() / 1000 + 3600) + "}")
                .getBytes(StandardCharsets.UTF_8)) + ".";
        clientField.set(null, (HttpClient) request -> request.getUrl().contains("metadata.google.internal")
                ? new HttpResponse(200, jwt)
                : new HttpResponse(200, "{\"access_token\":\"test-token\",\"expires_in\":3600}"));
    }

    @AfterEach
    void reset() throws Exception {
        IDaaSCredentialProviderFactory.reset();
        clientField.set(null, previousClient);
        configField.set(null, previousConfig);
    }

    @ParameterizedTest
    @CsvSource({
            "https://idaas.example.com/api/v2/system/oauth2, https://idaas.example.com",
            "https://idaas.example.com:8443/path?query=value#fragment, https://idaas.example.com:8443",
            "https://idaas.example.com/, https://idaas.example.com",
            "https://[::1]:8443/path, https://[::1]:8443"
    })
    void machineAndExchangeUseIssuerOrigin(String issuer, String expected) {
        IDaaSCredentialProviderFactory.reset();
        IDaaSCredentialProviderFactory.init(config(issuer));
        IDaaSMachineCredentialProvider machine = (IDaaSMachineCredentialProvider)
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
        assertEquals(expected, ((GcpVmOidcTokenProvider) machine.getOidcTokenProvider()).getAudience());
        assertEquals(expected, ((GcpVmOidcTokenProvider) IDaaSCredentialProviderFactory
                .getIDaaSTokenExchangeCredentialProvider().getOidcTokenProvider()).getAudience());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/relative/path", "https:///missing-host", "https://bad host/path"})
    void invalidIssuerFailsBeforeRequestingMetadata(String issuer) {
        IDaaSCredentialProviderFactory.reset();
        IDaaSCredentialProviderFactory.init(config(issuer));
        assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getIDaaSCredentialProvider);
        assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getIDaaSTokenExchangeCredentialProvider);
        if (issuer.equals("https:///missing-host")) {
            ConfigException failure = assertThrows(ConfigException.class,
                    IDaaSCredentialProviderFactory::getIDaaSTokenExchangeCredentialProvider);
            assertTrue(failure.getCause() instanceof java.net.URISyntaxException);
            assertEquals(issuer, ((java.net.URISyntaxException) failure.getCause()).getInput());
        }
        IDaaSCredentialProviderFactory.reset();
        IDaaSCredentialProviderFactory.init(config("https://valid.example.com/path"));
        assertNotNull(IDaaSCredentialProviderFactory.getIDaaSCredentialProvider());
    }

    private IDaaSClientConfig config(String issuer) {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIdaasInstanceId("idaas_test");
        config.setClientId("app_test");
        config.setIssuer(issuer);
        config.setTokenEndpoint("https://idaas.example.com/token");
        IdentityAuthenticationConfiguration authn = new IdentityAuthenticationConfiguration();
        authn.setAuthnMethod(TokenAuthnMethod.OIDC);
        authn.setClientDeployEnvironment(ClientDeployEnvironmentEnum.GOOGLE_VM);
        authn.setApplicationFederatedCredentialName("gcp-test");
        config.setAuthnConfiguration(authn);
        return config;
    }
}
