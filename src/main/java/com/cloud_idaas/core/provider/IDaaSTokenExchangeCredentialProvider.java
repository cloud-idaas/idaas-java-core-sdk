package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.credential.IDaaSCredential;

public interface IDaaSTokenExchangeCredentialProvider {

    default String getIssuedToken(String accessToken, String tokenType, String requestedTokenType){
        IDaaSCredential credential = getCredential(accessToken, tokenType, requestedTokenType);
        if (credential == null) {
            return null;
        }
        return credential.getAccessToken();
    }

    IDaaSCredential getCredential(String accessToken, String tokenType, String requestedTokenType);
}
