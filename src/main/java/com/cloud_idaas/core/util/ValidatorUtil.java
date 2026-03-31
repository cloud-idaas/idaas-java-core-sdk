package com.cloud_idaas.core.util;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.config.IdentityAuthenticationConfiguration;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.CacheException;
import com.cloud_idaas.core.exception.ConfigException;

public class ValidatorUtil {

    public static void validateConfigNotBlank(String s, String errorCode, String errorMessage) {
        if (StringUtil.isBlank(s)){
            throw new ConfigException(errorCode, errorMessage);
        }
    }

    public static <T> void validateConfigNotNull(T o, String errorCode, String errorMessage) {
        if (o == null){
            throw new ConfigException(errorCode, errorMessage);
        }
    }

    public static <T> void validateTokenNotNull(T o, String errorCode, String errorMessage) {
        if (o == null){
            throw new CacheException(errorCode, errorMessage);
        }
    }

    public static void validateBaseConfig(IDaaSClientConfig idaasClientConfig) {
        validateConfigNotBlank(idaasClientConfig.getIdaasInstanceId(), ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND.getCode(), "IDaaS Instance ID not found.");
        validateConfigNotBlank(idaasClientConfig.getClientId(), ErrorCode.CLIENT_ID_NOT_FOUND.getCode(), "Client ID not found.");
        validateConfigNotBlank(idaasClientConfig.getIssuer(), ErrorCode.ISSUER_ENDPOINT_NOT_FOUND.getCode(), "Issuer Endpoint not found");
        validateConfigNotBlank(idaasClientConfig.getTokenEndpoint(), ErrorCode.TOKEN_ENDPOINT_NOT_FOUND.getCode(), "Token Endpoint not found.");
        ScopeUtil.validateScope(idaasClientConfig.getScope());
        validateConfigNotNull(idaasClientConfig.getAuthnConfiguration(), ErrorCode.AUTHN_CONFIGURATION_NOT_FOUND.getCode(), "Authn Configuration not found.");
    }

    public static void validateHumanConfig(IDaaSClientConfig idaasClientConfig) {
        validateConfigNotBlank(idaasClientConfig.getAuthnConfiguration().getHumanAuthenticateClientId(), ErrorCode.HUMAN_AUTHENTICATE_CLIENT_ID_NOT_FOUND.getCode(),
                "Human Authenticate Client ID not found.");
        validateConfigNotBlank(idaasClientConfig.getDeviceAuthorizationEndpoint(), ErrorCode.DEVICE_AUTHORIZATION_ENDPOINT_NOT_FOUND.getCode(),
                "Device Authorization Endpoint not found.");
    }

    public static void validateClientConfig(IDaaSClientConfig idaasClientConfig) {
        IdentityAuthenticationConfiguration authnConfiguration = idaasClientConfig.getAuthnConfiguration();
        if (authnConfiguration == null) {
            throw new ConfigException(ErrorCode.AUTHN_CONFIGURATION_NOT_FOUND.getCode(), "Authn Configuration not found.");
        }
        TokenAuthnMethod authnMethod = authnConfiguration.getAuthnMethod();
        if (TokenAuthnMethod.CLIENT_SECRET_BASIC == authnMethod  || TokenAuthnMethod.CLIENT_SECRET_POST == authnMethod
            || TokenAuthnMethod.CLIENT_SECRET_JWT == authnMethod) {
            validateConfigNotBlank(authnConfiguration.getClientSecretEnvVarName(), ErrorCode.CLIENT_SECRET_ENV_VAR_NAME_NOT_FOUND.getCode(),
                    "Client Secret Env Var Name not found.");
        } else if (TokenAuthnMethod.PRIVATE_KEY_JWT == authnMethod) {
            validateConfigNotBlank(authnConfiguration.getPrivateKeyEnvVarName(), ErrorCode.PRIVATE_KEY_ENV_VAR_NAME_NOT_FOUND.getCode(),
                    "Private Key Env Var Name not found.");
        } else if (TokenAuthnMethod.PKCS7 == authnMethod || TokenAuthnMethod.OIDC == authnMethod) {
            validateConfigNotBlank(authnConfiguration.getApplicationFederatedCredentialName(), ErrorCode.APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND.getCode(),
                    "Application Federated Credential Name not found.");
            validateConfigNotNull(authnConfiguration.getClientDeployEnvironment(), ErrorCode.CLIENT_DEPLOY_ENVIRONMENT_NOT_FOUND.getCode(),
                    "Client Deploy Environment not found."
            );
        } else if (TokenAuthnMethod.PCA == authnMethod) {
            validateConfigNotBlank(authnConfiguration.getApplicationFederatedCredentialName(), ErrorCode.APPLICATION_FEDERATED_CREDENTIAL_NAME_NOT_FOUND.getCode(),
                    "Application Federated Credential Name not found.");
            validateConfigNotBlank(authnConfiguration.getClientX509Certificate(), ErrorCode.CLIENT_X509_CERTIFICATE_NOT_FOUND.getCode(),
                    "Client X509 Certificate not found.");
            validateConfigNotBlank(authnConfiguration.getX509CertChains(), ErrorCode.X509_CERT_CHAINS_NOT_FOUND.getCode(),
                    "X509 Cert Chains not found.");
            validateConfigNotBlank(authnConfiguration.getPrivateKeyEnvVarName(), ErrorCode.PRIVATE_KEY_ENV_VAR_NAME_NOT_FOUND.getCode(),
                    "Private Key Env Var Name not found.");
        } else if (TokenAuthnMethod.PLUGIN == authnMethod) {
            validateConfigNotBlank(idaasClientConfig.getOpenApiEndpoint(), ErrorCode.OPEN_API_ENDPOINT_NOT_FOUND.getCode(),
                    "Open Api Endpoint not found.");
            validateConfigNotBlank(authnConfiguration.getPluginName(), ErrorCode.PLUGIN_NAME_NOT_FOUND.getCode(),
                    "Plugin Name not found.");
        }
    }

    public static void validateHttpConfig(HttpConfiguration httpConfiguration) {
        if (httpConfiguration != null){
            if (httpConfiguration.getConnectTimeout() < 2000 || httpConfiguration.getConnectTimeout() > 60000){
                throw new ConfigException(ErrorCode.CONNECT_TIMEOUT_NOT_VALID.getCode(), "Connect Timeout not valid.");
            }
            if (httpConfiguration.getReadTimeout() < 2000 || httpConfiguration.getReadTimeout() > 60000){
                throw new ConfigException(ErrorCode.READ_TIMEOUT_NOT_VALID.getCode(), "Read Timeout not valid.");
            }
        }
    }

    public static void validateLocalToken(IDaaSTokenResponse localToken) {
        if (!StringUtil.equals(localToken.getTokenType(), HttpConstants.BEARER)){
            throw new CacheException(ErrorCode.INVALID_TOKEN_TYPE.getCode(), String.format("Invalid local token type: %s.", localToken.getTokenType()));
        }
        validateTokenNotNull(localToken.getAccessToken(), ErrorCode.ACCESS_TOKEN_NOT_FOUND.getCode(), "Access Token not found.");
        validateTokenNotNull(localToken.getIdToken(), ErrorCode.ID_TOKEN_NOT_FOUND.getCode(), "ID Token not found.");
        validateTokenNotNull(localToken.getRefreshToken(), ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode(), "Refresh Token not found.");
    }
}
