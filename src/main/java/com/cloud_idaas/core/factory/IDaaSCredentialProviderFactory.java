package com.cloud_idaas.core.factory;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.http.HttpClientFactory;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.*;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.implementation.IDaaSMachineCredentialProvider;
import com.cloud_idaas.core.implementation.IDaaSMachineTokenExchangeCredentialProvider;
import com.cloud_idaas.core.implementation.authentication.jwt.StaticClientSecretAssertionProvider;
import com.cloud_idaas.core.implementation.authentication.jwt.StaticPrivateKeyAssertionProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.KubernetesOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.AwsEksOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.AlibabaCloudAckOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.GcpVmOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.HumanFederatedOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.pkcs7.AlibabaCloudEcsAttestedDocumentProvider;
import com.cloud_idaas.core.implementation.authentication.pkcs7.AwsEc2Pkcs7AttestedDocumentProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CountDownLatch;

public class IDaaSCredentialProviderFactory {

    private static final IDaaSClientConfig IDAAS_CLIENT_CONFIG = new IDaaSClientConfig();
    // Independently published configuration reads remain available to plugin worker threads during warmup.
    private static volatile IDaaSClientConfig CONFIG_READ_SNAPSHOT;
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    /**
     * When developer run the application, the credential provider will be initialized
     */
    private static OidcTokenProvider HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER;

    /**
     * It will cache all credential providers, based by scope.
     */
    private static ConcurrentMap<String, IDaaSCredentialProvider> CREDENTIAL_PROVIDERS = new ConcurrentHashMap<>();

    private static ConcurrentMap<String, IDaaSMachineTokenExchangeCredentialProvider> TOKEN_EXCHANGE_CREDENTIAL_PROVIDERS = new ConcurrentHashMap<>();

    // Last successfully initialized transport configuration, retained across reset for old providers.
    private static volatile HttpConfiguration HTTP_CONFIG_SNAPSHOT;
    // Task registration uses the factory monitor; execution, waiting and failure removal do not.
    private static ConcurrentMap<String, FutureTask<IDaaSCredentialProvider>> CREDENTIAL_CREATIONS = new ConcurrentHashMap<>();

    private static final ThreadLocal<java.util.Set<FutureTask<IDaaSCredentialProvider>>> ACTIVE_CREATIONS = new ThreadLocal<>();

    private static PluginInitialization PLUGIN_INITIALIZATION; // Guarded by the factory monitor.

    private static final Logger LOGGER = LoggerFactory.getLogger(IDaaSCredentialProviderFactory.class);

    /**
     * Initialize with the config file resolved from the JVM parameter, the environment variable or the default path.
     *
     * <p>Repeated invocations are ignored. Call {@link #reset()} first to re-initialize with another configuration.
     */
    public static void init() {
        init((String) null);
    }

