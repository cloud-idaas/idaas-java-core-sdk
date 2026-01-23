package com.cloud_idaas.core.domain.constants;


public enum ErrorCode {

    IDAAS_INSTANCE_ID_NOT_FOUND("IDaaSInstanceIdNotFound"),

    CLIENT_ID_NOT_FOUND("ClientIdNotFound"),

    ISSUER_ENDPOINT_NOT_FOUND("IssuerEndpointNotFound"),

    TOKEN_ENDPOINT_NOT_FOUND("TokenEndpointNotFound"),

    HUMAN_AUTHENTICATE_CLIENT_ID_NOT_FOUND("HumanAuthenticateClientIdNotFound"),

    HUMAN_AUTHENTICATE_SCOPE_NOT_FOUND("HumanAuthenticateScopeNotFound"),

    DEVICE_AUTHORIZATION_ENDPOINT_NOT_FOUND("DeviceAuthorizationEndpointNotFound"),

    AUTHN_CONFIGURATION_NOT_FOUND("AuthnConfigurationNotFound"),

    CLIENT_SECRET_ENV_VAR_NAME_NOT_FOUND("ClientSecretEnvVarNameNotFound"),

    PRIVATE_KEY_ENV_VAR_NAME_NOT_FOUND("PrivateKeyEnvVarNameNotFound"),

    APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND("ApplicationFederatedCredentialNameNotFound"),

    CLIENT_DEPLOY_ENVIRONMENT_NOT_FOUND("ClientDeployEnvironmentNotFound"),

    CLIENT_X509_CERTIFICATE_NOT_FOUND("ClientX509CertificateNotFound"),

    X509_CERT_CHAINS_NOT_FOUND("X509CertChainsNotFound"),

    UNSUPPORTED_CLIENT_DEPLOY_ENVIRONMENT("UnsupportedClientDeployEnvironment"),

    UNSUPPORTED_AUTHENTICATION_METHOD("UnsupportedAuthenticationMethod"),

    CONNECT_TIMEOUT_NOT_VALID("ConnectTimeoutNotValid"),

    READ_TIMEOUT_NOT_VALID("ReadTimeoutNotValid"),

    IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT("IDaaSCredentialProviderFactoryNotInit"),

    NOT_SUPPORTED_WEB_KEY("NotSupportedWebKey"),

    REFRESH_TOKEN_EMPTY("RefreshTokenEmpty"),

    DEVELOPER_API_ENDPOINT_NOT_FOUND("DeveloperApiEndpointNotFound"),

    LOAD_CONFIG_FILE_FAILED("LoadConfigFileFailed"),

    INVALID_REQUEST("InvalidRequest"),

    CONNECT_TIME_OUT("ConnectTimeOut"),

    READ_TIME_OUT("ReadTimeOut"),

    CLIENT_ERROR("ClientError"),

    SERVER_ERROR("ServerError"),

    INVALID_TOKEN_TYPE("InvalidTokenType"),

    ACCESS_TOKEN_NOT_FOUND("AccessTokenNotFound"),

    ID_TOKEN_NOT_FOUND("IdTokenNotFound"),

    REFRESH_TOKEN_NOT_FOUND("RefreshTokenNotFound"),

    ;
    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
