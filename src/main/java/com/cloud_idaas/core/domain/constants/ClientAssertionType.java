package com.cloud_idaas.core.domain.constants;

public interface ClientAssertionType {

    /**

     * RFC specification
     */
    String OAUTH_JWT_BEARER = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    /**
     * PCA
     */
    String PRIVATE_CA_JWT_BEARER = "urn:cloud:idaas:params:oauth:client-assertion-type:x509-jwt-bearer";

    /**
     * PKCS7
     */
    String PKCS7_BEARER = "urn:cloud:idaas:params:oauth:client-assertion-type:pkcs7-bearer";
    /**
     * OIDC
     */
    String OIDC_BEARER = "urn:cloud:idaas:params:oauth:client-assertion-type:id-token-bearer";
}
