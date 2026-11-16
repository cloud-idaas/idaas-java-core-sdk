package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.http.*;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/** Regression coverage for wrapped EC2 CMS assertions and RSA-2048 selection. */
class AwsEc2IdentityDocumentRegressionTest {
    private Object previousClient;
    private Object previousConfig;
    private static final byte[] CONTENT = new byte[512];
    private static final String BASE64 = Base64.getEncoder().encodeToString(CONTENT);

    @BeforeEach void before() throws Exception {
        previousClient = field("singletonOKHttpClient").get(null);
        previousConfig = field("httpConfiguration").get(null);
        IDaaSCredentialProviderFactory.reset();
        IDaaSCredentialProviderFactory.init(config(false));
        field("httpConfiguration").set(null, HttpConfiguration.copy(IDaaSCredentialProviderFactory.getHttpConfig()));
    }

    @AfterEach void after() throws Exception {
        IDaaSCredentialProviderFactory.reset();
        field("singletonOKHttpClient").set(null, previousClient);
        field("httpConfiguration").set(null, previousConfig);
    }

    @Test void lineFeedWrappedMetadataProducesStrictlyDecodableAssertion() throws Exception {
        checkWrapped("\n");
    }

    @Test void crlfWrappedMetadataProducesUnchangedBinaryContent() throws Exception {
        checkWrapped("\r\n");
    }

    @Test void singleLineMetadataRemainsUnchanged() throws Exception {
        install(BASE64, null);
        assertEquals(BASE64, new AwsEc2Pkcs7AttestedDocumentProvider().getAttestedDocument());
    }

    @Test void sessionTokenIsTrimmedBeforeUseInMetadataHeader() throws Exception {
        install(BASE64, null, " \tsynthetic-session\r\n");
        assertEquals(BASE64, new AwsEc2Pkcs7AttestedDocumentProvider().getAttestedDocument());
    }

    @Test void missingSessionTokenIsRejectedBeforeFetchingDocument() throws Exception {
        for (String token : new String[]{null, "", " \r\n\t "}) {
            install(BASE64, null, token);
            CredentialException failure = assertThrows(CredentialException.class,
                    () -> AwsEc2Pkcs7AttestedDocumentProvider.builder().build().refreshCredential());
            assertEquals("EmptyEc2MetadataToken", failure.getErrorCode());
        }
    }

    @Test void emptyOrWhitespaceMetadataIsRejectedBeforeAuthentication() throws Exception {
        for (String body : new String[]{null, "", " \r\n\t "}) {
            install(body, null);
            assertThrows(CredentialException.class,
                    () -> AwsEc2Pkcs7AttestedDocumentProvider.builder().build().refreshCredential());
        }
    }

    @Test void machineAndExchangeSubmitNormalizedRsa2048Assertion() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        install(Base64.getMimeEncoder(76, new byte[]{'\n'}).encodeToString(CONTENT), calls);
        IDaaSCredentialProviderFactory.reset();
        IDaaSCredentialProviderFactory.init(config(true));
        assertEquals("issued", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
        assertEquals("issued", IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider()
                .getCredential("subject", "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken());
        assertEquals(2, calls.get());
    }

    private void checkWrapped(String separator) throws Exception {
        String wrapped = " \t" + Base64.getMimeEncoder(76, separator.getBytes(StandardCharsets.US_ASCII))
                .encodeToString(CONTENT) + "\r\n";
        assertThrows(IllegalArgumentException.class, () -> Base64.getDecoder().decode(wrapped));
        install(wrapped, null);
        String assertion = new AwsEc2Pkcs7AttestedDocumentProvider().getAttestedDocument();
        assertEquals(BASE64, assertion);
        assertArrayEquals(CONTENT, Base64.getDecoder().decode(assertion));
    }

    private void install(String body, AtomicInteger oauthCalls) throws Exception {
        install(body, oauthCalls, "synthetic-session");
    }

    private void install(String body, AtomicInteger oauthCalls, String sessionBody) throws Exception {
        AtomicReference<String> session = new AtomicReference<>();
        field("singletonOKHttpClient").set(null, (HttpClient) request -> {
            if (request.getUrl().endsWith("/latest/api/token")) {
                assertEquals(HttpMethod.PUT, request.getMethod());
                assertEquals("21600", request.getHeaders().get("X-aws-ec2-metadata-token-ttl-seconds").get(0));
                session.set(sessionBody == null ? null : sessionBody.trim());
                return new HttpResponse(200, sessionBody);
            }
            if (request.getUrl().equals("https://test.invalid/token")) {
                assertNotNull(oauthCalls);
                assertEquals(BASE64, request.getFormBody().get("client_assertion").get(0));
                oauthCalls.incrementAndGet();
                return new HttpResponse(200, "{\"access_token\":\"issued\",\"expires_in\":3600,\"expires_at\":"
                        + (System.currentTimeMillis() / 1000 + 3600) + "}");
            }
            assertEquals("http://169.254.169.254/latest/dynamic/instance-identity/rsa2048", request.getUrl());
            assertNotNull(session.get());
            assertFalse(session.get().isEmpty(), "Empty session token must fail before fetching the document");
            assertEquals(HttpMethod.GET, request.getMethod());
            assertEquals(session.get(), request.getHeaders().get("X-aws-ec2-metadata-token").get(0));
            return new HttpResponse(200, body);
        });
    }

    private static IDaaSClientConfig config(boolean ec2) {
        IDaaSClientConfig c = new IDaaSClientConfig();
        c.setIdaasInstanceId("test-instance"); c.setClientId("test-client");
        c.setScope("test|read"); c.setIssuer("https://test.invalid"); c.setTokenEndpoint("https://test.invalid/token");
        IdentityAuthenticationConfiguration a = new IdentityAuthenticationConfiguration();
        a.setAuthnMethod(ec2 ? TokenAuthnMethod.PKCS7 : TokenAuthnMethod.NONE);
        a.setClientDeployEnvironment(ClientDeployEnvironmentEnum.AWS_EC2);
        a.setApplicationFederatedCredentialName("ec2-test"); c.setAuthnConfiguration(a);
        return c;
    }

    private static Field field(String name) throws Exception {
        Field f = HttpClientFactory.class.getDeclaredField(name); f.setAccessible(true); return f;
    }
}
