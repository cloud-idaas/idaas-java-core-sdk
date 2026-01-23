package com.cloud_idaas.core.factory;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.domain.constants.AuthenticationConstants;
import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.implementation.IDaaSMachineCredentialProvider;
import com.cloud_idaas.core.implementation.authentication.jwt.StaticClientSecretAssertionProvider;
import com.cloud_idaas.core.implementation.authentication.jwt.StaticPrivateKeyAssertionProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.FileOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.oidc.HumanFederatedOidcTokenProvider;
import com.cloud_idaas.core.implementation.authentication.pkcs7.AlibabaCloudEcsAttestedDocumentProvider;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class IDaaSCredentialProviderFactory {

    private static final IDaaSClientConfig IDAAS_CLIENT_CONFIG = new IDaaSClientConfig();
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    /**
     * When developer run the application, the credential provider will be initialized
     */
    private static OidcTokenProvider HUMAN_FEDERATE_CREDENTIAL_OIDC_TOKEN_PROVIDER;

    /**
     * It will cache all credential providers, based by scope.
     */
    private static final ConcurrentMap<String, IDaaSCredentialProvider> CREDENTIAL_PROVIDERS = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(IDaaSCredentialProviderFactory.class);

    public synchronized static void init() {
        if (INITIALIZED.get()) {
            LOGGER.info("IDaaS Credential Provider Factory has been initialized.");
            return;
        }
        final String configContent = ConfigReader.getConfigAsString();
        try {
            IDAAS_CLIENT_CONFIG.assign(JSONUtil.parseObject(configContent, IDaaSClientConfig.class));
            validateClientConfig(IDAAS_CLIENT_CONFIG);
            validateHttpConfig(IDAAS_CLIENT_CONFIG.getHttpConfiguration());
            INITIALIZED.set(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), "IDaaS Credential Provider Factory init failed.");
        }

        initCredentialProvider();
    }

    private static void initCredentialProvider() {
        if (!INITIALIZED.get()) {
            return;
        }

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
        CREDENTIAL_PROVIDERS.computeIfAbsent(IDAAS_CLIENT_CONFIG.getScope(), IDaaSCredentialProviderFactory::createCredentialProvider);
    }

    public synchronized static void init(IDaaSClientConfig authenticationConfig) {
        if (INITIALIZED.get()) {
            LOGGER.info("IDaaS Credential Provider Factory has been initialized.");
            return;
        }

        IDAAS_CLIENT_CONFIG.assign(authenticationConfig);
        validateClientConfig(IDAAS_CLIENT_CONFIG);
        INITIALIZED.set(true);
    }

    private static void validateClientConfig(IDaaSClientConfig clientConfig) {
        ValidatorUtil.validateBaseConfig(clientConfig);
        if (clientConfig.getAuthnConfiguration().getIdentityType() == AuthenticationIdentityEnum.HUMAN){
            ValidatorUtil.validateHumanConfig(clientConfig);
        } else {
            ValidatorUtil.validateClientConfig(clientConfig);
        }
    }

    private static void validateHttpConfig(HttpConfiguration httpConfiguration) {
        ValidatorUtil.validateHttpConfig(httpConfiguration);
    }

    public static IDaaSCredentialProvider getIDaaSCredentialProvider() {
        return getIDaaSCredentialProvider(IDAAS_CLIENT_CONFIG.getScope());
    }

    public static IDaaSCredentialProvider getIDaaSCredentialProvider(String scope) {
        if (!INITIALIZED.get()) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), "IDaaS Credential Provider Factory has not been initialized.");
        }

        return CREDENTIAL_PROVIDERS.computeIfAbsent(scope, IDaaSCredentialProviderFactory::createCredentialProvider);
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
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PRIVATE_KEY_JWT:
                privateKeyEnvVarName = authnConfig.getPrivateKeyEnvVarName();
                privateKeyString = System.getenv(privateKeyEnvVarName);
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            case PKCS7:
                credentialProvider.setApplicationFederatedCredentialName(authnConfig.getApplicationFederatedCredentialName());
                if (authnConfig.getClientDeployEnvironment() == ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS) {
                    AlibabaCloudEcsAttestedDocumentProvider documentProvider = AlibabaCloudEcsAttestedDocumentProvider.builder()
                            .idaasInstanceId(IDAAS_CLIENT_CONFIG.getIdaasInstanceId())
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
                    OidcTokenProvider oidcTokenProvider = new FileOidcTokenProvider(oidcTokenFilePath);
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
                privateKeyString = System.getenv(privateKeyEnvVarName);
                clientAssertionProvider = new StaticPrivateKeyAssertionProvider(privateKeyString);
                credentialProvider.setClientAssertionProvider(clientAssertionProvider);
                break;
            default:
                throw new ConfigException(ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD.getCode(), "Unsupported authentication method:" + authnMethod);
        }
        credentialProvider.getCredential();
        return credentialProvider;
    }

    public static String getDeveloperApiEndpoint() {
        if (!INITIALIZED.get()) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), "IDaaS Credential Provider Factory has not been initialized.");
        }
        return IDAAS_CLIENT_CONFIG.getDeveloperApiEndpoint();
    }

    public static String getIDaasInstanceId() {
        if (!INITIALIZED.get()) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), "IDaaS Credential Provider Factory has not been initialized.");
        }
        return IDAAS_CLIENT_CONFIG.getIdaasInstanceId();
    }

    public static HttpConfiguration getHttpConfig() {
        if (!INITIALIZED.get()) {
            throw new ConfigException(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode(), "IDaaS Credential Provider Factory has not been initialized.");
        }
        return IDAAS_CLIENT_CONFIG.getHttpConfiguration();
    }
}
