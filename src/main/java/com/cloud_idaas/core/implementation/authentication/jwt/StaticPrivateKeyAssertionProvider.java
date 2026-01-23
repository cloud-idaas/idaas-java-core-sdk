package com.cloud_idaas.core.implementation.authentication.jwt;

import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import com.cloud_idaas.core.util.PkiUtil;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public class StaticPrivateKeyAssertionProvider implements JwtClientAssertionProvider {

    private final String privateKeyString;
    private final transient PrivateKey privateKey;

    private String clientId;
    private String tokenEndpoint;
    private String scope;

    public StaticPrivateKeyAssertionProvider(String privateKeyString) {
        this.privateKeyString = privateKeyString;
        try {
            this.privateKey = PkiUtil.parsePrivateKeyFromPem(privateKeyString);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CredentialException(e.getMessage(),  e);
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getClientAssertion() {
        final JwtClaims claims = new JwtClaims();
        claims.setAudience(tokenEndpoint);
        claims.setSubject(clientId);
        claims.setIssuer(clientId);
        claims.setJwtId(UUID.randomUUID().toString());
        claims.setIssuedAt(NumericDate.now());
        claims.setExpirationTimeMinutesInTheFuture(10);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(privateKey);

        if (privateKey instanceof ECPrivateKey) {
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);
        } else if (privateKey instanceof RSAPrivateKey) {
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        } else {
            throw new CredentialException(ErrorCode.NOT_SUPPORTED_WEB_KEY.getCode(), "Not supported web key: " + privateKey);
        }
        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new CredentialException(e.getMessage(),  e);
        }
    }
}
