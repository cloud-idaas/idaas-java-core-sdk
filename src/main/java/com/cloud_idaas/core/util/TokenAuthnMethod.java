package com.cloud_idaas.core.util;

public enum TokenAuthnMethod {

    NONE,
    CLIENT_SECRET_POST,
    CLIENT_SECRET_BASIC,
    CLIENT_SECRET_JWT,
    PRIVATE_KEY_JWT,
    /**
     * IDaaS custom defined authentication method
     */
    PKCS7,
    PCA,
    OIDC,

    ;
}
