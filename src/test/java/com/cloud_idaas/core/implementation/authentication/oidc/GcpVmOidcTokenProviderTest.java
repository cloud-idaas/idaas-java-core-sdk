package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GcpVmOidcTokenProviderTest {

    @BeforeAll
    static void initializeFactory() {
        IDaaSCredentialProviderFactory.init(createValidTestConfig());
    }

    @Test void defaultHttpErrorsDoNotExposeMetadataBodyInPublicException() throws Exception {
        for (int variant = 0; variant < 4; variant++) {
            final int status = variant % 2 == 0 ? 403 : 503;
            final boolean json = variant >= 2;
            String marker = "synthetic-sensitive-metadata-error-" + status;
            String body = json ? "{\"error\":\"" + marker + "\",\"error_description\":\"" + marker + "\"}" : marker;
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/identity", exchange -> {
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, bytes.length);
                exchange.getResponseBody().write(bytes); exchange.close();
            });
            server.start();
            try {
                GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder().audience("https://test.invalid")
                        .metadataIdentityUrlTemplate("http://127.0.0.1:" + server.getAddress().getPort() + "/identity?audience=%s").build();
                CredentialException failure = assertThrows(CredentialException.class, provider::refreshCredential);
                assertEquals("GcpMetadataRequestFailed", failure.getErrorCode());
                assertTrue(failure.getMessage().contains(Integer.toString(status)));
                org.junit.jupiter.api.Assertions.assertNull(failure.getCause());
                java.io.StringWriter trace = new java.io.StringWriter();
                failure.printStackTrace(new java.io.PrintWriter(trace));
                assertFalse(trace.toString().contains(body));
            } finally { server.stop(0); }
        }
    }

    @Test
    @DisplayName("Builder: 应设置 audience 并保留默认 Metadata URL")
    void builder_ShouldSetAudienceAndDefaultMetadataUrl() {
        GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder()
                .audience("idaas-instance-id")
                .build();

        assertEquals("idaas-instance-id", provider.getAudience());
        assertTrue(provider.getMetadataIdentityUrlTemplate().contains("metadata.google.internal"));
        assertTrue(provider.getMetadataIdentityUrlTemplate().contains("audience=%s"));
        assertTrue(provider.getMetadataIdentityUrlTemplate().contains("format=full"));
    }

    @Test
    @DisplayName("Builder: 缺少 audience 应抛出 IllegalArgumentException")
    void builder_WithoutAudience_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GcpVmOidcTokenProvider.builder().build());

        assertEquals("audience cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Builder: 空 Metadata URL 应抛出 IllegalArgumentException")
    void builder_WithEmptyMetadataUrl_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> GcpVmOidcTokenProvider.builder().metadataIdentityUrlTemplate(""));
    }

    @Test
    @DisplayName("类型: 应实现 OidcTokenProvider 并继承刷新凭据 Provider")
    void type_ShouldMatchProviderContracts() {
        GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder()
                .audience("idaas-instance-id")
                .build();

        assertTrue(provider instanceof OidcTokenProvider);
        assertTrue(provider instanceof AbstractRefreshedCredentialProvider);
        assertFalse(provider.isAsyncCredentialUpdateEnabled());
    }

    @Test
    @DisplayName("Metadata: 应携带 Google 请求头、编码 audience 并按 JWT exp 设置缓存时间")
    void refreshCredential_ShouldRequestMetadataIdentityToken() throws Exception {
        long expirationTime = Instant.now().getEpochSecond() + 7200;
        String token = createUnsignedJwt(expirationTime);
        AtomicReference<String> metadataFlavor = new AtomicReference<>();
        AtomicReference<String> rawQuery = new AtomicReference<>();

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/identity", exchange -> {
            metadataFlavor.set(exchange.getRequestHeaders().getFirst(HttpConstants.GCP_METADATA_FLAVOR));
            rawQuery.set(exchange.getRequestURI().getRawQuery());
            byte[] response = token.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        try {
            String urlTemplate = "http://127.0.0.1:" + server.getAddress().getPort()
                    + "/identity?audience=%s&format=full";
            GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder()
                    .audience("https://idaas.example.com/instance?id=1")
                    .metadataIdentityUrlTemplate(urlTemplate)
                    .build();

            RefreshResult<String> result = provider.refreshCredential();

            assertEquals(token, result.getValue());
            assertEquals(HttpConstants.GCP_METADATA_FLAVOR_VALUE, metadataFlavor.get());
            assertEquals("audience=https%3A%2F%2Fidaas.example.com%2Finstance%3Fid%3D1&format=full",
                    rawQuery.get());
            assertEquals(Instant.ofEpochSecond(expirationTime - 600), result.getStaleTime());
            assertEquals(Instant.ofEpochSecond(expirationTime - 1200), result.getPrefetchTime());
            java.lang.reflect.Field expiry = RefreshResult.class.getDeclaredField("expiresAt");
            expiry.setAccessible(true);
            assertEquals(Instant.ofEpochSecond(expirationTime), expiry.get(result));
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("缓存: getOidcToken 应缓存 Metadata Server 返回的 token")
    void getOidcToken_ShouldCacheMetadataToken() throws IOException {
        long expirationTime = Instant.now().getEpochSecond() + 3600;
        String token = createUnsignedJwt(expirationTime);
        AtomicInteger requestCount = new AtomicInteger();

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/identity", exchange -> {
            requestCount.incrementAndGet();
            byte[] response = token.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        try {
            GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder()
                    .audience("idaas-instance-id")
                    .metadataIdentityUrlTemplate("http://127.0.0.1:" + server.getAddress().getPort()
                            + "/identity?audience=%s&format=full")
                    .build();

            assertEquals(token, provider.getOidcToken());
            assertEquals(token, provider.getOidcToken());
            assertEquals(1, requestCount.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shortLivedMetadataTokenRemainsCachedUntilItsRefreshWindow() throws IOException {
        long expirationTime = Instant.now().getEpochSecond() + 30;
        String token = createUnsignedJwt(expirationTime);
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/identity", exchange -> {
            requests.incrementAndGet();
            byte[] response = token.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder().audience("https://test.invalid")
                    .metadataIdentityUrlTemplate("http://127.0.0.1:" + server.getAddress().getPort()
                            + "/identity?audience=%s&format=full").build();
            assertEquals(token, provider.getOidcToken());
            assertEquals(token, provider.getOidcToken());
            assertEquals(1, requests.get());
        } finally {
            server.stop(0);
        }
    }

    @Test void malformedMetadataBodyIsNotExposedInExceptionOrStackTrace() throws Exception {
        String body = "synthetic-sensitive-metadata-token";
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/identity", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try {
            GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder().audience("https://test.invalid")
                    .metadataIdentityUrlTemplate("http://127.0.0.1:" + server.getAddress().getPort()
                            + "/identity?audience=%s").build();
            CredentialException failure = assertThrows(CredentialException.class, provider::refreshCredential);
            assertEquals("InvalidGcpIdentityToken", failure.getErrorCode());
            org.junit.jupiter.api.Assertions.assertNull(failure.getCause());
            java.io.StringWriter trace = new java.io.StringWriter();
            failure.printStackTrace(new java.io.PrintWriter(trace));
            assertFalse(trace.toString().contains(body));
        } finally {
            server.stop(0);
        }
    }

    private static String createUnsignedJwt(long expirationTime) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String header = encoder.encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}"
                .getBytes(StandardCharsets.UTF_8));
        String payload = encoder.encodeToString(("{\"exp\":" + expirationTime + "}")
                .getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }

    @Test
    void missingAndExpiredExpirationProduceCredentialErrors() throws IOException {
        for (String claims : new String[]{"{}", "{\"exp\":1}"}) {
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String token = encoder.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8))
                    + "." + encoder.encodeToString(claims.getBytes(StandardCharsets.UTF_8)) + ".";
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/identity", exchange -> {
                byte[] response = token.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            });
            server.start();
            try {
                GcpVmOidcTokenProvider provider = GcpVmOidcTokenProvider.builder().audience("https://test.invalid")
                        .metadataIdentityUrlTemplate("http://127.0.0.1:" + server.getAddress().getPort()
                                + "/identity?audience=%s&format=full").build();
                CredentialException failure = assertThrows(CredentialException.class, provider::refreshCredential);
                assertEquals(claims.equals("{}") ? "MissingGcpIdentityTokenExpiration" : "ExpiredGcpIdentityToken",
                        failure.getErrorCode());
            } finally {
                server.stop(0);
            }
        }
    }

    private static IDaaSClientConfig createValidTestConfig() {
        IDaaSClientConfig config = new IDaaSClientConfig();
        config.setIdaasInstanceId("test-instance-id");
        config.setClientId("test-client-id");
        config.setScope("test-audience|test-scope");
        config.setIssuer("https://test.idaas.example.com");
        config.setTokenEndpoint("https://test.idaas.example.com/token");
        config.setDeviceAuthorizationEndpoint("https://test.idaas.example.com/device");

        IdentityAuthenticationConfiguration authnConfig = new IdentityAuthenticationConfiguration();
        authnConfig.setIdentityType(AuthenticationIdentityEnum.CLIENT);
        authnConfig.setAuthnMethod(TokenAuthnMethod.OIDC);
        authnConfig.setApplicationFederatedCredentialName("test-federated-credential");
        authnConfig.setClientDeployEnvironment(ClientDeployEnvironmentEnum.GOOGLE_VM);
        config.setAuthnConfiguration(authnConfig);
        return config;
    }
}
