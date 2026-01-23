package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.provider.OidcTokenProvider;

public class StaticOidcTokenProvider implements OidcTokenProvider {

    private String oidcToken;

    public StaticOidcTokenProvider() {
    }

    public StaticOidcTokenProvider(String oidcToken) {
        this.oidcToken = oidcToken;
    }

    @Override
    public String getOidcToken() {
        return oidcToken;
    }

    public void setOidcToken(String oidcToken) {
        this.oidcToken = oidcToken;
    }
}
