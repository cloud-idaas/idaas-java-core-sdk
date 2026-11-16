package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.http.*;
import com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider;
import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwsEc2Pkcs7AttestedDocumentProvider extends AbstractRefreshedCredentialProvider<String> implements Pkcs7AttestedDocumentProvider {

    private static final String EC2_META_SERVER_TOKEN_URL = "http://169.254.169.254/latest/api/token";

    // IDaaS AWS trust sources verify the RSA-2048 CMS signature, not the legacy DSA endpoint.
    private static final String EC2_META_SERVER_PKCS7_URL = "http://169.254.169.254/latest/dynamic/instance-identity/rsa2048";

    private static final long DEFAULT_TOKEN_TTL_SECONDS = 21600L;

    private final long tokenTtlSeconds;

    private static final HttpClient HTTP_CLIENT = request -> HttpClientFactory.getDefaultHttpClient().send(request);

    /**
     * Creates a provider with the default IMDSv2 token TTL.
     * Retains the no-argument constructor exposed by earlier SDK versions.
     */
    public AwsEc2Pkcs7AttestedDocumentProvider() {
        this(builder());
    }

    protected AwsEc2Pkcs7AttestedDocumentProvider(AwsEc2Pkcs7AttestedDocumentProviderBuilder builder) {
        super(builder);
        this.tokenTtlSeconds = builder.tokenTtlSeconds;
    }

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds;
    }

    @Override
    public String getAttestedDocument() {
        return this.getCachedResultSupplier().get();
    }

    @Override
    public RefreshResult<String> refreshCredential() {
        Map<String, List<String>> tokenHeaders = new HashMap<>();
        tokenHeaders.put(HttpConstants.X_AWS_EC2_METADATA_TOKEN_TTL_SECONDS, Collections.singletonList(String.valueOf(this.tokenTtlSeconds)));
        HttpRequest tokenRequest = new HttpRequest.Builder()
                .url(EC2_META_SERVER_TOKEN_URL)
                .httpMethod(HttpMethod.PUT)
                .headers(tokenHeaders)
                .build();
        HttpResponse tokenResponse = HTTP_CLIENT.send(tokenRequest);
        String token = tokenResponse.getBody();
        if (token != null) {
            token = token.trim();
        }
        if (token == null || token.isEmpty()) {
            throw new CredentialException("EmptyEc2MetadataToken", "EC2 returned an empty IMDSv2 session token");
        }

        Map<String, List<String>> docHeaders = new HashMap<>();
        docHeaders.put(HttpConstants.X_AWS_EC2_METADATA_TOKEN, Collections.singletonList(token));
        HttpRequest docRequest = new HttpRequest.Builder()
                .url(EC2_META_SERVER_PKCS7_URL)
                .httpMethod(HttpMethod.GET)
                .headers(docHeaders)
                .build();
        HttpResponse docResponse = HTTP_CLIENT.send(docRequest);
        String document = docResponse.getBody();
        if (document != null) {
            // Metadata responses contain wrapped Base64; IDaaS expects a single-line assertion.
            document = document.replaceAll("\\s+", "");
        }
        if (document == null || document.isEmpty()) {
            throw new CredentialException("EmptyEc2IdentityDocument", "EC2 returned an empty RSA-2048 identity document");
        }

        long now = System.currentTimeMillis() / 1000L;
        return RefreshResult.builder(document)
                .staleTime(Instant.ofEpochSecond(now + this.tokenTtlSeconds))
                .prefetchTime(Instant.ofEpochSecond(now + this.tokenTtlSeconds / 2))
                .build();
    }

    public static AwsEc2Pkcs7AttestedDocumentProviderBuilder builder() {
        return AwsEc2Pkcs7AttestedDocumentProviderBuilder.anAwsEc2Pkcs7AttestedDocumentProvider();
    }

    public static final class AwsEc2Pkcs7AttestedDocumentProviderBuilder
            extends AbstractRefreshedCredentialProvider.BuilderImpl<AwsEc2Pkcs7AttestedDocumentProvider, AwsEc2Pkcs7AttestedDocumentProviderBuilder> {

        private long tokenTtlSeconds = DEFAULT_TOKEN_TTL_SECONDS;

        private AwsEc2Pkcs7AttestedDocumentProviderBuilder() {}

        private static AwsEc2Pkcs7AttestedDocumentProviderBuilder anAwsEc2Pkcs7AttestedDocumentProvider() {
            return new AwsEc2Pkcs7AttestedDocumentProviderBuilder();
        }

        public AwsEc2Pkcs7AttestedDocumentProviderBuilder tokenTtlSeconds(long tokenTtlSeconds) {
            if (tokenTtlSeconds < 1 || tokenTtlSeconds > DEFAULT_TOKEN_TTL_SECONDS) {
                throw new IllegalArgumentException("tokenTtlSeconds must be between 1 and " + DEFAULT_TOKEN_TTL_SECONDS);
            }

            this.tokenTtlSeconds = tokenTtlSeconds;
            return this;
        }

        public AwsEc2Pkcs7AttestedDocumentProvider build() {
            return new AwsEc2Pkcs7AttestedDocumentProvider(this);
        }
    }
}
