package com.cloud_idaas.core.factory;

import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.domain.constants.ConfigPathConstants;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.http.HttpClient;
import com.cloud_idaas.core.http.HttpClientFactory;
import com.cloud_idaas.core.http.HttpResponse;
import com.cloud_idaas.core.implementation.IDaaSMachineCredentialProvider;
import com.cloud_idaas.core.implementation.IDaaSMachineTokenExchangeCredentialProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.FileOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.HumanFederatedOidcTokenProvider;
import com.cloud_idaas.core.util.JSONUtil;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/** Regression scenarios run in separate JVMs to isolate environment and static HTTP clients. */
class SdkRegressionTest {

    @TempDir
    Path temp;

    @Test void filePluginCanDelegateDifferentScopeToExistingWorker() throws Exception {
        runProbe("plugin-worker-provider");
    }

    @Test void coldPluginWorkerCanUseDefaultHttpAndOAuthHelpers() throws Exception {
        runProbe("plugin-cold-http");
    }

    @Test void failedColdPluginDiscardsCandidateTransportAndCanRetry() throws Exception {
        runProbe("plugin-cold-failure");
    }

    @Test void failedPluginWarmupKeepsOldWorkerAndExchangeTransport() throws Exception {
        runProbe("plugin-old-transport");
    }

    @Test void concurrentInitWaitsForPluginCompletionAndCanRetryFailure() throws Exception {
        runProbe("plugin-init-wait");
    }

    @Test void obsoletePluginWarmupCannotRemoveNewColdCandidateOrCommit() throws Exception {
        runProbe("plugin-cold-superseded");
    }

    @Test void completedCreationIsRecheckedBeforeRegisteringAnotherTask() throws Exception {
        runProbe("completion-gap");
    }

    @Test void pluginWorkerCanReadConfigAndCannotMutatePendingTransport() throws Exception {
        runProbe("plugin-config-worker");
    }

    @Test void pluginCannotMutatePendingTransportDuringWarmup() throws Exception {
        runProbe("plugin-http-mutation");
    }

    @Test void directoryTokenPathIsReportedAsReadFailureWithoutStderr() throws Exception {
        runProbe("directory-token");
    }

    @Test
    void cloudOidcPathPrecedence() throws Exception {
        runProbe("paths");
    }

    @Test
    void failedFileInitializationCanRetryWithNewConfiguration() throws Exception {
        runProbe("init");
    }

    @Test
    void cachedHumanLoginCanRefreshWithoutLosingRefreshToken() throws Exception {
        runProbe("human-fresh");
    }

    @Test
    void emptyHumanRefreshResponseIsReportedAsCredentialError() throws Exception {
        runProbe("human-empty");
    }

    @Test
    void nearlyExpiredHumanCacheRetainsRefreshTokenOnStartup() throws Exception {
        runProbe("human-expiring");
    }

    @Test
    void privateKeyJvmPropertyWorksForBothProvidersAndAuthenticationMethods() throws Exception {
        runProbe("private-key");
    }

    @Test
    void cloudDefaultPathsAndLegacyKubernetesFallbackArePreserved() throws Exception {
        runProbe("defaults");
    }

    @Test
    void resetAppliesNewTimeoutsToAlreadyLoadedOAuthAndMetadataClients() throws Exception {
        runProbe("http-reset");
    }

    @Test
    void unsupportedPkcs7EnvironmentsAreRejectedAtInitialization() throws Exception {
        runProbe("pkcs7-validation");
    }

    @Test
    void configurationReadsDuringWarmupButProviderReadersWaitForInitialization() throws Exception {
        runProbe("concurrent-init");
    }

    @Test
    void httpTransportDoesNotWaitForFactoryMonitor() throws Exception {
        runProbe("http-monitor");
    }

    @Test
    void resetDuringFirstEc2OrEcsAuthenticationDoesNotDeadlockOrPublishOldProvider() throws Exception {
        runProbe("reset-creation");
    }

    @Test
    void concurrentScopeCreationIsSharedAndFailedCreationCanRetry() throws Exception {
        runProbe("shared-creation");
    }

    @Test
    void concurrentDefaultProvidersKeepScopeAndClientInOneGeneration() throws Exception {
        runProbe("default-generation");
    }

    @Test
    void lateHttpCallerDoesNotOverwriteTheNewConfiguration() throws Exception {
        runProbe("http-generation");
    }

    @Test void unusedExchangeRetainsTransportAfterReset() throws Exception {
        runProbe("lazy-exchange-reset");
    }

    @Test void failedWarmupDoesNotReplaceCommittedHttpConfiguration() throws Exception {
        runProbe("failed-warmup-transport");
    }

    @Test void firstFailedWarmupLeavesNoTransportAndCanRetry() throws Exception {
        runProbe("first-failed-warmup");
    }

    @Test void stagedTransportIsIsolatedAndRemovedOnError() throws Exception {
        runProbe("transport-stage");
    }

    @Test void removedEskConfigHasMigrationHintWithoutChangingGenericJson() throws Exception {
        runProbe("esk-migration");
    }

    @Test void transportSnapshotCannotBeMutatedByCallers() throws Exception {
        runProbe("snapshot-copy");
    }

    @Test void emptyAndNonObjectConfigFailClearlyAndAllowRetry() throws Exception {
        runProbe("invalid-config-content");
    }

    @Test void explicitInvalidAuthenticationEnumsFailWithFieldDiagnostics() throws Exception {
        runProbe("authentication-enum-diagnostics");
    }

    @Test void objectInitializationRejectsNullEnumsButKeepsOmittedDefaults() throws Exception {
        runProbe("object-enum-validation");
    }

    @Test void environmentSpellingHintsOnlyApplyToOidcAndPkcs7() throws Exception {
        runProbe("environment-enum-diagnostics");
    }

    @Test void completedFailedCreationRetriesBeforeOriginalCallerCleanupAndRetiresSuccess() throws Exception {
        runProbe("completed-creation");
    }

    @Test void committedHttpSnapshotIsIsolatedFromPublicConfigurationMutations() throws Exception {
        runProbe("committed-http-config");
    }

    @Test void gcpMetadataRejectsEmptyBodiesAndUnsuccessfulReturnedResponsesSafely() throws Exception {
        runProbe("gcp-metadata-invalid");
    }

    @Test void sameThreadPluginReentryFailsWithoutDeadlockAndCanRetry() throws Exception {
        runProbe("plugin-reentry");
    }

    @Test void platformBuildersUseInjectedEnvironmentAndExplicitOverride() throws Exception {
        runProbe("platform-builder-env");
    }

    @Test void platformBuildersFallBackForBlankEnvironment() throws Exception {
        runProbe("platform-builder-blank");
    }

    private void runProbe(String scenario) throws Exception {
        Path output = temp.resolve("probe.log");
        ProcessBuilder builder = new ProcessBuilder(
                Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", System.getProperty("java.class.path"), Probe.class.getName(), scenario, temp.toString());
        builder.environment().put("SDK_TEST_SECRET", "test-secret");
        builder.environment().put("SDK_TEST_OIDC_FILE", temp.resolve("custom.jwt").toString());
        builder.environment().put("AWS_WEB_IDENTITY_TOKEN_FILE", temp.resolve("platform.jwt").toString());
        builder.environment().put("ALIBABA_CLOUD_OIDC_TOKEN_FILE", temp.resolve("platform.jwt").toString());
        builder.environment().put("CLOUD_IDAAS_HUMAN_CREDENTIAL_CACHE_PATH", temp.resolve("human.json").toString());
        builder.environment().remove("SDK_TEST_MISSING_PATH");
        builder.environment().remove("SDK_TEST_PRIVATE_KEY");
        if (scenario.equals("platform-builder-blank")) {
            builder.environment().put("AWS_WEB_IDENTITY_TOKEN_FILE", "  ");
            builder.environment().put("ALIBABA_CLOUD_OIDC_TOKEN_FILE", "  ");
        }
        if (scenario.equals("defaults")) {
            builder.environment().remove("AWS_WEB_IDENTITY_TOKEN_FILE");
            builder.environment().remove("ALIBABA_CLOUD_OIDC_TOKEN_FILE");
        }
        Process process = builder.redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(30, TimeUnit.SECONDS), "probe timed out: " + scenario);
            assertEquals(0, process.exitValue(), new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
        } finally {
            process.destroyForcibly();
        }
    }

