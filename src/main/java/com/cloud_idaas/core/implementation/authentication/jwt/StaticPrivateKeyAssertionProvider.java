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

        final String algorithm;
        if (privateKey instanceof RSAPrivateKey) {
            int keySize = ((RSAPrivateKey) privateKey).getModulus().bitLength();
            if (keySize <= 2048) {
                algorithm = AlgorithmIdentifiers.RSA_USING_SHA256;
            } else if (keySize <= 3072) {
                algorithm = AlgorithmIdentifiers.RSA_USING_SHA384;
            } else {
                algorithm = AlgorithmIdentifiers.RSA_USING_SHA512;
            }
        } else if (privateKey instanceof ECPrivateKey) {
            int orderBitLength = ((ECPrivateKey) privateKey).getParams().getOrder().bitLength();
            if (orderBitLength <= 256) {
                algorithm = AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256;
            } else if (orderBitLength <= 384) {
                algorithm = AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384;
            } else if (orderBitLength <= 521) {
                algorithm = AlgorithmIdentifiers.ECDSA_USING_P521_CURVE_AND_SHA512;
            } else {
                throw new CredentialException(ErrorCode.NOT_SUPPORTED_WEB_KEY.getCode(),
                        "Unsupported EC curve with order bit length: " + orderBitLength);
            }
        } else {
            throw new CredentialException(ErrorCode.NOT_SUPPORTED_WEB_KEY.getCode(), "Not supported web key: " + privateKey);
        }

        jws.setAlgorithmHeaderValue(algorithm);
        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new CredentialException(e.getMessage(),  e);
        }
    }
}