    /**
     * Initialize with the config file located at the specified path.
     *
     * <p>Repeated invocations are ignored. Call {@link #reset()} first to re-initialize with another configuration.
     *
     * @param configPath config file path. When null or blank, falls back to the JVM parameter,
     *                   the environment variable and then the default path
     */
    public static void init(String configPath) {
        for (;;) {
            PluginInitialization waiting;
            PluginInitialization plugin = null;
            synchronized (IDaaSCredentialProviderFactory.class) {
                waiting = pendingInitializationForOtherThread();
                if (waiting == null) {
                    if (INITIALIZED.get()) {
                        LOGGER.info("IDaaS Credential Provider Factory has been initialized.");
                        return;
                    }
                    final String content = ConfigReader.getConfigAsString(configPath);
                    try {
                        IDAAS_CLIENT_CONFIG.assign(parseClientConfig(content));
                        validateConfig(IDAAS_CLIENT_CONFIG);
                        normalizeConfig(IDAAS_CLIENT_CONFIG);
                        publishConfigReadSnapshot();
                        INITIALIZED.set(true);
                    } catch (Exception e) {
                        reset();
                        String message = "IDaaS Credential Provider Factory init failed.";
                        if (e instanceof ConfigException
                                && ("RemovedClientDeployEnvironment".equals(((ConfigException) e).getErrorCode())
                                || "InvalidConfigContent".equals(((ConfigException) e).getErrorCode())
                                || "InvalidAuthenticationIdentity".equals(((ConfigException) e).getErrorCode())
                                || ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode().equals(((ConfigException) e).getErrorCode())
                                || ErrorCode.UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT.getCode().equals(((ConfigException) e).getErrorCode()))) {
                            message += " " + e.getMessage();
                        }
                        throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), message, e);
                    }
                    final Object generation = CREDENTIAL_PROVIDERS;
                    final HttpConfiguration pending = IDAAS_CLIENT_CONFIG.getHttpConfiguration() == null
                            ? new HttpConfiguration() : HttpConfiguration.copy(IDAAS_CLIENT_CONFIG.getHttpConfiguration());
                    try {
                        if (IDAAS_CLIENT_CONFIG.getAuthnConfiguration().getAuthnMethod() != TokenAuthnMethod.PLUGIN) {
                            HttpClientFactory.withInitializationHttpConfig(pending, () -> {
                                initCredentialProvider();
                                commitHttpConfiguration(generation, pending);
                            });
                            return;
                        }
                        // Human login keeps its existing synchronous initialization. Only the plugin
                        // callback is moved outside the monitor, after its configuration/task are captured.
                        HttpClientFactory.withInitializationHttpConfig(pending,
                                IDaaSCredentialProviderFactory::initHumanCredentialProvider);
                        plugin = new PluginInitialization(generation, pending,
                                prepareCredentialCreation(null, true));
                        HttpClientFactory.beginPluginInitialization(generation, pending);
                        PLUGIN_INITIALIZATION = plugin;
                    } catch (RuntimeException | Error e) {
                        if (generation == CREDENTIAL_PROVIDERS) reset();
                        throw e;
                    }
                }
            }
            if (waiting != null) {
                waiting.awaitCompletion();
                continue;
            }
            warmPlugin(plugin);
            return;
        }
    }

    private static void commitHttpConfiguration(Object generation, HttpConfiguration pending) {
        if (!INITIALIZED.get() || generation != CREDENTIAL_PROVIDERS) {
            throw new IllegalStateException("Factory initialization was superseded");
        }
        HTTP_CONFIG_SNAPSHOT = HttpConfiguration.copy(pending);
    }

    private static PluginInitialization pendingInitializationForOtherThread() {
        if (PLUGIN_INITIALIZATION == null || PLUGIN_INITIALIZATION.owner == Thread.currentThread()) return null;
        java.util.Set<FutureTask<IDaaSCredentialProvider>> active = ACTIVE_CREATIONS.get();
        // A concurrent first reader may run the shared startup task before the init caller does.
        // A reentrant init from that callback must retain the existing ignored-init behavior.
        return active != null && active.contains(PLUGIN_INITIALIZATION.creation.task) ? null : PLUGIN_INITIALIZATION;
    }

    private static void warmPlugin(PluginInitialization initialization) {
        try {
            HttpClientFactory.withInitializationHttpConfig(initialization.httpConfiguration, () -> {
                initialization.creation.get();
                synchronized (IDaaSCredentialProviderFactory.class) {
                    commitHttpConfiguration(initialization.generation, initialization.httpConfiguration);
                }
            });
        } catch (RuntimeException | Error e) {
            synchronized (IDaaSCredentialProviderFactory.class) {
                if (initialization.generation == CREDENTIAL_PROVIDERS) reset();
            }
            throw e;
        } finally {
            synchronized (IDaaSCredentialProviderFactory.class) {
                if (PLUGIN_INITIALIZATION == initialization) PLUGIN_INITIALIZATION = null;
                HttpClientFactory.endPluginInitialization(initialization.generation);
                initialization.finished.countDown();
            }
        }
    }

    private static final class PluginInitialization {
        private final Thread owner = Thread.currentThread();
        private final CountDownLatch finished = new CountDownLatch(1);
        private final Object generation;
        private final HttpConfiguration httpConfiguration;
        private final CredentialCreation creation;

        private PluginInitialization(Object generation, HttpConfiguration configuration, CredentialCreation creation) {
            this.generation = generation;
            this.httpConfiguration = configuration;
            this.creation = creation;
        }

        private void awaitCompletion() {
            boolean interrupted = false;
            try {
                for (;;) {
                    try {
                        finished.await();
                        return;
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            } finally {
                if (interrupted) Thread.currentThread().interrupt();
            }
        }
    }

    private static IDaaSClientConfig parseClientConfig(String content) {
        // Diagnose enum mistakes at the factory boundary; never normalize values or
        // change generic Gson deserialization or irrelevant environment-field behavior.
        if (StringUtil.isBlank(content)) {
            throw new ConfigException("InvalidConfigContent", "IDaaS configuration must be a non-empty JSON object.");
        }
        com.google.gson.JsonElement document = JSONUtil.parseObject(content, com.google.gson.JsonElement.class);
        if (document == null || !document.isJsonObject()) {
            throw new ConfigException("InvalidConfigContent", "IDaaS configuration must be a non-empty JSON object.");
        }
        com.google.gson.JsonObject root = document.getAsJsonObject();
        com.google.gson.JsonElement authentication = root.get("authnConfiguration");
        if (authentication != null && authentication.isJsonObject()) {
            com.google.gson.JsonObject auth = authentication.getAsJsonObject();
            validateEnumInput(auth, "authnMethod", TokenAuthnMethod.class,
                    ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(), true);
            validateEnumInput(auth, "identityType", AuthenticationIdentityEnum.class,
                    "InvalidAuthenticationIdentity", true);
            com.google.gson.JsonElement environment = auth.get("clientDeployEnvironment");
            if (environment != null && environment.isJsonPrimitive()
                    && environment.getAsJsonPrimitive().isString()
                    && "AWS_ESK".equalsIgnoreCase(environment.getAsString().trim())) {
                throw new ConfigException("RemovedClientDeployEnvironment",
                        "clientDeployEnvironment 'AWS_ESK' was removed; use 'AWS_EKS'. See CHANGELOG for migration details.");
            }
            com.google.gson.JsonElement method = auth.get("authnMethod");
            if (method != null && ("PKCS7".equals(method.getAsString()) || "OIDC".equals(method.getAsString()))) {
                validateEnumInput(auth, "clientDeployEnvironment", ClientDeployEnvironmentEnum.class,
                        ErrorCode.UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT.getCode(), false);
            }
        }
        return JSONUtil.parseObject(content, IDaaSClientConfig.class);
    }

    private static <E extends Enum<E>> void validateEnumInput(com.google.gson.JsonObject auth, String field,
                                                              Class<E> type, String errorCode, boolean rejectUnknown) {
        if (!auth.has(field)) return; // Preserve defaults when a field is omitted.
        com.google.gson.JsonElement input = auth.get(field);
        String suggestion = null;
        if (input != null && input.isJsonPrimitive() && input.getAsJsonPrimitive().isString()) {
            String value = input.getAsString();
            for (E candidate : type.getEnumConstants()) {
                if (candidate.name().equals(value)) return;
                if (candidate.name().equalsIgnoreCase(value.trim())) suggestion = candidate.name();
            }
        }
        if (rejectUnknown || suggestion != null) {
            // Only enum names from SDK constants may enter the message, never raw configuration values.
            throw new ConfigException(errorCode, "Invalid " + field + "; expected an exact enum name."
                    + (suggestion == null ? "" : " Did you mean '" + suggestion + "'?"));
        }
    }

    /**
     * Provider registration captures one configuration generation under this monitor.
     * Non-plugin file warm-up keeps its readiness barrier; plugin warm-up runs outside
     * the monitor so existing acyclic cross-thread plugin delegation can proceed.
     */
    private static synchronized void ensureInitialized() {
        if (!INITIALIZED.get()) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(),
                    "IDaaS Credential Provider Factory has not been initialized.");
        }
    }

    private static String resolveGcpAudience() {
        String issuer = IDAAS_CLIENT_CONFIG.getIssuer();
        if (StringUtil.isBlank(issuer)) {
            throw new ConfigException(ErrorCode.ISSUER_ENDPOINT_NOT_FOUND.getCode(),
                    "issuer is required as audience source for GCP VM OIDC authentication");
        }
        try {
            URI uri = new URI(issuer);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new URISyntaxException(issuer, "Absolute URI with host required");
            }
            return uri.getScheme() + "://" + uri.getHost()
                    + (uri.getPort() == -1 ? "" : ":" + uri.getPort());
        } catch (URISyntaxException e) {
            throw new ConfigException("InvalidEndpointFormat",
                    "Cannot extract origin from issuer for GCP VM OIDC audience", e);
        }
    }

    private static void initCredentialProvider() {
        if (!INITIALIZED.get()) {
            return;
        }

        initHumanCredentialProvider();
        getIDaaSCredentialProvider(IDAAS_CLIENT_CONFIG.getScope());
        if (IDAAS_CLIENT_CONFIG.getAuthnConfiguration().getAuthnMethod() != TokenAuthnMethod.PLUGIN) {
            getIDaaSTokenExchangeCredentialProvider(IDAAS_CLIENT_CONFIG.getScope());
        }
    }

    private static void initHumanCredentialProvider() {
        // If the identity type is human, trigger login in the Init method, so that authentication is completed when the application starts.
        IdentityAuthenticationConfiguration authenticationConfiguration = IDAAS_CLIENT_CONFIG.getAuthnConfiguration();
        if (authenticationConfiguration.getIdentityType() == AuthenticationIdentityEnum.HUMAN) {
            HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER = HumanFederatedOidcTokenProvider.builder()
                    .clientId(authenticationConfiguration.getHumanAuthenticateClientId())
                    .tokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint())
                    .deviceAuthorizationEndpoint(IDAAS_CLIENT_CONFIG.getDeviceAuthorizationEndpoint())
                    .build();

            // Support local refresh token refresh later to achieve 30-day single login per device for users
            HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER.getOidcToken();
        }
    }

    /**
     * Initialize with the configuration passed in at runtime.
     *
     * <p>Repeated invocations are ignored. Call {@link #reset()} first to re-initialize with another configuration.
     *
     * @param authenticationConfig the client configuration to initialize with
     */
    public static void init(IDaaSClientConfig authenticationConfig) {
        for (;;) {
            PluginInitialization waiting;
            synchronized (IDaaSCredentialProviderFactory.class) {
                waiting = pendingInitializationForOtherThread();
                if (waiting == null) {
                    initConfiguration(authenticationConfig);
                    return;
                }
            }
            waiting.awaitCompletion();
        }
    }

    private static void initConfiguration(IDaaSClientConfig authenticationConfig) {
        if (INITIALIZED.get()) {
            LOGGER.info("IDaaS Credential Provider Factory has been initialized.");
            return;
        }

        try {
            IDAAS_CLIENT_CONFIG.assign(authenticationConfig);
            validateConfig(IDAAS_CLIENT_CONFIG);
            normalizeConfig(IDAAS_CLIENT_CONFIG);
            HTTP_CONFIG_SNAPSHOT = IDAAS_CLIENT_CONFIG.getHttpConfiguration() == null
                    ? new HttpConfiguration() : HttpConfiguration.copy(IDAAS_CLIENT_CONFIG.getHttpConfiguration());
            publishConfigReadSnapshot();
            INITIALIZED.set(true);
        } catch (RuntimeException e) {
            reset();
            throw e;
        }
    }

    private static String resolveCloudOidcTokenFilePath(IdentityAuthenticationConfiguration config,
                                                        String platformEnvName, String defaultPath) {
        if (StringUtil.isNotBlank(config.getOidcTokenFilePath())) {
            return config.getOidcTokenFilePath();
        }
        String customEnvName = config.getOidcTokenFilePathEnvVarName();
        if (StringUtil.isNotBlank(customEnvName)) {
            String path = System.getenv(customEnvName);
            ValidatorUtil.validateConfigNotBlank(path, ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(),
                    "OIDC token file path environment variable is missing or empty: " + customEnvName);
            return path;
        }
        String platformPath = StringUtil.isBlank(platformEnvName) ? null : System.getenv(platformEnvName);
        String path = StringUtil.defaultIfBlank(platformPath, defaultPath);
        ValidatorUtil.validateConfigNotBlank(path, ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), "An explicit OIDC token file path is required");
        return path;
    }

    /**
     * Reset the factory back to the uninitialized state, so that a subsequent {@code init} call can
     * apply another configuration.
     *
     * <p>The cached configuration and all cached credential providers are discarded, which means the
     * providers are rebuilt from the new configuration on the next {@code get} call. Provider instances
     * already handed out to callers keep their authentication settings (such as client ID, scope and endpoints),
     * so callers must obtain providers again to adopt a new identity configuration. SDK-managed HTTP requests
     * use the most recently successfully initialized global transport settings, including after re-initialization;
     * existing raw HTTP client references and in-flight requests are not closed.
     */
    public synchronized static void reset() {
        if (PLUGIN_INITIALIZATION != null) {
            PluginInitialization previous = PLUGIN_INITIALIZATION;
            PLUGIN_INITIALIZATION = null;
            HttpClientFactory.endPluginInitialization(previous.generation);
            previous.finished.countDown();
        }
        CONFIG_READ_SNAPSHOT = null;
        INITIALIZED.set(false);
        IDAAS_CLIENT_CONFIG.assign(new IDaaSClientConfig());
        // Detach the generation, so an in-flight creation cannot repopulate the new cache.
        CREDENTIAL_PROVIDERS = new ConcurrentHashMap<>();
        TOKEN_EXCHANGE_CREDENTIAL_PROVIDERS = new ConcurrentHashMap<>();
        CREDENTIAL_CREATIONS = new ConcurrentHashMap<>();
        HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER = null;
    }

    private static void validateConfig(IDaaSClientConfig clientConfig) {
        ValidatorUtil.validateBaseConfig(clientConfig);
        ValidatorUtil.validateConfigNotNull(clientConfig.getAuthnConfiguration().getAuthnMethod(),
                ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(), "Invalid authnMethod; expected an exact enum name.");
        ValidatorUtil.validateConfigNotNull(clientConfig.getAuthnConfiguration().getIdentityType(),
                "InvalidAuthenticationIdentity", "Invalid identityType; expected HUMAN or CLIENT.");
        if (clientConfig.getAuthnConfiguration().getIdentityType() == AuthenticationIdentityEnum.HUMAN) {
            ValidatorUtil.validateHumanConfig(clientConfig);
        }
        ValidatorUtil.validateClientConfig(clientConfig);
        ValidatorUtil.validateHttpConfig(clientConfig.getHttpConfiguration());
    }

    private static void normalizeConfig(IDaaSClientConfig clientConfig) {
        NormalizeUtil.normalizeEndpoints(clientConfig);
    }

    public static IDaaSCredentialProvider getIDaaSCredentialProvider() {
        return getCredentialProvider(null, true);
    }

    public static IDaaSCredentialProvider getIDaaSCredentialProvider(String scope) {
        return getCredentialProvider(scope, false);
    }

    private static IDaaSCredentialProvider getCredentialProvider(String requestedScope, boolean useDefaultScope) {
        return prepareCredentialCreation(requestedScope, useDefaultScope).get();
    }

    private static CredentialCreation prepareCredentialCreation(String requestedScope, boolean useDefaultScope) {
        final String scope;
        final FutureTask<IDaaSCredentialProvider> task;
        final ConcurrentMap<String, FutureTask<IDaaSCredentialProvider>> creations;
        synchronized (IDaaSCredentialProviderFactory.class) {
            ensureInitialized();
            scope = useDefaultScope ? IDAAS_CLIENT_CONFIG.getScope() : requestedScope;
            ScopeUtil.validateScope(scope);
            IDaaSCredentialProvider cached = CREDENTIAL_PROVIDERS.get(scope);
            if (cached != null) {
                return new CredentialCreation(cached);
            }
            creations = CREDENTIAL_CREATIONS;
            FutureTask<IDaaSCredentialProvider> existing = creations.get(scope);
            if (existing != null && existing.isDone()) {
                // Success publishes the provider before completion. Recheck because it may
                // have completed after the first cache read; otherwise retire the failed task.
                IDaaSCredentialProvider completed = CREDENTIAL_PROVIDERS.get(scope);
                if (completed != null) {
                    return new CredentialCreation(completed);
                }
                creations.remove(scope, existing);
                existing = null;
            }
            if (existing == null) {
                // Completion can publish and retire its task between the two lookups.
                IDaaSCredentialProvider completed = CREDENTIAL_PROVIDERS.get(scope);
                if (completed != null) return new CredentialCreation(completed);
                // Construction only captures configuration; credential IO happens in the task.
                final IDaaSCredentialProvider provider = createCredentialProvider(scope);
                final ConcurrentMap<String, IDaaSCredentialProvider> providers = CREDENTIAL_PROVIDERS;
                existing = new FutureTask<>(() -> {
                    provider.getCredential();
                    providers.put(scope, provider);
                    return provider;
                });
                creations.put(scope, existing);
            }
            task = existing;
        }
        return new CredentialCreation(scope, task, creations);
    }

    private static final class CredentialCreation {
        private final IDaaSCredentialProvider cached;
        private final String scope;
        private final FutureTask<IDaaSCredentialProvider> task;
        private final ConcurrentMap<String, FutureTask<IDaaSCredentialProvider>> creations;

        private CredentialCreation(IDaaSCredentialProvider cached) {
            this.cached = cached;
            this.scope = null;
            this.task = null;
            this.creations = null;
        }

        private CredentialCreation(String scope, FutureTask<IDaaSCredentialProvider> task,
                                   ConcurrentMap<String, FutureTask<IDaaSCredentialProvider>> creations) {
            this.cached = null;
            this.scope = scope;
            this.task = task;
            this.creations = creations;
        }

        private IDaaSCredentialProvider get() {
            if (cached != null) return cached;
            runCredentialCreation(task);
            boolean interrupted = false;
            try {
                for (;;) {
                    try {
                        return task.get();
                    } catch (InterruptedException e) {
                        // Match the previous uninterruptible cache-monitor wait, retaining the flag.
                        interrupted = true;
                    }
                }
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                throw new ConfigException("CredentialCreationFailed", "Failed to create credential provider", cause);
            } finally {
                creations.remove(scope, task); // Retire both successful and failed completed work.
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void runCredentialCreation(FutureTask<IDaaSCredentialProvider> task) {
        java.util.Set<FutureTask<IDaaSCredentialProvider>> active = ACTIVE_CREATIONS.get();
        if (active == null) {
            active = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<FutureTask<IDaaSCredentialProvider>, Boolean>());
            ACTIVE_CREATIONS.set(active);
        }
        if (!active.add(task)) {
            throw new ConfigException("RecursiveCredentialCreation", "Credential creation cannot wait for itself on the same thread");
        }
        try {
            task.run(); // FutureTask guarantees one warm-up, including concurrent callers.
        } finally {
            active.remove(task);
            if (active.isEmpty()) {
                ACTIVE_CREATIONS.remove();
            }
        }
    }

    private static IDaaSCredentialProvider createCredentialProvider(String scope) {
        IdentityAuthenticationConfiguration authnConfig = IDAAS_CLIENT_CONFIG.getAuthnConfiguration();
        IDaaSMachineCredentialProvider credentialProvider = IDaaSMachineCredentialProvider.builder()
                .clientId(IDAAS_CLIENT_CONFIG.getClientId())
                .scope(scope)
                .tokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint())
                .authnMethod(authnConfig.getAuthnMethod())
                .build();
        TokenAuthnMethod authnMethod = authnConfig.getAuthnMethod();

        String privateKeyEnvVarName;
        String privateKeyString;
        JwtClientAssertionProvider clientAssertionProvider;
        switch (authnMethod) {
            case CLIENT_SECRET_BASIC:
            case CLIENT_SECRET_POST:
                credentialProvider.setClientSecretSupplier(() -> System.getenv(authnConfig.getClientSecretEnvVarName()));
                break;
            case CLIENT_SECRET_JWT:
                clientAssertionProvider = new StaticClientSecretAssertionProvider(() -> System.getenv(authnConfig.getClientSecretEnvVarName()));
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setScope(scope);
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PRIVATE_KEY_JWT:
                privateKeyEnvVarName = authnConfig.getPrivateKeyEnvVarName();
                privateKeyString = StringUtil.defaultIfBlank(System.getenv(privateKeyEnvVarName), System.getProperty(privateKeyEnvVarName));
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setScope(scope);
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PKCS7:
                credentialProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS) {
                    AlibabaCloudEcsAttestedDocumentProvider documentProvider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                            .idaasInstanceId(IDAAS_CLIENT_CONFIG.getIdaasInstanceId())
                            .build();
                    credentialProvider.setAttestedDocumentProvider(documentProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.AWS_EC2) {
                    AwsEc2Pkcs7AttestedDocumentProvider documentProvider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                            .build();
                    credentialProvider.setAttestedDocumentProvider(documentProvider);
                }
                break;
            case OIDC:
                credentialProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.KUBERNETES) {
                    String oidcTokenFilePath = authnConfig.getOidcTokenFilePath();
                    if (StringUtil.isEmpty(oidcTokenFilePath) && StringUtil.isNotEmpty(authnConfig.getOidcTokenFilePathEnvVarName())) {
                        oidcTokenFilePath = System.getenv(authnConfig.getOidcTokenFilePathEnvVarName());
                    }

                    if (StringUtil.isEmpty(oidcTokenFilePath)) {
                        oidcTokenFilePath = AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH;
                    }
                    OidcTokenProvider oidcTokenProvider = KubernetesOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    credentialProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.AWS_EKS) {
                    String oidcTokenFilePath = resolveCloudOidcTokenFilePath(authnConfig,
                            AuthenticationConstants.AWS_EKS_OIDC_TOKEN_PATH_ENV,
                            AuthenticationConstants.AWS_EKS_OIDC_TOKEN_DEFAULT_PATH);
                    OidcTokenProvider oidcTokenProvider = AwsEksOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    credentialProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK) {
                    String oidcTokenFilePath = resolveCloudOidcTokenFilePath(authnConfig,
                            AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV,
                            AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_DEFAULT_PATH);
                    OidcTokenProvider oidcTokenProvider = AlibabaCloudAckOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    credentialProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.GOOGLE_VM) {
                    GcpVmOidcTokenProvider oidcTokenProvider = GcpVmOidcTokenProvider.builder()
                            // audience 取 issuer 的 origin（scheme://host[:port]）
                            .audience(resolveGcpAudience())
                            .build();
                    credentialProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.COMPUTER) {
                    credentialProvider.setOidcTokenProvider(HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER);
                } else {
                    throw new ConfigException(ErrorCode.UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT.getCode(), "Unsupported client deploy environment:" +
                            authnConfig.getClientDeployEnvironment());
                }
                break;
            case PCA:
                credentialProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                credentialProvider.setClientX509Certificate(authnConfig.getClientX509Certificate());
                credentialProvider.setX509CertChains(authnConfig.getX509CertChains());
                privateKeyEnvVarName = authnConfig.getPrivateKeyEnvVarName();
                privateKeyString = StringUtil.defaultIfBlank(System.getenv(privateKeyEnvVarName), System.getProperty(privateKeyEnvVarName));
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setScope(scope);
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PLUGIN:
                credentialProvider.setPluginName(authnConfig.getPluginName());
                break;
            default:
                throw new ConfigException(ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(), "Unsupported authentication method:" + authnMethod);
        }
        return credentialProvider;
    }

    public static synchronized IDaaSMachineTokenExchangeCredentialProvider getIDaaSTokenExchangeCredentialProvider() {
        ensureInitialized();
        return getIDaaSTokenExchangeCredentialProvider(IDAAS_CLIENT_CONFIG.getScope());
    }

    public static synchronized IDaaSMachineTokenExchangeCredentialProvider getIDaaSTokenExchangeCredentialProvider(String scope) {
        ensureInitialized();

        ScopeUtil.validateScope(scope);
        return TOKEN_EXCHANGE_CREDENTIAL_PROVIDERS.computeIfAbsent(scope, IDaaSCredentialProviderFactory::createTokenExchangeCredentialProvider);
    }

    private static IDaaSMachineTokenExchangeCredentialProvider createTokenExchangeCredentialProvider(String scope) {
        IdentityAuthenticationConfiguration authnConfig = IDAAS_CLIENT_CONFIG.getAuthnConfiguration();
        IDaaSMachineTokenExchangeCredentialProvider tokenExchangeProvider = IDaaSMachineTokenExchangeCredentialProvider.builder()
                .clientId(IDAAS_CLIENT_CONFIG.getClientId())
                .scope(scope)
                .tokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint())
                .authnMethod(authnConfig.getAuthnMethod())
                .build();
        TokenAuthnMethod authnMethod = authnConfig.getAuthnMethod();

        String privateKeyEnvVarName;
        String privateKeyString;
        JwtClientAssertionProvider clientAssertionProvider;
        switch (authnMethod) {
            case CLIENT_SECRET_BASIC:
            case CLIENT_SECRET_POST:
                tokenExchangeProvider.setClientSecretSupplier(() -> System.getenv(authnConfig.getClientSecretEnvVarName()));
                break;
            case CLIENT_SECRET_JWT:
                clientAssertionProvider = new StaticClientSecretAssertionProvider(() -> System.getenv(authnConfig.getClientSecretEnvVarName()));
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticClientSecretAssertionProvider) clientAssertionProvider).setScope(scope);
                tokenExchangeProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PRIVATE_KEY_JWT:
                privateKeyEnvVarName = authnConfig.getPrivateKeyEnvVarName();
                privateKeyString = StringUtil.defaultIfBlank(System.getenv(privateKeyEnvVarName), System.getProperty(privateKeyEnvVarName));
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setScope(scope);
                tokenExchangeProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PKCS7:
                tokenExchangeProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS) {
                    AlibabaCloudEcsAttestedDocumentProvider documentProvider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                            .idaasInstanceId(IDAAS_CLIENT_CONFIG.getIdaasInstanceId())
                            .build();
                    tokenExchangeProvider.setAttestedDocumentProvider(documentProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.AWS_EC2) {
                    AwsEc2Pkcs7AttestedDocumentProvider documentProvider = AwsEc2Pkcs7AttestedDocumentProvider.builder()
                            .build();
                    tokenExchangeProvider.setAttestedDocumentProvider(documentProvider);
                }
                break;
            case OIDC:
                tokenExchangeProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.KUBERNETES) {
                    String oidcTokenFilePath = authnConfig.getOidcTokenFilePath();
                    if (StringUtil.isEmpty(oidcTokenFilePath) && StringUtil.isNotEmpty(authnConfig.getOidcTokenFilePathEnvVarName())) {
                        oidcTokenFilePath = System.getenv(authnConfig.getOidcTokenFilePathEnvVarName());
                    }

                    if (StringUtil.isEmpty(oidcTokenFilePath)) {
                        oidcTokenFilePath = AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH;
                    }
                    KubernetesOidcTokenProvider oidcTokenProvider = KubernetesOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    tokenExchangeProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.AWS_EKS) {
                    String oidcTokenFilePath = resolveCloudOidcTokenFilePath(authnConfig,
                            AuthenticationConstants.AWS_EKS_OIDC_TOKEN_PATH_ENV,
                            AuthenticationConstants.AWS_EKS_OIDC_TOKEN_DEFAULT_PATH);
                    AwsEksOidcTokenProvider oidcTokenProvider = AwsEksOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    tokenExchangeProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK) {
                    String oidcTokenFilePath = resolveCloudOidcTokenFilePath(authnConfig,
                            AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV,
                            AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_DEFAULT_PATH);
                    AlibabaCloudAckOidcTokenProvider oidcTokenProvider = AlibabaCloudAckOidcTokenProvider.builder().oidcTokenFilePath(oidcTokenFilePath).build();
                    tokenExchangeProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.GOOGLE_VM) {
                    GcpVmOidcTokenProvider oidcTokenProvider = GcpVmOidcTokenProvider.builder()
                            // audience 取 issuer 的 origin（scheme://host[:port]）
                            .audience(resolveGcpAudience())
                            .build();
                    tokenExchangeProvider.setOidcTokenProvider(oidcTokenProvider);
                } else if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.COMPUTER) {
                    tokenExchangeProvider.setOidcTokenProvider(HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER);
                } else {
                    throw new ConfigException(ErrorCode.UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT.getCode(), "Unsupported client deploy environment:" +
                            authnConfig.getClientDeployEnvironment());
                }
                break;
            case PCA:
                tokenExchangeProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                tokenExchangeProvider.setClientX509Certificate(authnConfig.getClientX509Certificate());
                tokenExchangeProvider.setX509CertChains(authnConfig.getX509CertChains());
                privateKeyEnvVarName = authnConfig.getPrivateKeyEnvVarName();
                privateKeyString = StringUtil.defaultIfBlank(System.getenv(privateKeyEnvVarName), System.getProperty(privateKeyEnvVarName));
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setClientId(IDAAS_CLIENT_CONFIG.getClientId());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setTokenEndpoint(IDAAS_CLIENT_CONFIG.getTokenEndpoint());
                ((StaticPrivateKeyAssertionProvider) clientAssertionProvider).setScope(scope);
                tokenExchangeProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PLUGIN:
                throw new ConfigException(ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(),
                        String.format("Unsupported authentication method: %s for token exchange", authnMethod));
            default:
                throw new ConfigException(ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(), "Unsupported authentication method:" + authnMethod);
        }
        return tokenExchangeProvider;
    }

    private static void publishConfigReadSnapshot() {
        IDaaSClientConfig snapshot = new IDaaSClientConfig();
        snapshot.assign(IDAAS_CLIENT_CONFIG);
        CONFIG_READ_SNAPSHOT = snapshot;
    }

    private static IDaaSClientConfig configReadSnapshot() {
        IDaaSClientConfig snapshot = CONFIG_READ_SNAPSHOT;
        if (snapshot == null) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(),
                    "IDaaS Credential Provider Factory has not been initialized.");
        }
        return snapshot;
    }

    public static String getDeveloperApiEndpoint() {
        return configReadSnapshot().getDeveloperApiEndpoint();
    }

    public static String getOpenApiEndpoint() {
        return configReadSnapshot().getOpenApiEndpoint();
    }

    public static String getIDaasInstanceId() {
        return configReadSnapshot().getIdaasInstanceId();
    }

    public static String getClientId() {
        return configReadSnapshot().getClientId();
    }

    /**
     * Internal last-successful transport snapshot, read without the factory monitor.
     * Retained after reset so previously returned providers can still perform their first request.
     * Null means no initialization has succeeded yet. Returns a defensive copy.
     * This is an internal SDK transport bridge, not the public factory-state query;
     * application code should use {@link #getHttpConfig()} after initialization.
     */
    public static HttpConfiguration getHttpConfigSnapshot() {
        return HttpConfiguration.copy(HTTP_CONFIG_SNAPSHOT);
    }

    /**
     * Returns active factory settings and requires initialization. Changes to the returned object do not
     * modify the committed transport snapshot; use reset/init to apply another HTTP configuration.
     */
    public static HttpConfiguration getHttpConfig() {
        return configReadSnapshot().getHttpConfiguration();
    }
}
