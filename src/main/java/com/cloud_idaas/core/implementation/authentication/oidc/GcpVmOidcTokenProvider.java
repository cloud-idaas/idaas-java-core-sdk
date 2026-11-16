package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.ServerException;
import com.cloud_idaas.core.exception.EncodingException;
import com.cloud_idaas.core.http.HttpClient;
import com.cloud_idaas.core.http.HttpClientFactory;
import com.cloud_idaas.core.http.HttpMethod;
import com.cloud_idaas.core.http.HttpRequest;
import com.cloud_idaas.core.http.HttpResponse;
import com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.StringUtil;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Obtains a Google Compute Engine VM identity token from the GCP metadata server.
 *
 * <p>The returned token is used as the OIDC assertion when an IDaaS application
 * is configured with a Google VM federated credential. The metadata server
 * issues the token for the supplied audience. The SDK factory uses the configured
 * IDaaS issuer's origin (scheme://host[:port]).</p>
 */
public class GcpVmOidcTokenProvider extends AbstractRefreshedCredentialProvider<String>
        implements OidcTokenProvider {

    private static final String GCP_METADATA_IDENTITY_URL_TEMPLATE =
            "http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/identity?audience=%s&format=full";
    /*
     * Refresh lead times are capped by the remaining lifetime for short-lived tokens.
     * CachedResultSupplier adds positive jitter; expiresAt supplies the independent
     * hard bound even when that jitter reaches the JWT expiration time.
     */
    private static final long STALE_TIME_ADVANCE_SECONDS = TimeUnit.MINUTES.toSeconds(10);

    private static final long PREFETCH_TIME_ADVANCE_SECONDS = TimeUnit.MINUTES.toSeconds(20);

    private static final HttpClient HTTP_CLIENT = request -> HttpClientFactory.getDefaultHttpClient().send(request);

    private final String metadataIdentityUrlTemplate;

    private final String audience;

    protected GcpVmOidcTokenProvider(GcpVmOidcTokenProviderBuilder builder) {
        super(builder);
        this.metadataIdentityUrlTemplate = builder.metadataIdentityUrlTemplate;
        this.audience = builder.audience;
    }

    public String getMetadataIdentityUrlTemplate() {
        return metadataIdentityUrlTemplate;
    }

    public String getAudience() {
        return audience;
    }

    @Override
    public String getOidcToken() {
        return this.getCachedResultSupplier().get();
    }

    @Override
    public RefreshResult<String> refreshCredential() {
        String encodedAudience;
        try {
            encodedAudience = URLEncoder.encode(this.audience, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(e.getMessage(), e);
        }

        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpConstants.GCP_METADATA_FLAVOR,
                Collections.singletonList(HttpConstants.GCP_METADATA_FLAVOR_VALUE));

        HttpRequest request = new HttpRequest.Builder()
                .url(String.format(this.metadataIdentityUrlTemplate, encodedAudience))
                .httpMethod(HttpMethod.GET)
                .headers(headers)
                .redactErrorBody(true)
                .build();
        final HttpResponse response;
        try {
            response = HTTP_CLIENT.send(request);
        } catch (ClientException | ServerException e) {
            // Default HTTP errors may contain the metadata body in both message and cause.
            String code = e instanceof ClientException ? ((ClientException) e).getErrorCode()
                    : ((ServerException) e).getErrorCode();
            String status = code != null && code.matches("[345][0-9]{2}") ? " with HTTP status " + code : "";
            throw new CredentialException("GcpMetadataRequestFailed", "GCP metadata request failed" + status);
        }
        if (!response.isSuccess()) {
            throw new CredentialException("GcpMetadataRequestFailed",
                    "GCP metadata request failed with HTTP status " + response.getStatusCode());
        }
        String oidcToken = response.getBody();
        if (StringUtil.isBlank(oidcToken)) {
            throw new CredentialException("EmptyGcpIdentityToken", "GCP metadata returned an empty identity token");
        }

        long expirationTime = getExpirationTime(oidcToken);
        long now = Instant.now().getEpochSecond();
        if (expirationTime <= now) {
            throw new CredentialException("ExpiredGcpIdentityToken", "GCP returned an expired identity token");
        }
        long lifetime = expirationTime - now;
        long staleAdvance = Math.min(STALE_TIME_ADVANCE_SECONDS, lifetime / 5);
        long prefetchAdvance = Math.min(PREFETCH_TIME_ADVANCE_SECONDS, lifetime / 3);
        return RefreshResult.builder(oidcToken)
                .expiresAt(Instant.ofEpochSecond(expirationTime))
                .staleTime(Instant.ofEpochSecond(expirationTime - staleAdvance))
                .prefetchTime(Instant.ofEpochSecond(expirationTime - prefetchAdvance))
                .build();
    }

    private long getExpirationTime(String oidcToken) {
        try {
            NumericDate expiration = new JwtConsumerBuilder()
                    .setSkipAllValidators()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    .build()
                    .processToClaims(oidcToken)
                    .getExpirationTime();
            if (expiration == null) {
                throw new CredentialException("MissingGcpIdentityTokenExpiration", "GCP identity token is missing exp");
            }
            return expiration.getValue();
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new CredentialException("InvalidGcpIdentityToken", "GCP identity token could not be parsed");
        }
    }

    public static GcpVmOidcTokenProviderBuilder builder() {
        return GcpVmOidcTokenProviderBuilder.aGcpVmOidcTokenProvider();
    }

    public static final class GcpVmOidcTokenProviderBuilder
            extends AbstractRefreshedCredentialProvider.BuilderImpl<GcpVmOidcTokenProvider, GcpVmOidcTokenProviderBuilder> {

        private String metadataIdentityUrlTemplate = GCP_METADATA_IDENTITY_URL_TEMPLATE;

        private String audience;

        private GcpVmOidcTokenProviderBuilder() {
        }

        private static GcpVmOidcTokenProviderBuilder aGcpVmOidcTokenProvider() {
            return new GcpVmOidcTokenProviderBuilder();
        }

        public GcpVmOidcTokenProviderBuilder metadataIdentityUrlTemplate(String metadataIdentityUrlTemplate) {
            if (StringUtil.isEmpty(metadataIdentityUrlTemplate)) {
                throw new IllegalArgumentException("metadataIdentityUrlTemplate cannot be empty");
            }
            this.metadataIdentityUrlTemplate = metadataIdentityUrlTemplate;
            return this;
        }

        public GcpVmOidcTokenProviderBuilder audience(String audience) {
            if (StringUtil.isEmpty(audience)) {
                throw new IllegalArgumentException("audience cannot be empty");
            }
            this.audience = audience;
            return this;
        }

        public GcpVmOidcTokenProvider build() {
            if (StringUtil.isEmpty(this.audience)) {
                throw new IllegalArgumentException("audience cannot be empty");
            }
            return new GcpVmOidcTokenProvider(this);
        }
    }
}
