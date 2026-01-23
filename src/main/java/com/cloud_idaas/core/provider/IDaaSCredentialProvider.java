package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.credential.IDaaSCredential;

@FunctionalInterface
public interface IDaaSCredentialProvider extends OidcTokenProvider {

    default String getBearerToken() {
        IDaaSCredential credential = getCredential();
        if (credential == null) {
            return null;
        }
        return credential.getAccessToken();
    };

    default String getOidcToken() {
        return getBearerToken();
    }

    IDaaSCredential getCredential();
}
