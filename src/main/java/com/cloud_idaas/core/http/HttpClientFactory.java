package com.cloud_idaas.core.http;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.exception.ConfigException;

public class HttpClientFactory {

    private static volatile HttpConfiguration httpConfiguration;
    private static volatile HttpClient singletonOKHttpClient;

    private static final Object LOCK = new Object(); // Used for synchronization lock

    private static final ThreadLocal<InitializationTransport> INITIALIZATION = new ThreadLocal<>();
    // Only a cold bootstrap may expose this uncommitted transport to plugin workers.
    // Requests with a last-successful configuration keep using that configuration.
    private static PluginTransport pluginTransport; // Guarded by LOCK.

    /** Internal factory bridge. Registers an isolated cold-bootstrap candidate, never a committed client. */
    public static void beginPluginInitialization(Object generation, HttpConfiguration configuration) {
        if (generation == null || configuration == null) {
            throw new IllegalArgumentException("Plugin generation and HTTP configuration must not be null");
        }
        synchronized (LOCK) {
            pluginTransport = new PluginTransport(generation,
                    new InitializationTransport(HttpConfiguration.copy(configuration)));
        }
    }

    /** Internal factory bridge. An obsolete initializer must not detach a newer candidate. */
    public static void endPluginInitialization(Object generation) {
        synchronized (LOCK) {
            if (pluginTransport != null && pluginTransport.generation == generation) pluginTransport = null;
        }
    }

    private static final class PluginTransport {
        private final Object generation;
        private final Thread owner = Thread.currentThread();
        private final InitializationTransport transport;

        private PluginTransport(Object generation, InitializationTransport transport) {
            this.generation = generation;
            this.transport = transport;
        }
    }

    /**
     * Internal SDK bridge used by the factory across package boundaries, not an application configuration API.
     * Runs synchronous warm-up with thread-local candidate timeouts. Plugin cold bootstrap also
     * permits workers to use its registered candidate until the first successful transport exists.
     * Candidate transports are published only when warm-up succeeds and matches the committed configuration.
     * The scope is removed on failure, including Errors. Nested scopes are not supported.
     */
    public static void withInitializationHttpConfig(HttpConfiguration configuration, Runnable warmup) {
        if (configuration == null || warmup == null) {
            throw new IllegalArgumentException("HTTP configuration and warm-up callback must not be null");
        }
        if (INITIALIZATION.get() != null) {
            throw new IllegalStateException("Nested HTTP initialization is not supported");
        }
        final InitializationTransport pending;
        synchronized (LOCK) {
            pending = pluginTransport != null && pluginTransport.owner == Thread.currentThread()
                    && sameTimeouts(pluginTransport.transport.configuration, configuration)
                    ? pluginTransport.transport : new InitializationTransport(HttpConfiguration.copy(configuration));
        }
        INITIALIZATION.set(pending);
        try {
            warmup.run();
            synchronized (LOCK) {
                HttpConfiguration committed = IDaaSCredentialProviderFactory.getHttpConfigSnapshot();
                // A concurrent reader may already have selected a client for the committed config.
                if (pending.client != null && sameTimeouts(committed, pending.configuration)
                        && (singletonOKHttpClient == null || !sameTimeouts(committed, httpConfiguration))) {
                    singletonOKHttpClient = pending.client;
                    httpConfiguration = HttpConfiguration.copy(pending.configuration);
                }
            }
        } finally {
            INITIALIZATION.remove();
        }
    }

    private static final class InitializationTransport {
        private final HttpConfiguration configuration;
        private HttpClient client;

        private InitializationTransport(HttpConfiguration configuration) {
            this.configuration = configuration;
        }
    }

    private static boolean sameTimeouts(HttpConfiguration first, HttpConfiguration second) {
        return first != null && second != null
                && first.getConnectTimeout() == second.getConnectTimeout()
                && first.getReadTimeout() == second.getReadTimeout();
    }

    private static HttpClient createClient(HttpConfiguration configuration) {
        return DefaultOKHttpClientImp.builder()
                .connectTimeout(configuration.getConnectTimeout())
                .readTimeout(configuration.getReadTimeout()).build();
    }

    // Caller holds LOCK; a candidate is never installed in the committed singleton here.
    private static HttpClient initializationClient(InitializationTransport pending) {
        if (pending.client == null) {
            pending.client = singletonOKHttpClient != null && sameTimeouts(pending.configuration, httpConfiguration)
                    ? singletonOKHttpClient : createClient(pending.configuration);
        }
        return pending.client;
    }

    public static HttpClient getDefaultHttpClient() {
        // Existing providers use the last successful configuration. With no successful transport,
        // a plugin worker may use the isolated cold candidate, without publishing it globally.
        // Never acquire the factory monitor while holding LOCK.
        synchronized (LOCK) {
            InitializationTransport pending = INITIALIZATION.get();
            if (pending != null) {
                return initializationClient(pending);
            }
            HttpConfiguration current = IDaaSCredentialProviderFactory.getHttpConfigSnapshot();
            if (current == null) {
                if (pluginTransport != null) return initializationClient(pluginTransport.transport);
                throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(),
                        "IDaaS Credential Provider Factory has not been initialized.");
            }
            if (singletonOKHttpClient == null || !sameTimeouts(current, httpConfiguration)) {
                singletonOKHttpClient = createClient(current);
                httpConfiguration = HttpConfiguration.copy(current);
            }
            return singletonOKHttpClient;
        }
    }
}
