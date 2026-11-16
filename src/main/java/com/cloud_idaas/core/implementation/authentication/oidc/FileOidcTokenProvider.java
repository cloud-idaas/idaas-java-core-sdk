package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FileOidcTokenProvider implements OidcTokenProvider {

    private final String oidcTokenFilePath;

    private long expiresTime;
    private transient String oidcToken;

    public FileOidcTokenProvider(String oidcTokenFilePath) {
        this.oidcTokenFilePath = oidcTokenFilePath;
    }

    @Override
    public synchronized String getOidcToken() {
        if (oidcToken != null && !willSoonExpire()) {
            return oidcToken;
        } else {
            try {
                String candidate = new String(Files.readAllBytes(Paths.get(oidcTokenFilePath)), StandardCharsets.UTF_8);
                org.jose4j.jwt.NumericDate expiration = new JwtConsumerBuilder()
                        .setSkipAllValidators()
                        .setDisableRequireSignature()
                        .setSkipSignatureVerification()
                        .build()
                        .processToClaims(candidate)
                        .getExpirationTime();
                if (expiration == null) {
                    throw new CredentialException("MissingOidcTokenExpiration", "OIDC token is missing exp");
                }
                long candidateExpiry = expiration.getValue();
                if (candidateExpiry <= System.currentTimeMillis() / 1000) {
                    throw new CredentialException("ExpiredOidcToken", "OIDC token is expired");
                }
                // Publish only after parsing and expiration checks have succeeded.
                expiresTime = candidateExpiry;
                oidcToken = candidate;
            } catch (IOException e) {
                // Parser/IO messages can contain token content or private paths. Keep only the safe category.
                throw new CredentialException("InvalidOidcToken",
                        "OIDC token file could not be read (" + e.getClass().getSimpleName() + ")");
            } catch (InvalidJwtException | MalformedClaimException e) {
                throw new CredentialException("InvalidOidcToken", "OIDC token file could not be parsed");
            }
        }
        return oidcToken;
    }

    public String getOidcTokenFilePath() {
        return oidcTokenFilePath;
    }

    private boolean willSoonExpire() {
        final long now = System.currentTimeMillis() / 1000;
        return TimeUnit.MINUTES.toSeconds(10) > (this.expiresTime - now);
    }
}
