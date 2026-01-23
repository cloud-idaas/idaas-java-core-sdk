package com.cloud_idaas.core.config;

import com.cloud_idaas.core.domain.constants.AuthenticationIdentityEnum;
import com.cloud_idaas.core.domain.constants.ClientDeployEnvironmentEnum;
import com.cloud_idaas.core.util.TokenAuthnMethod;

import java.io.Serializable;

public class IdentityAuthenticationConfiguration implements Serializable {

    private static final long serialVersionUID = -8590674403849831036L;

    private AuthenticationIdentityEnum identityType = AuthenticationIdentityEnum.CLIENT;

    private TokenAuthnMethod authnMethod = TokenAuthnMethod.NONE;

    /**
     * When authentication method is client_secret_post or client_secret_basic or client_secret_jwt,
     * it is supported to use environment variable to get client secret.
     */
    private String clientSecretEnvVarName;

    /**
     * When authentication method is private_key_jwt,
     * it is supported to use environment variable to get private_key.
     */
    private String privateKeyEnvVarName;

    private String applicationFederatedCredentialName;

    private ClientDeployEnvironmentEnum clientDeployEnvironment;

    private String oidcTokenFilePathEnvVarName;

    private String oidcTokenFilePath;
    /**
     * Only applicable to PCA federated authentication scenarios
     */
    private String clientX509Certificate;

    private String x509CertChains;

    private String humanAuthenticateClientId = "iap_developer";

    public IdentityAuthenticationConfiguration() {
    }

    public AuthenticationIdentityEnum getIdentityType() {
        return identityType;
    }

    public void setIdentityType(AuthenticationIdentityEnum identityType) {
        this.identityType = identityType;
    }

    public TokenAuthnMethod getAuthnMethod() {
        return authnMethod;
    }

    public void setAuthnMethod(TokenAuthnMethod authnMethod) {
        this.authnMethod = authnMethod;
    }

    public String getClientSecretEnvVarName() {
        return clientSecretEnvVarName;
    }

    public void setClientSecretEnvVarName(String clientSecretEnvVarName) {
        this.clientSecretEnvVarName = clientSecretEnvVarName;
    }

    public String getPrivateKeyEnvVarName() {
        return privateKeyEnvVarName;
    }

    public void setPrivateKeyEnvVarName(String privateKeyEnvVarName) {
        this.privateKeyEnvVarName = privateKeyEnvVarName;
    }

    public String getApplicationFederatedCredentialName() {
        return applicationFederatedCredentialName;
    }

    public void setApplicationFederatedCredentialName(String applicationFederatedCredentialName) {
        this.applicationFederatedCredentialName = applicationFederatedCredentialName;
    }

    public ClientDeployEnvironmentEnum getClientDeployEnvironment() {
        return clientDeployEnvironment;
    }

    public void setClientDeployEnvironment(ClientDeployEnvironmentEnum clientDeployEnvironment) {
        this.clientDeployEnvironment = clientDeployEnvironment;
    }

    public String getOidcTokenFilePathEnvVarName() {
        return oidcTokenFilePathEnvVarName;
    }

    public void setOidcTokenFilePathEnvVarName(String oidcTokenFilePathEnvVarName) {
        this.oidcTokenFilePathEnvVarName = oidcTokenFilePathEnvVarName;
    }

    public String getOidcTokenFilePath() {
        return oidcTokenFilePath;
    }

    public void setOidcTokenFilePath(String oidcTokenFilePath) {
        this.oidcTokenFilePath = oidcTokenFilePath;
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

    public String getHumanAuthenticateClientId() {
        return humanAuthenticateClientId;
    }

    public void setHumanAuthenticateClientId(String humanAuthenticateClientId) {
        this.humanAuthenticateClientId = humanAuthenticateClientId;
    }

    public static IdentityAuthenticationConfiguration copy(IdentityAuthenticationConfiguration source) {
        if (source == null) {
            return null;
        }
        
        IdentityAuthenticationConfiguration target = new IdentityAuthenticationConfiguration();
        target.setAuthnMethod(source.getAuthnMethod());
        target.setIdentityType(source.getIdentityType());
        target.setClientSecretEnvVarName(source.getClientSecretEnvVarName());
        target.setPrivateKeyEnvVarName(source.getPrivateKeyEnvVarName());
        target.setApplicationFederatedCredentialName(source.getApplicationFederatedCredentialName());
        target.setClientDeployEnvironment(source.getClientDeployEnvironment());
        target.setOidcTokenFilePathEnvVarName(source.getOidcTokenFilePathEnvVarName());
        target.setOidcTokenFilePath(source.getOidcTokenFilePath());
        target.setClientX509Certificate(source.getClientX509Certificate());
        target.setX509CertChains(source.getX509CertChains());
        target.setHumanAuthenticateClientId(source.getHumanAuthenticateClientId());
        return target;
    }

}
