package com.cloud_idaas.core.config;

import java.io.Serializable;

public class IDaaSClientConfig implements Serializable {

    private static final long serialVersionUID = 1001601574164658022L;

    private String idaasInstanceId;
    private String clientId;
    /**
     * default value, using idaas pam resource server scope
     */
    private String scope = "urn:cloud:idaas:pam|cloud_account:obtain_access_credential";
    private String issuer;
    private String tokenEndpoint;
    private String deviceAuthorizationEndpoint;
    private String developerApiEndpoint;

    private IdentityAuthenticationConfiguration authnConfiguration;

    private HttpConfiguration httpConfiguration = new HttpConfiguration();

    public IDaaSClientConfig() {
    }

    public String getIdaasInstanceId() {
        return idaasInstanceId;
    }

    public void setIdaasInstanceId(String idaasInstanceId) {
        this.idaasInstanceId = idaasInstanceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getDeviceAuthorizationEndpoint() {
        return deviceAuthorizationEndpoint;
    }

    public void setDeviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
    }

    public String getDeveloperApiEndpoint() {
        return developerApiEndpoint;
    }

    public void setDeveloperApiEndpoint(String developerApiEndpoint) {
        this.developerApiEndpoint = developerApiEndpoint;
    }

    public IdentityAuthenticationConfiguration getAuthnConfiguration() {
        return authnConfiguration;
    }

    public void setAuthnConfiguration(IdentityAuthenticationConfiguration authnConfiguration) {
        this.authnConfiguration = authnConfiguration;
    }

    public HttpConfiguration getHttpConfiguration() {
        return httpConfiguration;
    }

    public void setHttpConfiguration(HttpConfiguration httpConfiguration) {
        this.httpConfiguration = httpConfiguration;
    }

    public void assign(IDaaSClientConfig other) {
        if (other == null) {
            return;
        }
        this.idaasInstanceId = other.idaasInstanceId;
        this.clientId = other.clientId;
        this.scope = other.scope;
        this.issuer = other.issuer;
        this.tokenEndpoint = other.tokenEndpoint;
        this.deviceAuthorizationEndpoint = other.deviceAuthorizationEndpoint;
        this.developerApiEndpoint = other.developerApiEndpoint;
        if (other.authnConfiguration != null) {
            this.authnConfiguration = IdentityAuthenticationConfiguration.copy(other.getAuthnConfiguration());
        } else {
            this.authnConfiguration = null;
        }
        if (other.httpConfiguration != null){
            this.httpConfiguration = HttpConfiguration.copy(other.getHttpConfiguration());
        } else {
            this.httpConfiguration = null;
        }
    }


}
