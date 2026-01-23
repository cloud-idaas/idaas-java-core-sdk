package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.FileUtil;
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
    public String getOidcToken() {
        if (oidcToken != null && !willSoonExpire()) {
            return oidcToken;
        } else {
            try {
                oidcToken = FileUtil.readFile(oidcTokenFilePath);
                expiresTime = new JwtConsumerBuilder()
                        .setSkipAllValidators()
                        .setDisableRequireSignature()
                        .setSkipSignatureVerification()
                        .build()
                        .processToClaims(oidcToken)
                        .getExpirationTime()
                        .getValue();
            } catch (InvalidJwtException | MalformedClaimException | IOException e) {
                throw new CredentialException(e.getMessage(), e);
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