    /** Child-process entry point; all HTTP calls are intercepted before OAuth static initialization. */
    public static class Probe {
        public static void main(String[] args) throws Exception {
            Path dir = Paths.get(args[1]);
            if (args[0].equals("first-failed-warmup")) {
                checkFirstFailedWarmup(dir);
                return;
            }
            if (args[0].equals("plugin-cold-http") || args[0].equals("plugin-cold-failure")) {
                checkColdPluginHttp(dir, args[0].equals("plugin-cold-failure"));
                return;
            }
            if (args[0].equals("plugin-cold-superseded")) {
                checkSupersededPlugin(dir);
                return;
            }
            IDaaSCredentialProviderFactory.init(config());
            if (args[0].equals("plugin-worker-provider")) {
                checkPluginWorkerProvider(dir);
            } else if (args[0].equals("plugin-old-transport")) {
                checkPluginOldTransport(dir);
            } else if (args[0].equals("plugin-init-wait")) {
                checkPluginInitWait(dir);
            } else if (args[0].equals("completion-gap")) {
                checkCompletionGap();
            } else if (args[0].equals("plugin-config-worker")) {
                checkPluginConfigWorker(dir, true);
            } else if (args[0].equals("plugin-http-mutation")) {
                checkPluginConfigWorker(dir, false);
            } else if (args[0].equals("directory-token")) {
                checkDirectoryToken(dir);
            } else if (args[0].equals("plugin-reentry")) {
                checkPluginReentry(dir);
            } else if (args[0].equals("platform-builder-env")) {
                checkPlatformBuilderEnvironment(dir, false);
            } else if (args[0].equals("platform-builder-blank")) {
                checkPlatformBuilderEnvironment(dir, true);
            } else if (args[0].equals("completed-creation")) {
                checkCompletedCreation();
            } else if (args[0].equals("committed-http-config")) {
                checkCommittedHttpConfig(dir);
            } else if (args[0].equals("gcp-metadata-invalid")) {
                checkGcpMetadataInvalid();
            } else if (args[0].equals("authentication-enum-diagnostics")) {
                checkAuthenticationEnumDiagnostics(dir);
            } else if (args[0].equals("object-enum-validation")) {
                checkObjectEnumValidation();
            } else if (args[0].equals("environment-enum-diagnostics")) {
                checkEnvironmentEnumDiagnostics(dir);
            } else if (args[0].equals("invalid-config-content")) {
                checkInvalidConfigContent(dir);
            } else if (args[0].equals("esk-migration")) {
                checkEskMigration(dir);
            } else if (args[0].equals("snapshot-copy")) {
                checkSnapshotCopy();
            } else if (args[0].equals("transport-stage")) {
                checkTransportStage();
            } else if (args[0].equals("lazy-exchange-reset")) {
                checkLazyExchangeReset();
            } else if (args[0].equals("failed-warmup-transport")) {
                checkFailedWarmupTransport(dir);
            } else if (args[0].equals("default-generation")) {
                checkDefaultGeneration();
            } else if (args[0].equals("http-generation")) {
                checkHttpGeneration();
            } else if (args[0].equals("http-monitor")) {
                checkHttpMonitor();
            } else if (args[0].equals("reset-creation")) {
                checkResetCreation();
            } else if (args[0].equals("shared-creation")) {
                checkSharedCreation();
            } else if (args[0].equals("paths")) {
                checkPaths(dir);
            } else if (args[0].equals("init")) {
                checkInitialization(dir);
            } else if (args[0].equals("private-key")) {
                checkPrivateKey();
            } else if (args[0].equals("defaults")) {
                checkDefaults();
            } else if (args[0].equals("http-reset")) {
                checkHttpReset();
            } else if (args[0].equals("pkcs7-validation")) {
                checkPkcs7Validation();
            } else if (args[0].equals("concurrent-init")) {
                checkConcurrentInitialization(dir);
            } else if (args[0].equals("human-empty")) {
                checkEmptyHumanRefresh(dir);
            } else {
                checkHumanCache(dir, args[0].equals("human-expiring"));
            }
        }

        private static void checkPlatformBuilderEnvironment(Path dir, boolean blank) {
            String eks = blank ? com.cloud_idaas.core.domain.constants.AuthenticationConstants.AWS_EKS_OIDC_TOKEN_DEFAULT_PATH
                    : dir.resolve("platform.jwt").toString();
            String ack = blank ? com.cloud_idaas.core.domain.constants.AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_DEFAULT_PATH
                    : dir.resolve("platform.jwt").toString();
            assertEquals(eks, com.cloud_idaas.core.implementation.authentication.oidc.AwsEksOidcTokenProvider.builder().build().getOidcTokenFilePath());
            assertEquals(ack, com.cloud_idaas.core.implementation.authentication.oidc.AlibabaCloudAckOidcTokenProvider.builder().build().getOidcTokenFilePath());
            String explicit = dir.resolve("explicit.jwt").toString();
            assertEquals(explicit, com.cloud_idaas.core.implementation.authentication.oidc.AwsEksOidcTokenProvider.builder()
                    .oidcTokenFilePath(explicit).build().getOidcTokenFilePath());
            assertEquals(explicit, com.cloud_idaas.core.implementation.authentication.oidc.AlibabaCloudAckOidcTokenProvider.builder()
                    .oidcTokenFilePath(explicit).build().getOidcTokenFilePath());
        }

        private static Path pluginFile(Path dir, String name, int timeout) throws Exception {
            IDaaSClientConfig candidate = pluginConfig();
            candidate.setClientId(name);
            candidate.setScope(name + "|read");
            candidate.getHttpConfiguration().setConnectTimeout(timeout);
            Path file = dir.resolve(name + ".json");
            Files.write(file, JSONUtil.toJSONBytes(candidate));
            return file;
        }

