package com.cloud_idaas.core.implementation.authentication.jwt;

import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.provider.JwtClientAssertionProvider;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

public class StaticClientSecretAssertionProvider implements JwtClientAssertionProvider {

    private final Supplier<String> clientSecretSupplier;

    private String clientId;
    private String tokenEndpoint;
    private String scope;

    public StaticClientSecretAssertionProvider(Supplier<String> clientSecretSupplier) {
        this.clientSecretSupplier = clientSecretSupplier;
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
        jws.setKey(new HmacKey(clientSecretSupplier.get().getBytes(StandardCharsets.UTF_8)));

        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new CredentialException(e.getMessage(), e);
        }
    }
}
