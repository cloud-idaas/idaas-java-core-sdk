package com.cloud_idaas.core.credential;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IDaaSTokenResponse implements IDaaSCredential, Serializable {
    private static final long serialVersionUID = -8367812108696951221L;

    /**
     * Person authentication, write the related refresh token to local storage for refreshing to avoid multiple logins
     */
    @SerializedName("access_token")
    private String accessToken;

    /**
     * Going forward, this id token will not be used, use access token instead
     */
    @SerializedName("id_token")
    private String idToken;
    /**
     * Person authentication, write the related refresh token to local storage for refreshing to avoid multiple logins
     */
    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;
    /**
     * Token expiration time in seconds
     */
    @SerializedName("expires_in")
    private long expiresIn;
    /**
     * Token expiration time as Unix timestamp in seconds
     */
    @SerializedName("expires_at")
    private long expiresAt;

    public IDaaSTokenResponse() {
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean willSoonExpire() {
        final long now = System.currentTimeMillis() / 1000;
        final double expireFact = 0.15;
        return this.expiresIn * expireFact > (this.expiresAt - now);
    }
}
