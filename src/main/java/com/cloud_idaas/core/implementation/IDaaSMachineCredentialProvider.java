package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.http.OAuth2TokenUtil;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;
import com.cloud_idaas.core.util.StringUtil;
import com.cloud_idaas.core.util.TokenAuthnMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class IDaaSMachineCredentialProvider extends AbstractRefreshedCredentialProvider<IDaaSCredential> implements IDaaSCredentialProvider {

    private TokenAuthnMethod authnMethod = TokenAuthnMethod.CLIENT_SECRET_POST;
    private final String clientId;
    private final String scope;
    private final String tokenEndpoint;

    private Supplier<String> clientSecretSupplier;
    /**
     * When using private_key_jwt、client_secret_jwt、pca Authentication method, this parameter is required.
     */
    private JwtClientAssertionProvider clientAssertionProvider;

    private String applicationFederatedCredentialName;
    /**
     * When using PKCS7 authentication method, this parameter is required.
     */
    private Pkcs7AttestedDocumentProvider attestedDocumentProvider;
    /**
     * When using OIDC authentication method, this parameter is required.
     */
    private OidcTokenProvider oidcTokenProvider;
    /**
     * When using PCA authentication method, this parameter is required.
     */
    private String clientX509Certificate;
    private String x509CertChains;

    private static final Logger LOGGER = LoggerFactory.getLogger(IDaaSMachineCredentialProvider.class);

    private IDaaSMachineCredentialProvider(IDaaSMachineCredentialProviderBuilder builder) {
        super(builder);
        if (StringUtil.isBlank(builder.clientId)) {
            throw new IllegalArgumentException("clientId is blank");
        }
        if (StringUtil.isBlank(builder.scope)) {
            throw new IllegalArgumentException("scope is blank");
        }
        if (StringUtil.isBlank(builder.tokenEndpoint)) {
            throw new IllegalArgumentException("tokenEndpoint is blank");
        }

        if (builder.authnMethod != null) {
            this.authnMethod = builder.authnMethod;
        }
        this.clientId = builder.clientId;
        this.scope = builder.scope;
        this.tokenEndpoint = builder.tokenEndpoint;
        this.clientSecretSupplier = builder.clientSecretSupplier;
        this.clientAssertionProvider = builder.clientAssertionProvider;
        this.applicationFederatedCredentialName = builder.applicationFederatedCredentialName;
        this.attestedDocumentProvider = builder.attestedDocumentProvider;
        this.oidcTokenProvider = builder.oidcTokenProvider;
        this.clientX509Certificate = builder.clientX509Certificate;
        this.x509CertChains = builder.x509CertChains;
    }

    @Override
    public IDaaSCredential getCredential() {
        return this.getCachedResultSupplier().get();
    }

    public TokenAuthnMethod getAuthnMethod() {
        return authnMethod;
    }

    public void setAuthnMethod(TokenAuthnMethod authnMethod) {
        this.authnMethod = authnMethod;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public Supplier<String> getClientSecretSupplier() {
        return clientSecretSupplier;
    }

    public void setClientSecretSupplier(Supplier<String> clientSecretSupplier) {
        this.clientSecretSupplier = clientSecretSupplier;
    }

    public JwtClientAssertionProvider getClientAssertionProvider() {
        return clientAssertionProvider;
    }

    public void setClientAssertionProvider(JwtClientAssertionProvider clientAssertionProvider) {
        this.clientAssertionProvider = clientAssertionProvider;
    }

    public String getApplicationFederatedCredentialName() {
        return applicationFederatedCredentialName;
    }

    public void setApplicationFederatedCredentialName(String applicationFederatedCredentialName) {
        this.applicationFederatedCredentialName = applicationFederatedCredentialName;
    }

    public Pkcs7AttestedDocumentProvider getAttestedDocumentProvider() {
        return attestedDocumentProvider;
    }

    public void setAttestedDocumentProvider(Pkcs7AttestedDocumentProvider attestedDocumentProvider) {
        this.attestedDocumentProvider = attestedDocumentProvider;
    }

    public OidcTokenProvider getOidcTokenProvider() {
        return oidcTokenProvider;
    }

    public void setOidcTokenProvider(OidcTokenProvider oidcTokenProvider) {
        this.oidcTokenProvider = oidcTokenProvider;
    }

    public String getClientX509Certificate() {
        return clientX509Certificate;
    }

    public void setClientX509Certificate(String clientX509Certificate) {
        this.clientX509Certificate = clientX509Certificate;
    }

    public String getX509CertChains() {
        return x509CertChains;
    }

    public void setX509CertChains(String x509CertChains) {
        this.x509CertChains = x509CertChains;
    }

    private IDaaSTokenResponse getTokenFromIDaaS() {
        switch (authnMethod) {
            case CLIENT_SECRET_BASIC:
                if (clientSecretSupplier == null) {
                    clientSecretSupplier = () -> System.getenv("ALIBABA_CLOUD_EIAM_APP_CLIENT_SECRET");
                    if (StringUtil.isBlank(clientSecretSupplier.get())) {
                        throw new IllegalArgumentException("clientSecret is blank");
                    }
                }
                return OAuth2TokenUtil.getTokenWithClientSecretBasic(clientId, clientSecretSupplier.get(), tokenEndpoint, scope);
            case CLIENT_SECRET_POST:
                if (clientSecretSupplier == null) {
                    clientSecretSupplier = () -> System.getenv("ALIBABA_CLOUD_EIAM_APP_CLIENT_SECRET");
                    if (StringUtil.isBlank(clientSecretSupplier.get())) {
                        throw new IllegalArgumentException("clientSecret is blank");
                    }
                }
                return OAuth2TokenUtil.getTokenWithClientSecretPost(clientId, clientSecretSupplier.get(), tokenEndpoint, scope);
            case CLIENT_SECRET_JWT:
            case PRIVATE_KEY_JWT:
                if (clientAssertionProvider == null) {
                    throw new IllegalArgumentException("clientAssertionProvider is null.");
                }
                return OAuth2TokenUtil.getTokenWithClientAssertion(clientId, clientAssertionProvider.getClientAssertion(), tokenEndpoint, scope);
            case PKCS7:
                if (StringUtil.isBlank(applicationFederatedCredentialName)) {
                    throw new IllegalArgumentException("applicationFederatedCredentialName is blank");
                }
                if (attestedDocumentProvider == null) {
                    throw new IllegalArgumentException("attestedDocumentProvider is null");
                }
                String pkcs7AttestedDocument = attestedDocumentProvider.getAttestedDocument();
                return OAuth2TokenUtil.getTokenWithPKCS7AttestedDocument(clientId, applicationFederatedCredentialName, pkcs7AttestedDocument, tokenEndpoint,
                        scope);
            case OIDC:
                if (StringUtil.isBlank(applicationFederatedCredentialName)) {
                    throw new IllegalArgumentException("applicationFederatedCredentialName is blank");
                }
                if (oidcTokenProvider == null) {
                    throw new IllegalArgumentException("oidcTokenProvider is null");
                }
                String oidcToken = oidcTokenProvider.getOidcToken();
                return OAuth2TokenUtil.getTokenWithOIDCFederatedCredential(clientId, applicationFederatedCredentialName, oidcToken, tokenEndpoint, scope);
            case PCA:
                if (StringUtil.isBlank(applicationFederatedCredentialName)) {
                    throw new IllegalArgumentException("applicationFederatedCredentialName is blank");
                }
                if (StringUtil.isBlank(clientX509Certificate)) {
                    throw new IllegalArgumentException("clientX509Certificate is blank");
                }
                if (StringUtil.isBlank(x509CertChains)) {
                    throw new IllegalArgumentException("x509CertChains is blank");
                }
                return OAuth2TokenUtil.getTokenWithPCA(clientId, applicationFederatedCredentialName, clientX509Certificate, x509CertChains,
                        clientAssertionProvider.getClientAssertion(), tokenEndpoint, scope);
        }
        throw new UnsupportedOperationException("authn method is unsupported.");
    }

    public static IDaaSMachineCredentialProviderBuilder builder() {return new IDaaSMachineCredentialProviderBuilder();}

    @Override
    public RefreshResult<IDaaSCredential> refreshCredential() {
        IDaaSTokenResponse tokenResponse = getTokenFromIDaaS();
        LOGGER.info("Machine Credential refresh, time: {}", Instant.now());
        // staleTime: 4/5 of expiresIn, prefetchTime: 2/3 of expiresIn
        Instant staleTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 5));
        Instant prefetchTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 3));
        return RefreshResult.builder((IDaaSCredential)tokenResponse)
                .staleTime(staleTime)
                .prefetchTime(prefetchTime)
                .build();
    }

    public static final class IDaaSMachineCredentialProviderBuilder
            extends AbstractRefreshedCredentialProvider.BuilderImpl<IDaaSMachineCredentialProvider, IDaaSMachineCredentialProviderBuilder> {
        private String x509CertChains;
        private String clientX509Certificate;
        private OidcTokenProvider oidcTokenProvider;
        private Pkcs7AttestedDocumentProvider attestedDocumentProvider;
        private String applicationFederatedCredentialName;
        private JwtClientAssertionProvider clientAssertionProvider;
        private Supplier<String> clientSecretSupplier;
        private String tokenEndpoint;
        private String scope;
        private String clientId;
        private TokenAuthnMethod authnMethod;

        private IDaaSMachineCredentialProviderBuilder() {}

        public static IDaaSMachineCredentialProviderBuilder anIDaaSMachineCredentialProvider() {return new IDaaSMachineCredentialProviderBuilder();}

        public IDaaSMachineCredentialProviderBuilder x509CertChains(String x509CertChains) {
            this.x509CertChains = x509CertChains;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder clientX509Certificate(String clientX509Certificate) {
            this.clientX509Certificate = clientX509Certificate;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder oidcTokenProvider(OidcTokenProvider oidcTokenProvider) {
            this.oidcTokenProvider = oidcTokenProvider;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder attestedDocumentProvider(Pkcs7AttestedDocumentProvider attestedDocumentProvider) {
            this.attestedDocumentProvider = attestedDocumentProvider;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder applicationFederatedCredentialName(String applicationFederatedCredentialName) {
            this.applicationFederatedCredentialName = applicationFederatedCredentialName;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder clientAssertionProvider(JwtClientAssertionProvider clientAssertionProvider) {
            this.clientAssertionProvider = clientAssertionProvider;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder clientSecretSupplier(Supplier<String> clientSecretSupplier) {
            this.clientSecretSupplier = clientSecretSupplier;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public IDaaSMachineCredentialProviderBuilder authnMethod(TokenAuthnMethod authnMethod) {
            this.authnMethod = authnMethod;
            return this;
        }

        public IDaaSMachineCredentialProvider build() {
            return new IDaaSMachineCredentialProvider(this);
        }
    }
}