        private static void checkPluginWorkerProvider(Path dir) throws Exception {
            java.util.concurrent.ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor();
            worker.submit(() -> {}).get(2, TimeUnit.SECONDS); // An already-existing pool, not inherited ThreadLocal state.
            AtomicInteger calls = new AtomicInteger();
            try {
                installPlugin(scope -> {
                    calls.incrementAndGet();
                    if (scope.equals("outer|read")) {
                        try {
                            return (IDaaSTokenResponse) worker.submit(() -> IDaaSCredentialProviderFactory
                                    .getIDaaSCredentialProvider("inner|read").getCredential()).get(2, TimeUnit.SECONDS);
                        } catch (Exception e) { throw new AssertionError(e); }
                    }
                    return token("worker-provider", null, 3600);
                });
                IDaaSCredentialProviderFactory.reset();
                Path file = pluginFile(dir, "outer", 3000);
                IDaaSCredentialProviderFactory.init(file.toString());
                assertEquals("worker-provider", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
                assertEquals(2, calls.get());
            } finally { worker.shutdownNow(); }
        }

        private static void checkColdPluginHttp(Path dir, boolean fail) throws Exception {
            assertNull(IDaaSCredentialProviderFactory.getHttpConfigSnapshot());
            java.util.concurrent.ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor();
            worker.submit(() -> {}).get(2, TimeUnit.SECONDS);
            java.util.concurrent.atomic.AtomicReference<HttpClient> candidate = new java.util.concurrent.atomic.AtomicReference<>();
            AtomicInteger requests = new AtomicInteger();
            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
                    new java.net.InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/token", exchange -> {
                requests.incrementAndGet();
                byte[] response = JSONUtil.toJSONBytes(token("worker-http", null, 3600));
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response); exchange.close();
            });
            server.start();
            try {
                installPlugin(scope -> {
                    try {
                        IDaaSTokenResponse result = worker.submit(() -> {
                            HttpClient transport = HttpClientFactory.getDefaultHttpClient();
                            candidate.set(transport);
                            Field field = transport.getClass().getDeclaredField("okHttpClient");
                            field.setAccessible(true);
                            assertEquals(3000, ((okhttp3.OkHttpClient) field.get(transport)).connectTimeoutMillis());
                            return com.cloud_idaas.core.http.OAuth2TokenUtil.getTokenWithClientSecretPost(
                                    "synthetic", "synthetic-secret", "http://127.0.0.1:" + server.getAddress().getPort() + "/token", "test|read");
                        }).get(2, TimeUnit.SECONDS);
                        assertNull(IDaaSCredentialProviderFactory.getHttpConfigSnapshot());
                        assertSame(candidate.get(), HttpClientFactory.getDefaultHttpClient());
                        if (fail) throw new AssertionError("synthetic warmup failure");
                        return result;
                    } catch (Exception e) { throw new AssertionError(e); }
                });
                Path file = pluginFile(dir, "cold", 3000);
                if (fail) {
                    assertThrows(AssertionError.class, () -> IDaaSCredentialProviderFactory.init(file.toString()));
                    assertNull(IDaaSCredentialProviderFactory.getHttpConfigSnapshot());
                    assertThrows(ConfigException.class, HttpClientFactory::getDefaultHttpClient);
                    assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                    installPlugin(scope -> token("retry", null, 3600));
                    IDaaSCredentialProviderFactory.init(file.toString());
                    assertNotSame(candidate.get(), HttpClientFactory.getDefaultHttpClient());
                    assertEquals("retry", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
                } else {
                    IDaaSCredentialProviderFactory.init(file.toString());
                    assertSame(candidate.get(), HttpClientFactory.getDefaultHttpClient());
                    assertEquals("worker-http", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
                }
                assertEquals(1, requests.get());
                assertEquals(3000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            } finally { server.stop(0); worker.shutdownNow(); }
        }

        private static void checkPluginOldTransport(Path dir) throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig old = config();
            old.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            old.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            IDaaSCredentialProviderFactory.init(old);
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                requests.incrementAndGet();
                return new HttpResponse(200, JSONUtil.toJSONString(token("old-transport", null, 3600)));
            });
            HttpClient committed = HttpClientFactory.getDefaultHttpClient();
            IDaaSMachineTokenExchangeCredentialProvider exchange = IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
            IDaaSCredentialProviderFactory.reset();
            java.util.concurrent.ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor();
            try {
                installPlugin(scope -> {
                    try {
                        assertNotSame(committed, HttpClientFactory.getDefaultHttpClient());
                        assertEquals("old-transport", worker.submit(() -> {
                            assertSame(committed, HttpClientFactory.getDefaultHttpClient());
                            return exchange.getCredential("subject", "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken();
                        }).get(2, TimeUnit.SECONDS));
                    } catch (Exception e) { throw new AssertionError(e); }
                    throw new AssertionError("synthetic failed reinitialization");
                });
                Path file = pluginFile(dir, "failed-plugin", 3000);
                assertThrows(AssertionError.class, () -> IDaaSCredentialProviderFactory.init(file.toString()));
                assertSame(committed, HttpClientFactory.getDefaultHttpClient());
                assertEquals(5000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
                assertEquals("old-transport", exchange.getCredential("subject", "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken());
                assertEquals(2, requests.get());
            } finally { worker.shutdownNow(); }
        }

        private static void checkPluginInitWait(Path dir) throws Exception {
            for (boolean fail : new boolean[]{false, true}) {
                IDaaSCredentialProviderFactory.reset();
                java.util.concurrent.CountDownLatch entered = new java.util.concurrent.CountDownLatch(1);
                java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
                java.util.concurrent.CountDownLatch waiting = new java.util.concurrent.CountDownLatch(1);
                installPlugin(scope -> {
                    // Same-thread reentrant init keeps the existing ignored-init behavior.
                    IDaaSCredentialProviderFactory.init(config());
                    IDaaSCredentialProviderFactory.init(dir.resolve("does-not-exist.json").toString());
                    entered.countDown(); await(release);
                    if (fail) throw new AssertionError("synthetic plugin failure");
                    return token("ready", null, 3600);
                });
                Path file = pluginFile(dir, "wait-owner", 3000);
                java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);
                try {
                    java.util.concurrent.Future<?> owner = pool.submit(() -> IDaaSCredentialProviderFactory.init(file.toString()));
                    await(entered);
                    java.util.concurrent.Future<?> second = pool.submit(() -> {
                        Thread.currentThread().interrupt();
                        try {
                            waiting.countDown();
                            IDaaSClientConfig retry = config(); retry.setClientId("retry-client");
                            IDaaSCredentialProviderFactory.init(retry);
                            assertTrue(Thread.currentThread().isInterrupted());
                        } finally { Thread.interrupted(); }
                    });
                    await(waiting);
                    assertThrows(java.util.concurrent.TimeoutException.class, () -> second.get(100, TimeUnit.MILLISECONDS));
                    release.countDown();
                    if (fail) assertThrows(java.util.concurrent.ExecutionException.class, () -> owner.get(2, TimeUnit.SECONDS));
                    else owner.get(2, TimeUnit.SECONDS);
                    second.get(2, TimeUnit.SECONDS);
                    assertEquals(fail ? "retry-client" : "wait-owner", IDaaSCredentialProviderFactory.getClientId());
                } finally { release.countDown(); pool.shutdownNow(); }
            }
        }

        private static void checkSupersededPlugin(Path dir) throws Exception {
            java.util.concurrent.CountDownLatch enteredA = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch enteredB = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch releaseA = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch releaseB = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.atomic.AtomicReference<HttpClient> candidateB = new java.util.concurrent.atomic.AtomicReference<>();
            installPlugin(scope -> {
                if (scope.equals("generation-a|read")) { enteredA.countDown(); await(releaseA); }
                else { candidateB.set(HttpClientFactory.getDefaultHttpClient()); enteredB.countDown(); await(releaseB); }
                return token(scope, null, 3600);
            });
            Path a = pluginFile(dir, "generation-a", 3000);
            Path b = pluginFile(dir, "generation-b", 4000);
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);
            try {
                java.util.concurrent.Future<?> first = pool.submit(() -> IDaaSCredentialProviderFactory.init(a.toString()));
                await(enteredA);
                IDaaSCredentialProviderFactory.reset();
                java.util.concurrent.Future<?> second = pool.submit(() -> IDaaSCredentialProviderFactory.init(b.toString()));
                await(enteredB);
                releaseA.countDown();
                java.util.concurrent.ExecutionException obsolete = assertThrows(java.util.concurrent.ExecutionException.class,
                        () -> first.get(2, TimeUnit.SECONDS));
                assertTrue(obsolete.getCause() instanceof IllegalStateException);
                assertNull(IDaaSCredentialProviderFactory.getHttpConfigSnapshot());
                assertSame(candidateB.get(), HttpClientFactory.getDefaultHttpClient());
                releaseB.countDown(); second.get(2, TimeUnit.SECONDS);
                assertEquals("generation-b", IDaaSCredentialProviderFactory.getClientId());
                assertEquals(4000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
                assertSame(candidateB.get(), HttpClientFactory.getDefaultHttpClient());
                assertEquals("generation-b|read", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
            } finally { releaseA.countDown(); releaseB.countDown(); pool.shutdownNow(); }
        }

        private static void checkDirectoryToken(Path dir) throws Exception {
            java.io.PrintStream original = System.err;
            java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            try {
                System.setErr(new java.io.PrintStream(output));
                com.cloud_idaas.core.exception.CredentialException failure = assertThrows(
                        com.cloud_idaas.core.exception.CredentialException.class,
                        () -> new com.cloud_idaas.core.implementation.authentication.oidc.FileOidcTokenProvider(dir.toString()).getOidcToken());
                assertTrue(failure.getMessage().contains("could not be read"));
                assertFalse(failure.getMessage().contains(dir.toString()));
                assertNull(failure.getCause());
                assertEquals("", output.toString("UTF-8"));
            } finally {
                System.setErr(original);
            }
        }

        private static void checkPluginConfigWorker(Path dir, boolean readOnWorker) throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig configuration = pluginConfig();
            configuration.setDeveloperApiEndpoint("https://example.invalid/developer");
            configuration.getHttpConfiguration().setConnectTimeout(3000);
            configuration.getHttpConfiguration().setReadTimeout(4000);
            java.util.concurrent.ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor(job -> {
                Thread thread = new Thread(job); thread.setDaemon(true); return thread;
            });
            try {
                installPlugin(scope -> {
                    try {
                        if (readOnWorker) {
                            assertEquals("sdk-regression-client", worker.submit(IDaaSCredentialProviderFactory::getClientId).get(2, TimeUnit.SECONDS));
                            assertEquals(configuration.getIdaasInstanceId(), worker.submit(IDaaSCredentialProviderFactory::getIDaasInstanceId).get(2, TimeUnit.SECONDS));
                            assertEquals(configuration.getOpenApiEndpoint(), worker.submit(IDaaSCredentialProviderFactory::getOpenApiEndpoint).get(2, TimeUnit.SECONDS));
                            assertEquals(configuration.getDeveloperApiEndpoint(), worker.submit(IDaaSCredentialProviderFactory::getDeveloperApiEndpoint).get(2, TimeUnit.SECONDS));
                            assertEquals(3000, worker.submit(IDaaSCredentialProviderFactory::getHttpConfig).get(2, TimeUnit.SECONDS).getConnectTimeout());
                        }
                        IDaaSCredentialProviderFactory.getHttpConfig().setConnectTimeout(123);
                        Object client = HttpClientFactory.getDefaultHttpClient();
                        Field transport = client.getClass().getDeclaredField("okHttpClient");
                        transport.setAccessible(true);
                        assertEquals(3000, ((okhttp3.OkHttpClient) transport.get(client)).connectTimeoutMillis());
                        return token("plugin-ready", null, 3600);
                    } catch (Exception e) { throw new AssertionError(e); }
                });
                Path file = dir.resolve("plugin-worker.json");
                Files.write(file, JSONUtil.toJSONBytes(configuration));
                IDaaSCredentialProviderFactory.init(file.toString());
                assertEquals(3000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
                assertEquals("plugin-ready", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
            } finally { worker.shutdownNow(); }
        }

        private static void checkCompletionGap() throws Exception {
            String scope = "completion-gap|read";
            java.util.concurrent.CountDownLatch fetching = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch finishFetch = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch missed = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch resume = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.ConcurrentMap<String, com.cloud_idaas.core.provider.IDaaSCredentialProvider> providers =
                    new java.util.concurrent.ConcurrentHashMap<String, com.cloud_idaas.core.provider.IDaaSCredentialProvider>() {
                        @Override public com.cloud_idaas.core.provider.IDaaSCredentialProvider get(Object key) {
                            com.cloud_idaas.core.provider.IDaaSCredentialProvider value = super.get(key);
                            if (Thread.currentThread().getName().equals("late-reader") && value == null) {
                                missed.countDown();
                                try { assertTrue(resume.await(5, TimeUnit.SECONDS)); }
                                catch (InterruptedException e) { throw new AssertionError(e); }
                            }
                            return value;
                        }
                    };
            Field field = IDaaSCredentialProviderFactory.class.getDeclaredField("CREDENTIAL_PROVIDERS");
            field.setAccessible(true);
            AtomicInteger calls = new AtomicInteger();
            installPlugin(ignored -> {
                calls.incrementAndGet(); fetching.countDown();
                try { assertTrue(finishFetch.await(5, TimeUnit.SECONDS)); }
                catch (InterruptedException e) { throw new AssertionError(e); }
                return token("shared", null, 3600);
            });
            IDaaSCredentialProviderFactory.reset();
            IDaaSCredentialProviderFactory.init(pluginConfig());
            field.set(null, providers);
            java.util.concurrent.ExecutorService first = java.util.concurrent.Executors.newSingleThreadExecutor();
            java.util.concurrent.ExecutorService late = java.util.concurrent.Executors.newSingleThreadExecutor(job -> new Thread(job, "late-reader"));
            try {
                java.util.concurrent.Future<?> b = first.submit(() -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope));
                assertTrue(fetching.await(5, TimeUnit.SECONDS));
                java.util.concurrent.Future<?> a = late.submit(() -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope));
                assertTrue(missed.await(5, TimeUnit.SECONDS));
                finishFetch.countDown();
                Object published = b.get(2, TimeUnit.SECONDS); // Includes task retirement before A resumes.
                resume.countDown();
                assertSame(published, a.get(2, TimeUnit.SECONDS));
                assertEquals(1, calls.get());
            } finally { finishFetch.countDown(); resume.countDown(); first.shutdownNow(); late.shutdownNow(); }
        }

        private static void installPlugin(java.util.function.Function<String, IDaaSTokenResponse> callback) throws Exception {
            Field field = com.cloud_idaas.core.util.PluginCredentialProviderUtil.class.getDeclaredField("PLUGIN_CREDENTIAL_PROVIDER_MAP");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, com.cloud_idaas.core.provider.PluginCredentialProvider> plugins =
                    (java.util.Map<String, com.cloud_idaas.core.provider.PluginCredentialProvider>) field.get(null);
            plugins.put("reentry-probe", new com.cloud_idaas.core.provider.PluginCredentialProvider() {
                public String getName() { return "reentry-probe"; }
                public IDaaSTokenResponse getIDaaSCredential(String scope) { return callback.apply(scope); }
            });
        }

        private static IDaaSClientConfig pluginConfig() {
            IDaaSClientConfig config = config();
            config.setOpenApiEndpoint("https://example.invalid/openapi");
            config.setScope("outer|read");
            config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PLUGIN);
            config.getAuthnConfiguration().setPluginName("reentry-probe");
            return config;
        }

        private static void checkPluginReentry(Path dir) throws Exception {
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newSingleThreadExecutor(job -> {
                Thread thread = new Thread(job);
                thread.setDaemon(true);
                return thread;
            });
            try {
                for (String mode : new String[]{"direct", "cycle", "file"}) {
                    IDaaSCredentialProviderFactory.reset();
                    IDaaSClientConfig configuration = pluginConfig();
                    installPlugin(scope -> {
                        String nested = mode.equals("cycle") && scope.equals("outer|read") ? "inner|read"
                                : mode.equals("cycle") ? "outer|read" : scope;
                        IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(nested);
                        return token("unreachable", null, 3600);
                    });
                    Path file = dir.resolve("plugin.json");
                    Files.write(file, JSONUtil.toJSONBytes(configuration));
                    if (!mode.equals("file")) IDaaSCredentialProviderFactory.init(configuration);
                    java.util.concurrent.Future<?> request = pool.submit(() -> {
                        if (mode.equals("file")) IDaaSCredentialProviderFactory.init(file.toString());
                        else IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("outer|read");
                    });
                    java.util.concurrent.ExecutionException failure = assertThrows(java.util.concurrent.ExecutionException.class,
                            () -> request.get(2, TimeUnit.SECONDS));
                    Throwable root = failure.getCause();
                    while (root.getCause() != null) root = root.getCause();
                    assertTrue(root instanceof ConfigException);
                    assertEquals("RecursiveCredentialCreation", ((ConfigException) root).getErrorCode());
                    if (mode.equals("file")) assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                    installPlugin(scope -> token("recovered", null, 3600));
                    // Retry on the same worker proves failed recursion does not leave thread-local ownership behind.
                    assertEquals("recovered", pool.submit(() -> {
                        if (mode.equals("file")) IDaaSCredentialProviderFactory.init(file.toString());
                        return IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("outer|read").getCredential().getAccessToken();
                    }).get(2, TimeUnit.SECONDS));
                }
                IDaaSCredentialProviderFactory.reset();
                IDaaSCredentialProviderFactory.init(pluginConfig());
                AtomicInteger calls = new AtomicInteger();
                installPlugin(scope -> {
                    calls.incrementAndGet();
                    if (scope.equals("outer|read")) IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("inner|read");
                    return token(scope, null, 3600);
                });
                assertEquals("outer|read", pool.submit(() -> IDaaSCredentialProviderFactory
                        .getIDaaSCredentialProvider("outer|read").getCredential().getAccessToken()).get(2, TimeUnit.SECONDS));
                assertEquals(2, calls.get(), "Acyclic different-scope delegation remains supported");
            } finally {
                pool.shutdownNow();
            }
        }

        private static void checkCompletedCreation() throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig next = config();
            next.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            next.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            IDaaSCredentialProviderFactory.init(next);
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                requests.incrementAndGet();
                return new HttpResponse(200, JSONUtil.toJSONString(token("retried", null, 3600)));
            });
            Field field = IDaaSCredentialProviderFactory.class.getDeclaredField("CREDENTIAL_CREATIONS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.concurrent.ConcurrentMap<String, java.util.concurrent.FutureTask<com.cloud_idaas.core.provider.IDaaSCredentialProvider>> tasks =
                    (java.util.concurrent.ConcurrentMap<String, java.util.concurrent.FutureTask<com.cloud_idaas.core.provider.IDaaSCredentialProvider>>) field.get(null);
            String scope = "completed|read";
            java.util.concurrent.FutureTask<com.cloud_idaas.core.provider.IDaaSCredentialProvider> failed =
                    new java.util.concurrent.FutureTask<>(() -> { throw new ConfigException("synthetic completed failure"); });
            failed.run();
            assertTrue(failed.isDone());
            // Deterministically model completion before the original caller executes its cleanup.
            tasks.put(scope, failed);
            com.cloud_idaas.core.provider.IDaaSCredentialProvider provider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);
            assertEquals("retried", provider.getCredential().getAccessToken());
            assertEquals(1, requests.get());
            assertFalse(tasks.containsKey(scope), "Successful creation must retire its FutureTask");
            assertSame(provider, IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope));
            assertEquals(1, requests.get());
        }

        private static void checkCommittedHttpConfig(Path dir) throws Exception {
            HttpClient original = HttpClientFactory.getDefaultHttpClient();
            com.cloud_idaas.core.config.HttpConfiguration exposed = IDaaSCredentialProviderFactory.getHttpConfig();
            exposed.setConnectTimeout(0);
            exposed.setReadTimeout(1);
            assertEquals(5000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            assertEquals(10000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getReadTimeout());
            assertSame(original, HttpClientFactory.getDefaultHttpClient());
            IDaaSCredentialProviderFactory.reset();
            exposed.setConnectTimeout(60000);
            assertEquals(5000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            IDaaSCredentialProviderFactory.init(config());
            installHttpClient(request -> new HttpResponse(200, JSONUtil.toJSONString(token("file-init", null, 3600))));
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig fileConfig = config();
            fileConfig.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            fileConfig.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            Path file = dir.resolve("committed-http.json");
            Files.write(file, JSONUtil.toJSONBytes(fileConfig));
            IDaaSCredentialProviderFactory.init(file.toString());
            HttpClient committed = HttpClientFactory.getDefaultHttpClient();
            exposed = IDaaSCredentialProviderFactory.getHttpConfig();
            exposed.setConnectTimeout(0);
            exposed.setReadTimeout(1);
            assertSame(committed, HttpClientFactory.getDefaultHttpClient());
            assertEquals(5000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            assertEquals(10000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getReadTimeout());
        }

        private static void checkGcpMetadataInvalid() throws Exception {
            for (String body : new String[]{null, "", "  \n\t"}) {
                installHttpClient(request -> new HttpResponse(200, body));
                com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider provider =
                        com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider.builder()
                                .audience("https://synthetic.invalid").build();
                com.cloud_idaas.core.exception.CredentialException failure = assertThrows(
                        com.cloud_idaas.core.exception.CredentialException.class, provider::refreshCredential);
                assertEquals("EmptyGcpIdentityToken", failure.getErrorCode());
                assertNull(failure.getCause());
            }
            for (int status : new int[]{400, 500}) {
                installHttpClient(request -> new HttpResponse(status, "synthetic-sensitive-body"));
                com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider provider =
                        com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider.builder()
                                .audience("https://synthetic.invalid").build();
                com.cloud_idaas.core.exception.CredentialException failure = assertThrows(
                        com.cloud_idaas.core.exception.CredentialException.class, provider::refreshCredential);
                assertEquals("GcpMetadataRequestFailed", failure.getErrorCode());
                assertTrue(failure.getMessage().contains(Integer.toString(status)));
                assertFalse(failure.getMessage().contains("synthetic-sensitive-body"));
                assertNull(failure.getCause());
            }
        }

        private static String enumConfig(String field, String literal, String method) {
            com.google.gson.JsonObject root = JSONUtil.parseObject(JSONUtil.toJSONString(config()), com.google.gson.JsonObject.class);
            com.google.gson.JsonObject auth = root.getAsJsonObject("authnConfiguration");
            auth.addProperty("authnMethod", method);
            auth.addProperty("applicationFederatedCredentialName", "synthetic-federation");
            auth.addProperty("clientSecretEnvVarName", "SDK_TEST_SECRET");
            // Keep explicit JSON null in the fixture even though the production Gson omits nulls on serialization.
            auth.addProperty(field, "ENUM_FIXTURE_VALUE");
            return JSONUtil.toJSONString(root).replace("\"ENUM_FIXTURE_VALUE\"", literal);
        }

        private static ConfigException expectFileDiagnostic(Path dir, String content, String code, String field) throws Exception {
            IDaaSCredentialProviderFactory.reset();
            Path file = dir.resolve("enum-diagnostic.json");
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
            ConfigException failure = assertThrows(ConfigException.class,
                    () -> IDaaSCredentialProviderFactory.init(file.toString()));
            assertTrue(failure.getCause() instanceof ConfigException);
            assertEquals(code, ((ConfigException) failure.getCause()).getErrorCode());
            assertTrue(failure.getMessage().contains(field));
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
            IDaaSCredentialProviderFactory.init(config());
            assertEquals("sdk-regression-client", IDaaSCredentialProviderFactory.getClientId());
            return failure;
        }

        private static void checkAuthenticationEnumDiagnostics(Path dir) throws Exception {
            for (String field : new String[]{"authnMethod", "identityType"}) {
                String expected = field.equals("authnMethod") ? "CLIENT_SECRET_POST" : "HUMAN";
                String code = field.equals("authnMethod") ? "UnsupportedAuthenticationMethod" : "InvalidAuthenticationIdentity";
                for (String spelling : new String[]{expected.toLowerCase(java.util.Locale.ROOT), " " + expected + " "}) {
                    ConfigException failure = expectFileDiagnostic(dir,
                            enumConfig(field, JSONUtil.toJSONString(spelling), "NONE"), code, field);
                    assertTrue(failure.getMessage().contains("Did you mean '" + expected + "'?"));
                }
                for (String invalid : new String[]{"null", "123", "true", "[]", "{}", "\"synthetic-sensitive-value\""}) {
                    ConfigException failure = expectFileDiagnostic(dir, enumConfig(field, invalid, "NONE"), code, field);
                    assertFalse(failure.getMessage().contains("synthetic-sensitive-value"));
                    assertFalse(failure.getMessage().contains("Did you mean"));
                }
            }
            // Generic Gson remains unchanged; only factory validation rejects the null enum.
            IdentityAuthenticationConfiguration parsed = JSONUtil.parseObject("{\"identityType\":\"human\"}",
                    IdentityAuthenticationConfiguration.class);
            assertNull(parsed.getIdentityType());
        }

        private static void checkObjectEnumValidation() {
            for (boolean identity : new boolean[]{true, false}) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig invalid = config();
                if (identity) invalid.getAuthnConfiguration().setIdentityType(null);
                else invalid.getAuthnConfiguration().setAuthnMethod(null);
                ConfigException failure = assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(invalid));
                assertEquals(identity ? "InvalidAuthenticationIdentity" : "UnsupportedAuthenticationMethod", failure.getErrorCode());
                assertTrue(failure.getMessage().contains(identity ? "identityType" : "authnMethod"));
                assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                IDaaSCredentialProviderFactory.init(config());
                assertEquals("sdk-regression-client", IDaaSCredentialProviderFactory.getClientId());
            }
            IdentityAuthenticationConfiguration defaults = JSONUtil.parseObject("{}", IdentityAuthenticationConfiguration.class);
            assertEquals(com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum.CLIENT, defaults.getIdentityType());
            assertEquals(TokenAuthnMethod.NONE, defaults.getAuthnMethod());
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig omitted = config();
            omitted.setAuthnConfiguration(defaults);
            assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(omitted));
        }

        private static void checkEnvironmentEnumDiagnostics(Path dir) throws Exception {
            for (String method : new String[]{"OIDC", "PKCS7"}) {
                for (ClientDeployEnvironmentEnum environment : ClientDeployEnvironmentEnum.values()) {
                    for (String spelling : new String[]{environment.name().toLowerCase(java.util.Locale.ROOT), " " + environment.name() + " "}) {
                        ConfigException failure = expectFileDiagnostic(dir,
                                enumConfig("clientDeployEnvironment", JSONUtil.toJSONString(spelling), method),
                                "UnsupportedClientDeployEnvironment", "clientDeployEnvironment");
                        assertTrue(failure.getMessage().contains("Did you mean '" + environment.name() + "'?"));
                    }
                }
                expectFileDiagnostic(dir, enumConfig("clientDeployEnvironment", "\"unknown-environment\"", method),
                        "ClientDeployEnvironmentNotFound", "init failed");
            }
            installHttpClient(request -> new HttpResponse(200, JSONUtil.toJSONString(token("valid", null, 3600))));
            for (String value : new String[]{"aws_ec2", "unknown-environment", " AWS_EKS "}) {
                IDaaSCredentialProviderFactory.reset();
                Path file = dir.resolve("irrelevant-environment.json");
                Files.write(file, enumConfig("clientDeployEnvironment", JSONUtil.toJSONString(value), "CLIENT_SECRET_POST")
                        .getBytes(StandardCharsets.UTF_8));
                assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(file.toString()));
                assertEquals("valid", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
            }
        }

        private static void checkEskMigration(Path dir) throws Exception {
            assertNull(JSONUtil.parseObject("\"AWS_ESK\"", ClientDeployEnvironmentEnum.class));
            for (String method : new String[]{"OIDC", "PKCS7", "NONE"}) {
                for (String oldName : new String[]{"AWS_ESK", "aws_esk", " AWS_ESK ", "  AwS_eSk  "}) {
                    IDaaSCredentialProviderFactory.reset();
                    String content = JSONUtil.toJSONString(config()).replace("\"authnMethod\":\"NONE\"",
                            "\"authnMethod\":\"" + method + "\",\"clientDeployEnvironment\":\"AWS_ESK\"");
                    content = content.replace("AWS_ESK", oldName);
                    assertTrue(content.contains(oldName));
                    Path file = dir.resolve("esk-" + method + ".json");
                    Files.write(file, content.getBytes(StandardCharsets.UTF_8));
                    ConfigException failure = assertThrows(ConfigException.class,
                            () -> IDaaSCredentialProviderFactory.init(file.toString()));
                    assertTrue(failure.getMessage().contains("AWS_ESK"));
                    assertTrue(failure.getMessage().contains("AWS_EKS"));
                    assertTrue(failure.getCause() instanceof ConfigException);
                    assertEquals("RemovedClientDeployEnvironment", ((ConfigException) failure.getCause()).getErrorCode());
                    assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                    IDaaSCredentialProviderFactory.init(config());
                    assertEquals("sdk-regression-client", IDaaSCredentialProviderFactory.getClientId());
                }
            }
        }

        private static void checkInvalidConfigContent(Path dir) throws Exception {
            for (String content : new String[]{"", "  \n\t", "null", "[]", "123", "true", "\"synthetic-config-body\""}) {
                IDaaSCredentialProviderFactory.reset();
                Path file = dir.resolve("invalid-content.json");
                Files.write(file, content.getBytes(StandardCharsets.UTF_8));
                ConfigException failure = assertThrows(ConfigException.class,
                        () -> IDaaSCredentialProviderFactory.init(file.toString()));
                assertTrue(failure.getMessage().contains("non-empty JSON object"));
                assertFalse(failure.getMessage().contains("synthetic-config-body"));
                assertTrue(failure.getCause() instanceof ConfigException);
                assertEquals("InvalidConfigContent", ((ConfigException) failure.getCause()).getErrorCode());
                assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                IDaaSCredentialProviderFactory.init(config());
                assertEquals("sdk-regression-client", IDaaSCredentialProviderFactory.getClientId());
            }
        }

        private static void checkSnapshotCopy() {
            HttpClient old = HttpClientFactory.getDefaultHttpClient();
            com.cloud_idaas.core.config.HttpConfiguration exposed = IDaaSCredentialProviderFactory.getHttpConfigSnapshot();
            int timeout = exposed.getConnectTimeout();
            exposed.setConnectTimeout(60000);
            assertEquals(timeout, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            assertSame(old, HttpClientFactory.getDefaultHttpClient());
            IDaaSCredentialProviderFactory.reset();
            exposed = IDaaSCredentialProviderFactory.getHttpConfigSnapshot();
            exposed.setReadTimeout(60000);
            assertEquals(10000, IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getReadTimeout());
            assertSame(old, HttpClientFactory.getDefaultHttpClient());
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getHttpConfig);
        }

        private static void checkTransportStage() throws Exception {
            HttpClient old = HttpClientFactory.getDefaultHttpClient();
            assertThrows(IllegalArgumentException.class,
                    () -> HttpClientFactory.withInitializationHttpConfig(null, () -> fail("must not warm up")));
            assertThrows(IllegalArgumentException.class,
                    () -> HttpClientFactory.withInitializationHttpConfig(new com.cloud_idaas.core.config.HttpConfiguration(), null));
            assertSame(old, HttpClientFactory.getDefaultHttpClient());
            com.cloud_idaas.core.config.HttpConfiguration pending = new com.cloud_idaas.core.config.HttpConfiguration();
            pending.setConnectTimeout(2000);
            java.util.concurrent.ExecutorService worker = java.util.concurrent.Executors.newSingleThreadExecutor();
            try {
                assertThrows(AssertionError.class, () -> HttpClientFactory.withInitializationHttpConfig(pending, () -> {
                    assertNotSame(old, HttpClientFactory.getDefaultHttpClient());
                    try {
                        assertSame(old, worker.submit(HttpClientFactory::getDefaultHttpClient).get(2, TimeUnit.SECONDS));
                    } catch (Exception e) { throw new AssertionError(e); }
                    assertThrows(IllegalStateException.class, () ->
                            HttpClientFactory.withInitializationHttpConfig(pending, () -> fail("nested warmup")));
                    throw new AssertionError("synthetic warmup error");
                }));
                assertSame(old, HttpClientFactory.getDefaultHttpClient());
                java.util.concurrent.atomic.AtomicReference<HttpClient> staged = new java.util.concurrent.atomic.AtomicReference<>();
                HttpClientFactory.withInitializationHttpConfig(pending, () -> {
                    staged.set(HttpClientFactory.getDefaultHttpClient());
                    IDaaSCredentialProviderFactory.reset();
                    IDaaSClientConfig next = config();
                    next.setHttpConfiguration(pending);
                    IDaaSCredentialProviderFactory.init(next);
                });
                assertSame(staged.get(), HttpClientFactory.getDefaultHttpClient());
            } finally {
                worker.shutdownNow();
            }
        }

        private static void checkLazyExchangeReset() throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig configuration = config();
            configuration.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            configuration.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            IDaaSCredentialProviderFactory.init(configuration);
            IDaaSMachineTokenExchangeCredentialProvider old =
                    IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
            Field singleton = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
            singleton.setAccessible(true);
            assertNull(singleton.get(null), "No HTTP client has been materialized");
            IDaaSCredentialProviderFactory.reset();
            HttpClient transport = HttpClientFactory.getDefaultHttpClient();
            assertNotNull(transport);
            // Keep the production lazy selection, intercept only the final network request.
            singleton.set(null, (HttpClient) request -> new HttpResponse(200,
                    JSONUtil.toJSONString(token("after-reset", null, 3600))));
            assertEquals("after-reset", old.getCredential("subject",
                    "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken());
        }

        private static Path failedWarmupConfig(Path dir) throws Exception {
            IDaaSClientConfig candidate = config();
            candidate.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            candidate.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            candidate.getHttpConfiguration().setConnectTimeout(2000);
            candidate.getHttpConfiguration().setReadTimeout(3000);
            // A closed loopback listener fails immediately without external network dependency.
            int port;
            try (java.net.ServerSocket socket = new java.net.ServerSocket(0, 1,
                    java.net.InetAddress.getByName("127.0.0.1"))) {
                port = socket.getLocalPort();
            }
            candidate.setTokenEndpoint("https://127.0.0.1:" + port + "/token");
            Path file = dir.resolve("failed-warmup.json");
            Files.write(file, JSONUtil.toJSONString(candidate).getBytes(StandardCharsets.UTF_8));
            return file;
        }

        private static void checkFailedWarmupTransport(Path dir) throws Exception {
            HttpClient old = HttpClientFactory.getDefaultHttpClient();
            com.cloud_idaas.core.config.HttpConfiguration committed =
                    IDaaSCredentialProviderFactory.getHttpConfigSnapshot();
            IDaaSCredentialProviderFactory.reset();
            assertThrows(com.cloud_idaas.core.exception.CacheException.class,
                    () -> IDaaSCredentialProviderFactory.init(failedWarmupConfig(dir).toString()));
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
            assertEquals(committed.getConnectTimeout(), IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getConnectTimeout());
            assertEquals(committed.getReadTimeout(), IDaaSCredentialProviderFactory.getHttpConfigSnapshot().getReadTimeout());
            assertSame(old, HttpClientFactory.getDefaultHttpClient());
            IDaaSCredentialProviderFactory.init(config());
            assertSame(old, HttpClientFactory.getDefaultHttpClient());
        }

        private static void checkFirstFailedWarmup(Path dir) throws Exception {
            assertThrows(com.cloud_idaas.core.exception.CacheException.class,
                    () -> IDaaSCredentialProviderFactory.init(failedWarmupConfig(dir).toString()));
            assertNull(IDaaSCredentialProviderFactory.getHttpConfigSnapshot());
            assertThrows(ConfigException.class, HttpClientFactory::getDefaultHttpClient);
            IDaaSCredentialProviderFactory.init(config());
            assertNotNull(HttpClientFactory.getDefaultHttpClient());
        }

        private static IDaaSClientConfig config() {
            IDaaSClientConfig config = new IDaaSClientConfig();
            config.setIdaasInstanceId("sdk-regression-instance");
            config.setClientId("sdk-regression-client");
            config.setIssuer("https://example.invalid");
            config.setTokenEndpoint("https://example.invalid/token");
            config.setAuthnConfiguration(new IdentityAuthenticationConfiguration());
            return config;
        }

        private static void installHttpClient(HttpClient client) throws Exception {
            Field field = HttpClientFactory.class.getDeclaredField("singletonOKHttpClient");
            field.setAccessible(true);
            field.set(null, client);
            Field config = HttpClientFactory.class.getDeclaredField("httpConfiguration");
            config.setAccessible(true);
            config.set(null, com.cloud_idaas.core.config.HttpConfiguration.copy(IDaaSCredentialProviderFactory.getHttpConfig()));
        }

        private static IDaaSClientConfig generation(int n) {
            IDaaSClientConfig config = config();
            config.setClientId("generation-" + n);
            config.setScope("generation" + n + "|read");
            config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            config.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            return config;
        }

        private static void checkDefaultGeneration() throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSCredentialProviderFactory.init(generation(0));
            installHttpClient(request -> new HttpResponse(200,
                    JSONUtil.toJSONString(token("synthetic-token", null, 3600))));
            java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(true);
            java.util.concurrent.atomic.AtomicReference<Throwable> failure = new java.util.concurrent.atomic.AtomicReference<>();
            java.util.concurrent.CountDownLatch firstRead = new java.util.concurrent.CountDownLatch(1);
            Thread reader = new Thread(() -> {
                while (running.get()) {
                    try {
                        IDaaSMachineCredentialProvider normal = (IDaaSMachineCredentialProvider)
                                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
                        assertEquals("generation" + normal.getClientId().substring("generation-".length())
                                + "|read", normal.getScope());
                        IDaaSMachineTokenExchangeCredentialProvider exchange =
                                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
                        assertEquals("generation" + exchange.getClientId().substring("generation-".length())
                                + "|read", exchange.getScope());
                        assertNotNull(IDaaSCredentialProviderFactory.getClientId());
                        firstRead.countDown();
                    } catch (ConfigException expectedDuringReset) {
                        // A lookup between reset and init must reject the uninitialized state.
                    } catch (Throwable error) {
                        failure.set(error);
                        running.set(false);
                    }
                }
            });
            Thread writer = new Thread(() -> {
                try {
                    await(firstRead);
                    for (int i = 0; i < 10000 && running.get(); i++) {
                        IDaaSCredentialProviderFactory.reset();
                        IDaaSCredentialProviderFactory.init(generation(i % 2));
                    }
                } catch (Throwable error) {
                    failure.set(error);
                } finally {
                    running.set(false);
                }
            });
            reader.setDaemon(true);
            writer.setDaemon(true);
            reader.start();
            writer.start();
            writer.join(15000);
            running.set(false);
            reader.join(5000);
            assertFalse(writer.isAlive());
            assertFalse(reader.isAlive());
            if (failure.get() != null) throw new AssertionError(failure.get());
            assertThrows(ConfigException.class, () ->
                    IDaaSCredentialProviderFactory.getIDaaSCredentialProvider((String) null));
            assertThrows(ConfigException.class, () ->
                    IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider((String) null));
        }

        private static void checkHttpGeneration() throws Exception {
            HttpClientFactory.getDefaultHttpClient();
            Field lockField = HttpClientFactory.class.getDeclaredField("LOCK");
            lockField.setAccessible(true);
            Object lock = lockField.get(null);
            java.util.concurrent.atomic.AtomicReference<HttpClient> lateClient = new java.util.concurrent.atomic.AtomicReference<>();
            java.util.concurrent.atomic.AtomicReference<Throwable> failure = new java.util.concurrent.atomic.AtomicReference<>();
            Thread late = new Thread(() -> {
                try {
                    lateClient.set(HttpClientFactory.getDefaultHttpClient());
                } catch (Throwable error) {
                    failure.set(error);
                }
            });
            late.setDaemon(true);
            HttpClient current;
            synchronized (lock) {
                late.start();
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                while (late.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline) {
                    Thread.yield();
                }
                assertEquals(Thread.State.BLOCKED, late.getState());
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig next = config();
                next.getHttpConfiguration().setConnectTimeout(3000);
                IDaaSCredentialProviderFactory.init(next);
                current = HttpClientFactory.getDefaultHttpClient();
            }
            late.join(5000);
            assertFalse(late.isAlive());
            assertNull(failure.get());
            assertSame(current, lateClient.get());
            assertSame(current, HttpClientFactory.getDefaultHttpClient());
        }

        private static void checkHttpMonitor() throws Exception {
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newSingleThreadExecutor();
            try {
                synchronized (IDaaSCredentialProviderFactory.class) {
                    assertNotNull(pool.submit(HttpClientFactory::getDefaultHttpClient).get(2, TimeUnit.SECONDS));
                }
            } finally {
                pool.shutdownNow();
            }
        }

        private static void await(java.util.concurrent.CountDownLatch latch) {
            try {
                assertTrue(latch.await(5, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError(e);
            }
        }

        private static void checkResetCreation() throws Exception {
            for (ClientDeployEnvironmentEnum environment : new ClientDeployEnvironmentEnum[]{
                    ClientDeployEnvironmentEnum.AWS_EC2, ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS}) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig old = config();
                old.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
                old.getAuthnConfiguration().setClientDeployEnvironment(environment);
                old.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federation");
                IDaaSCredentialProviderFactory.init(old);
                java.util.concurrent.CountDownLatch entered = new java.util.concurrent.CountDownLatch(1);
                java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
                AtomicInteger metadata = new AtomicInteger();
                installHttpClient(request -> {
                    if (!request.getUrl().contains("example.invalid")) {
                        if (metadata.incrementAndGet() == 1) {
                            entered.countDown();
                            await(release);
                        }
                        return new HttpResponse(200, "synthetic-metadata");
                    }
                    return new HttpResponse(200, JSONUtil.toJSONString(token("old-token", null, 3600)));
                });
                java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);
                try {
                    java.util.concurrent.Future<com.cloud_idaas.core.provider.IDaaSCredentialProvider> creator =
                            pool.submit(() -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("probe|read"));
                    await(entered);
                    // Reset must finish while creation is still paused at the first metadata call.
                    pool.submit(IDaaSCredentialProviderFactory::reset).get(2, TimeUnit.SECONDS);
                    IDaaSClientConfig next = config();
                    next.setClientId("new-generation");
                    next.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
                    next.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
                    IDaaSCredentialProviderFactory.init(next);
                    release.countDown();
                    com.cloud_idaas.core.provider.IDaaSCredentialProvider previous = creator.get(5, TimeUnit.SECONDS);
                    assertTrue(metadata.get() >= 2, "Exercise the second production metadata HTTP call");
                    com.cloud_idaas.core.provider.IDaaSCredentialProvider current =
                            IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("probe|read");
                    assertNotSame(previous, current);
                    assertEquals("new-generation", ((IDaaSMachineCredentialProvider) current).getClientId());
                    assertEquals("old-token", previous.getCredential().getAccessToken());
                } finally {
                    release.countDown();
                    pool.shutdownNow();
                }
            }
        }

        private static void checkSharedCreation() throws Exception {
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig config = config();
            config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            config.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            IDaaSCredentialProviderFactory.init(config);
            java.util.concurrent.CountDownLatch entered = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                requests.incrementAndGet();
                entered.countDown();
                await(release);
                return new HttpResponse(200, JSONUtil.toJSONString(token("shared", null, 3600)));
            });
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(3);
            try {
                java.util.concurrent.Future<com.cloud_idaas.core.provider.IDaaSCredentialProvider> first =
                        pool.submit(() -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("Aa|read"));
                await(entered);
                java.util.concurrent.Future<com.cloud_idaas.core.provider.IDaaSCredentialProvider> second =
                        pool.submit(() -> {
                            Thread.currentThread().interrupt();
                            try {
                                com.cloud_idaas.core.provider.IDaaSCredentialProvider provider =
                                        IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("Aa|read");
                                assertTrue(Thread.currentThread().isInterrupted());
                                return provider;
                            } finally {
                                Thread.interrupted();
                            }
                        });
                assertThrows(java.util.concurrent.TimeoutException.class, () -> second.get(100, TimeUnit.MILLISECONDS));
                // These scopes have identical String hashes: even a colliding cache bin must not block IO.
                assertEquals("Aa|read".hashCode(), "BB|read".hashCode());
                installHttpClient(request -> new HttpResponse(200, JSONUtil.toJSONString(token("other", null, 3600))));
                assertNotNull(pool.submit(() -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("BB|read"))
                        .get(2, TimeUnit.SECONDS));
                release.countDown();
                assertSame(first.get(5, TimeUnit.SECONDS), second.get(5, TimeUnit.SECONDS));
                assertEquals(1, requests.get());
                installHttpClient(request -> { throw new ConfigException("simulated failure"); });
                assertThrows(RuntimeException.class, () -> IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("retry|read"));
                installHttpClient(request -> new HttpResponse(200, JSONUtil.toJSONString(token("retry", null, 3600))));
                assertEquals("retry", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider("retry|read").getCredential().getAccessToken());
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }

        private static void checkPkcs7Validation() {
            for (ClientDeployEnvironmentEnum environment : ClientDeployEnvironmentEnum.values()) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig config = config();
                config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.PKCS7);
                config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federation");
                config.getAuthnConfiguration().setClientDeployEnvironment(environment);
                if (environment == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS
                        || environment == ClientDeployEnvironmentEnum.AWS_EC2) {
                    IDaaSCredentialProviderFactory.init(config);
                } else {
                    ConfigException exception = assertThrows(ConfigException.class,
                            () -> IDaaSCredentialProviderFactory.init(config));
                    assertEquals(com.cloud_idaas.core.domain.constants.ErrorCode.UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT.getCode(),
                            exception.getErrorCode());
                    assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                }
            }
        }

        private static void checkHttpReset() throws Exception {
            Class<?>[] owners = {com.cloud_idaas.core.http.OAuth2TokenUtil.class,
                    com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider.class,
                    com.cloud_idaas.core.implementation.authentication.pkcs7.AwsEc2Pkcs7AttestedDocumentProvider.class,
                    com.cloud_idaas.core.implementation.authentication.pkcs7.AlibabaCloudEcsAttestedDocumentProvider.class};
            HttpClient first = HttpClientFactory.getDefaultHttpClient();
            HttpClient[] delegates = new HttpClient[owners.length];
            for (int i = 0; i < owners.length; i++) {
                Field field = owners[i].getDeclaredField("HTTP_CLIENT");
                field.setAccessible(true);
                delegates[i] = (HttpClient) field.get(null);
            }
            IDaaSCredentialProviderFactory.reset();
            assertSame(first, HttpClientFactory.getDefaultHttpClient());
            IDaaSClientConfig next = config();
            next.getHttpConfiguration().setConnectTimeout(3000);
            next.getHttpConfiguration().setReadTimeout(4000);
            IDaaSCredentialProviderFactory.init(next);
            HttpClient second = HttpClientFactory.getDefaultHttpClient();
            assertNotSame(first, second);
            Field transport = second.getClass().getDeclaredField("okHttpClient");
            transport.setAccessible(true);
            okhttp3.OkHttpClient ok = (okhttp3.OkHttpClient) transport.get(second);
            assertEquals(3000, ok.connectTimeoutMillis());
            assertEquals(4000, ok.readTimeoutMillis());
            assertSame(second, HttpClientFactory.getDefaultHttpClient());
            AtomicInteger calls = new AtomicInteger();
            installHttpClient(request -> {
                calls.incrementAndGet();
                return new HttpResponse(200, "updated-client");
            });
            com.cloud_idaas.core.http.HttpRequest request = new com.cloud_idaas.core.http.HttpRequest.Builder()
                    .url("https://example.invalid/token").httpMethod(com.cloud_idaas.core.http.HttpMethod.GET).build();
            for (HttpClient delegate : delegates) {
                assertEquals("updated-client", delegate.send(request).getBody());
            }
            assertEquals(4, calls.get());
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig defaults = config();
            defaults.setHttpConfiguration(null);
            IDaaSCredentialProviderFactory.init(defaults);
            HttpClient defaultClient = HttpClientFactory.getDefaultHttpClient();
            okhttp3.OkHttpClient defaultTransport = (okhttp3.OkHttpClient) transport.get(defaultClient);
            assertEquals(5000, defaultTransport.connectTimeoutMillis());
            assertEquals(10000, defaultTransport.readTimeoutMillis());
        }

        private static IDaaSTokenResponse token(String accessToken, String refreshToken, long lifetime) {
            IDaaSTokenResponse token = new IDaaSTokenResponse();
            token.setAccessToken(accessToken);
            token.setIdToken("test-id-token");
            token.setRefreshToken(refreshToken);
            token.setTokenType("Bearer");
            token.setExpiresIn(3600);
            token.setExpiresAt(Instant.now().getEpochSecond() + lifetime);
            return token;
        }

        private static void checkPaths(Path dir) throws Exception {
            String jwt = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8))
                    + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(
                    ("{\"exp\":" + (Instant.now().getEpochSecond() + 3600) + "}").getBytes(StandardCharsets.UTF_8)) + ".";
            for (String name : new String[]{"explicit.jwt", "custom.jwt", "platform.jwt"}) {
                Files.write(dir.resolve(name), jwt.getBytes(StandardCharsets.UTF_8));
            }
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                assertEquals(jwt, request.getFormBody().get("client_assertion").get(0));
                requests.incrementAndGet();
                return new HttpResponse(200, JSONUtil.toJSONString(token("machine-token", null, 3600)));
            });
            for (ClientDeployEnvironmentEnum env : new ClientDeployEnvironmentEnum[]{
                    ClientDeployEnvironmentEnum.AWS_EKS,
                    ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK, ClientDeployEnvironmentEnum.KUBERNETES}) {
                for (int mode = 0; mode < (env == ClientDeployEnvironmentEnum.KUBERNETES ? 2 : 3); mode++) {
                    IDaaSCredentialProviderFactory.reset();
                    IDaaSClientConfig config = config();
                    IdentityAuthenticationConfiguration auth = config.getAuthnConfiguration();
                    auth.setAuthnMethod(TokenAuthnMethod.OIDC);
                    auth.setClientDeployEnvironment(env);
                    auth.setApplicationFederatedCredentialName("test-federation");
                    auth.setOidcTokenFilePath(mode == 0 ? dir.resolve("explicit.jwt").toString() : null);
                    auth.setOidcTokenFilePathEnvVarName(mode < 2 ? "SDK_TEST_OIDC_FILE" : null);
                    String expectedPath = dir.resolve(mode == 0 ? "explicit.jwt" : mode == 1 ? "custom.jwt" : "platform.jwt").toString();
                    IDaaSCredentialProviderFactory.init(config);
                    IDaaSMachineCredentialProvider machine = (IDaaSMachineCredentialProvider)
                            IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
                    IDaaSMachineTokenExchangeCredentialProvider exchange =
                            IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();
                    assertEquals(expectedPath, ((FileOidcTokenProvider) machine.getOidcTokenProvider()).getOidcTokenFilePath());
                    assertEquals(expectedPath, ((FileOidcTokenProvider) exchange.getOidcTokenProvider()).getOidcTokenFilePath());
                    String expectedType = env == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK
                            ? "AlibabaCloudAckOidcTokenProvider" : env == ClientDeployEnvironmentEnum.KUBERNETES
                            ? "KubernetesOidcTokenProvider" : "AwsEksOidcTokenProvider";
                    assertEquals(expectedType, machine.getOidcTokenProvider().getClass().getSimpleName());
                    assertEquals(expectedType, exchange.getOidcTokenProvider().getClass().getSimpleName());
                    assertEquals("machine-token", exchange.getCredential("subject", "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken());
                    IDaaSCredentialProviderFactory.reset();
                    assertEquals(jwt, exchange.getOidcTokenProvider().getOidcToken());
                }
                if (env == ClientDeployEnvironmentEnum.KUBERNETES) continue;
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig bad = config();
                bad.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
                bad.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federation");
                bad.getAuthnConfiguration().setClientDeployEnvironment(env);
                bad.getAuthnConfiguration().setOidcTokenFilePathEnvVarName("SDK_TEST_MISSING_PATH");
                IDaaSCredentialProviderFactory.init(bad);
                assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getIDaaSCredentialProvider);
                assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getIDaaSTokenExchangeCredentialProvider);
            }
            assertEquals(16, requests.get());
            assertEquals(ClientDeployEnvironmentEnum.AWS_EKS, JSONUtil.parseObject("\"AWS_EKS\"", ClientDeployEnvironmentEnum.class));
        }

        private static void checkInitialization(Path dir) throws Exception {
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                if (requests.incrementAndGet() == 1) {
                    throw new ConfigException("simulated token endpoint failure");
                }
                return new HttpResponse(200, JSONUtil.toJSONString(token("retried-token", null, 3600)));
            });
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig config = config();
            config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
            config.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
            Path path = dir.resolve("config.json");
            Files.write(path, JSONUtil.toJSONBytes(config));
            System.setProperty(ConfigPathConstants.JVM_CONFIG_PATH_KEY, path.toString());
            com.cloud_idaas.core.exception.CacheException failure = assertThrows(
                    com.cloud_idaas.core.exception.CacheException.class, IDaaSCredentialProviderFactory::init);
            assertTrue(failure.getCause() instanceof ConfigException);
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getIDaaSCredentialProvider);
            config.setClientId("retried-client");
            Files.write(path, JSONUtil.toJSONBytes(config));
            System.setProperty(ConfigPathConstants.JVM_CONFIG_PATH_KEY, dir.resolve("wrong-priority.json").toString());
            IDaaSCredentialProviderFactory.init(path.toString());
            assertEquals("retried-client", IDaaSCredentialProviderFactory.getClientId());
            assertEquals("retried-token", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
            assertDoesNotThrow(() -> IDaaSCredentialProviderFactory.init(dir.resolve("missing-after-init.json").toString()));
            assertEquals(2, requests.get());
            IDaaSCredentialProviderFactory.reset();
            IDaaSClientConfig invalid = config();
            invalid.setClientId(null);
            assertThrows(ConfigException.class, () -> IDaaSCredentialProviderFactory.init(invalid));
            assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
            IDaaSCredentialProviderFactory.init(config());
            assertEquals("sdk-regression-client", IDaaSCredentialProviderFactory.getClientId());
        }

        private static void checkConcurrentInitialization(Path dir) throws Exception {
            for (boolean fail : new boolean[]{true, false}) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSCredentialProviderFactory.init(config());
                java.util.concurrent.CountDownLatch entered = new java.util.concurrent.CountDownLatch(1);
                java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
                java.util.concurrent.CountDownLatch readersStarted = new java.util.concurrent.CountDownLatch(2);
                installHttpClient(request -> {
                    entered.countDown();
                    try {
                        if (!release.await(5, TimeUnit.SECONDS)) throw new AssertionError("initializer not released");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    }
                    if (fail) throw new ConfigException("synthetic initialization failure");
                    return new HttpResponse(200, JSONUtil.toJSONString(token("ready", null, 3600)));
                });
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig config = config();
                config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.CLIENT_SECRET_POST);
                config.getAuthnConfiguration().setClientSecretEnvVarName("SDK_TEST_SECRET");
                Path file = dir.resolve("concurrent-init.json");
                Files.write(file, JSONUtil.toJSONBytes(config));
                java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(3);
                try {
                    java.util.concurrent.Future<?> init = executor.submit(() -> IDaaSCredentialProviderFactory.init(file.toString()));
                    assertTrue(entered.await(5, TimeUnit.SECONDS));
                    java.util.concurrent.Future<String> clientId = executor.submit(() -> {
                        readersStarted.countDown();
                        return IDaaSCredentialProviderFactory.getClientId();
                    });
                    java.util.concurrent.Future<?> provider = executor.submit(() -> {
                        readersStarted.countDown();
                        return IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
                    });
                    assertTrue(readersStarted.await(5, TimeUnit.SECONDS));
                    assertEquals("sdk-regression-client", clientId.get(2, TimeUnit.SECONDS));
                    assertThrows(java.util.concurrent.TimeoutException.class, () -> provider.get(100, TimeUnit.MILLISECONDS));
                    release.countDown();
                    if (fail) {
                        assertThrows(java.util.concurrent.ExecutionException.class, () -> init.get(5, TimeUnit.SECONDS));
                        assertThrows(ConfigException.class, IDaaSCredentialProviderFactory::getClientId);
                        assertTrue(assertThrows(java.util.concurrent.ExecutionException.class,
                                () -> provider.get(5, TimeUnit.SECONDS)).getCause() instanceof ConfigException);
                    } else {
                        init.get(5, TimeUnit.SECONDS);
                        assertEquals("sdk-regression-client", clientId.get(5, TimeUnit.SECONDS));
                        assertNotNull(provider.get(5, TimeUnit.SECONDS));
                    }
                } finally {
                    release.countDown();
                    executor.shutdownNow();
                }
            }
        }

        private static void checkDefaults() {
            for (ClientDeployEnvironmentEnum env : new ClientDeployEnvironmentEnum[]{
                    ClientDeployEnvironmentEnum.AWS_EKS,
                    ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK, ClientDeployEnvironmentEnum.KUBERNETES}) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig config = config();
                config.getAuthnConfiguration().setAuthnMethod(TokenAuthnMethod.OIDC);
                config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federation");
                config.getAuthnConfiguration().setClientDeployEnvironment(env);
                if (env == ClientDeployEnvironmentEnum.KUBERNETES) {
                    config.getAuthnConfiguration().setOidcTokenFilePathEnvVarName("SDK_TEST_MISSING_PATH");
                }
                IDaaSCredentialProviderFactory.init(config);
                FileOidcTokenProvider provider = (FileOidcTokenProvider)
                        IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider().getOidcTokenProvider();
                String expected = env == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK
                        ? "/var/run/secrets/ack.alibabacloud.com/rrsa-tokens/token"
                        : env == ClientDeployEnvironmentEnum.KUBERNETES
                        ? "/var/run/secrets/kubernetes.io/serviceaccount/token"
                        : "/var/run/secrets/eks.amazonaws.com/serviceaccount/token";
                assertEquals(expected, provider.getOidcTokenFilePath());
                String expectedType = env == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK
                        ? "AlibabaCloudAckOidcTokenProvider" : env == ClientDeployEnvironmentEnum.KUBERNETES
                        ? "KubernetesOidcTokenProvider" : "AwsEksOidcTokenProvider";
                assertEquals(expectedType, provider.getClass().getSimpleName());
            }
        }

        private static void checkPrivateKey() throws Exception {
            java.security.KeyPairGenerator generator = java.security.KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            java.security.KeyPair key = generator.generateKeyPair();
            String pem = "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(key.getPrivate().getEncoded())
                    + "\n-----END PRIVATE KEY-----";
            System.setProperty("SDK_TEST_PRIVATE_KEY", pem);
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                try {
                    org.jose4j.jws.JsonWebSignature jws = new org.jose4j.jws.JsonWebSignature();
                    jws.setCompactSerialization(request.getFormBody().get("client_assertion").get(0));
                    jws.setKey(key.getPublic());
                    assertTrue(jws.verifySignature());
                    assertEquals("RS256", jws.getAlgorithmHeaderValue());
                } catch (org.jose4j.lang.JoseException e) {
                    throw new AssertionError(e);
                }
                requests.incrementAndGet();
                return new HttpResponse(200, JSONUtil.toJSONString(token("signed-token", null, 3600)));
            });
            for (TokenAuthnMethod method : new TokenAuthnMethod[]{TokenAuthnMethod.PRIVATE_KEY_JWT, TokenAuthnMethod.PCA}) {
                IDaaSCredentialProviderFactory.reset();
                IDaaSClientConfig config = config();
                config.getAuthnConfiguration().setAuthnMethod(method);
                config.getAuthnConfiguration().setPrivateKeyEnvVarName("SDK_TEST_PRIVATE_KEY");
                config.getAuthnConfiguration().setApplicationFederatedCredentialName("test-federation");
                config.getAuthnConfiguration().setClientX509Certificate("test-certificate");
                config.getAuthnConfiguration().setX509CertChains("test-chain");
                IDaaSCredentialProviderFactory.init(config);
                assertEquals("signed-token", IDaaSCredentialProviderFactory.getIDaaSCredentialProvider().getCredential().getAccessToken());
                assertEquals("signed-token", IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider()
                        .getCredential("subject", "urn:ietf:params:oauth:token-type:access_token", null).getAccessToken());
            }
            assertEquals(4, requests.get());
        }

        private static void checkEmptyHumanRefresh(Path dir) throws Exception {
            Files.write(dir.resolve("human.json"), JSONUtil.toJSONBytes(token("cached", "refresh", 3600)));
            installHttpClient(request -> {
                assertEquals("refresh_token", request.getFormBody().get("grant_type").get(0));
                return new HttpResponse(200, "");
            });
            HumanFederatedOidcTokenProvider provider = HumanFederatedOidcTokenProvider.builder()
                    .clientId("human-client").tokenEndpoint("https://example.invalid/token")
                    .deviceAuthorizationEndpoint("https://example.invalid/device").build();
            com.cloud_idaas.core.exception.CredentialException failure = assertThrows(
                    com.cloud_idaas.core.exception.CredentialException.class, provider::refreshCredential);
            assertEquals("EmptyRefreshTokenResponse", failure.getErrorCode());
        }

        private static void checkHumanCache(Path dir, boolean expiring) throws Exception {
            Path cache = dir.resolve("human.json");
            Files.write(cache, JSONUtil.toJSONBytes(token("cached-access", "old-refresh", expiring ? 1 : 3600)));
            AtomicInteger requests = new AtomicInteger();
            installHttpClient(request -> {
                assertEquals("refresh_token", request.getFormBody().get("grant_type").get(0));
                int count = requests.incrementAndGet();
                assertEquals(count <= 2 ? "old-refresh" : "rotated-refresh", request.getFormBody().get("refresh_token").get(0));
                return new HttpResponse(200, JSONUtil.toJSONString(token("access-" + count,
                        count == 2 ? "rotated-refresh" : null, 3600)));
            });
            HumanFederatedOidcTokenProvider provider = HumanFederatedOidcTokenProvider.builder()
                    .clientId("human-client").tokenEndpoint("https://example.invalid/token")
                    .deviceAuthorizationEndpoint("https://example.invalid/device").build();
            assertEquals(expiring ? 1 : 0, requests.get());
            if (!expiring) {
                assertEquals("cached-access", provider.getOidcToken());
                assertEquals("old-refresh", provider.refreshCredential().getValue().getRefreshToken());
            }
            assertEquals("rotated-refresh", provider.refreshCredential().getValue().getRefreshToken());
            assertEquals("rotated-refresh", provider.refreshCredential().getValue().getRefreshToken());
            assertEquals(3, requests.get());
            IDaaSTokenResponse saved = JSONUtil.parseObject(new String(Files.readAllBytes(cache), StandardCharsets.UTF_8), IDaaSTokenResponse.class);
            assertEquals("rotated-refresh", saved.getRefreshToken());
            assertEquals("access-3", saved.getAccessToken());
        }
    }
}
