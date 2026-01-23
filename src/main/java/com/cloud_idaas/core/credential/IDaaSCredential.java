package com.cloud_idaas.core.credential;

public interface IDaaSCredential {

    String getAccessToken();

    /**
     * when identity type is client, id token is empty.
     * @return id token
     */
    String getIdToken();

    /**
     * when identity type is client, refresh token is empty.
     * @return refresh token
     */
    String getRefreshToken();

    String getTokenType();

}
