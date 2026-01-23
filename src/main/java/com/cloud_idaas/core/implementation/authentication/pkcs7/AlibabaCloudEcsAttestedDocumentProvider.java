package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.EncodingException;
import com.cloud_idaas.core.http.*;
import com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider;
import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;
import com.cloud_idaas.core.util.JSONUtil;
import com.cloud_idaas.core.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlibabaCloudEcsAttestedDocumentProvider extends AbstractRefreshedCredentialProvider<String> implements Pkcs7AttestedDocumentProvider {

    private static final String ECS_META_SERVER_PKCS7_TOKEN_URL = "http://100.100.100.200/latest/api/token";

    private static final String ECS_META_SERVER_PKCS7_URL_TEMPLATE = "http://100.100.100.200/latest/dynamic/instance-identity/pkcs7?audience=%s";

    private final String metaServerUrlTemplate;

    private String idaasInstanceId;

    private transient long signingTime = getNow();

    private long defaultDocumentEffectiveSeconds = 3600L;

    private static final HttpClient HTTP_CLIENT = HttpClientFactory.getDefaultHttpClient();

    protected AlibabaCloudEcsAttestedDocumentProvider(AlibabaCloudEcsAttestedDocumentProviderBuilder builder) {
        super(builder);
        this.metaServerUrlTemplate = builder.metaServerPkcs7UrlTemplate;
        this.idaasInstanceId = builder.idaasInstanceId;
        this.defaultDocumentEffectiveSeconds = builder.defaultDocumentEffectiveSeconds;
    }

    public String getMetaServerUrlTemplate() {
        return metaServerUrlTemplate;
    }

    public String getIdaasInstanceId() {
        return idaasInstanceId;
    }

    public void setIdaasInstanceId(String idaasInstanceId) {
        this.idaasInstanceId = idaasInstanceId;
    }

    public long getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(long signingTime) {
        this.signingTime = signingTime;
    }

    public long getDefaultDocumentEffectiveSeconds() {
        return defaultDocumentEffectiveSeconds;
    }

    public void setDefaultDocumentEffectiveSeconds(long defaultDocumentEffectiveSeconds) {
        this.defaultDocumentEffectiveSeconds = defaultDocumentEffectiveSeconds;
    }

    @Override
    public String getAttestedDocument() {
        return this.getCachedResultSupplier().get();
    }

    private long getNow() {
        // 要求时间戳为unix时间戳格式，单位为second
        return System.currentTimeMillis() / 1000L;
    }

    @Override
    public RefreshResult<String> refreshCredential() {
        Map<String, Object> audienceValue = new HashMap<>();
        audienceValue.put("aud", this.idaasInstanceId);
        this.signingTime = getNow();
        audienceValue.put("signingTime", this.signingTime);
        final String audienceParameterValue;
        try {
            audienceParameterValue = URLEncoder.encode(JSONUtil.toJSONString(audienceValue), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new EncodingException(e.getMessage(),  e);
        }
        Map<String, List<String>> tokenHeaders = new HashMap<>();
        tokenHeaders.put(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN_TTL_SECONDS, Collections.singletonList(String.valueOf(this.defaultDocumentEffectiveSeconds)));
        HttpRequest tokenRequest = new HttpRequest.Builder()
                .url(ECS_META_SERVER_PKCS7_TOKEN_URL)
                .httpMethod(HttpMethod.PUT)
                .headers(tokenHeaders)
                .build();
        HttpResponse tokenResponse = HTTP_CLIENT.send(tokenRequest);
        String token = tokenResponse.getBody();
        Map<String, List<String>> docHeaders = new HashMap<>();
        docHeaders.put(HttpConstants.X_ALIYUN_ECS_METADATA_TOKEN, Collections.singletonList(token));
        HttpRequest docRequest= new HttpRequest.Builder()
                .url(String.format(this.metaServerUrlTemplate, audienceParameterValue))
                .httpMethod(HttpMethod.GET)
                .headers(docHeaders)
                .build();
        HttpResponse docResponse = HTTP_CLIENT.send(docRequest);
        return RefreshResult.builder(docResponse.getBody())
                .staleTime(Instant.ofEpochSecond(this.signingTime + this.defaultDocumentEffectiveSeconds))
                .prefetchTime(Instant.ofEpochSecond(this.signingTime + this.defaultDocumentEffectiveSeconds / 2))
                .build();
    }

    public static AlibabaCloudEcsAttestedDocumentProviderBuilder builder() {
        return AlibabaCloudEcsAttestedDocumentProviderBuilder.anAlibabaCloudEcsAttestedDocumentProvider();
    }

    public static final class AlibabaCloudEcsAttestedDocumentProviderBuilder
            extends AbstractRefreshedCredentialProvider.BuilderImpl<AlibabaCloudEcsAttestedDocumentProvider, AlibabaCloudEcsAttestedDocumentProviderBuilder> {

        private String metaServerPkcs7UrlTemplate = ECS_META_SERVER_PKCS7_URL_TEMPLATE;
        private String idaasInstanceId;
        private long defaultDocumentEffectiveSeconds = 3600L;

        private AlibabaCloudEcsAttestedDocumentProviderBuilder() {}

        private static AlibabaCloudEcsAttestedDocumentProviderBuilder anAlibabaCloudEcsAttestedDocumentProvider() {return new AlibabaCloudEcsAttestedDocumentProviderBuilder();}

        public AlibabaCloudEcsAttestedDocumentProviderBuilder metaServerPkcs7UrlTemplate(String metaServerPkcs7UrlTemplate) {
            if (StringUtil.isEmpty(metaServerPkcs7UrlTemplate)) {
                throw new IllegalArgumentException("metaServerUrl cannot be empty");
            }

            this.metaServerPkcs7UrlTemplate = metaServerPkcs7UrlTemplate;
            return this;
        }

        public AlibabaCloudEcsAttestedDocumentProviderBuilder idaasInstanceId(String idaasInstanceId) {
            if (StringUtil.isEmpty(idaasInstanceId)) {
                throw new IllegalArgumentException("idaasInstanceId cannot be empty");
            }

            this.idaasInstanceId = idaasInstanceId;
            return this;
        }

        public AlibabaCloudEcsAttestedDocumentProviderBuilder defaultDocumentEffectiveSeconds(long defaultDocumentEffectiveSeconds) {
            if (defaultDocumentEffectiveSeconds <= 1200 || defaultDocumentEffectiveSeconds > 1314000) {
                throw new IllegalArgumentException("defaultDocumentEffectiveSeconds must be greater than 1200 and less than 1314000");
            }

            this.defaultDocumentEffectiveSeconds = defaultDocumentEffectiveSeconds;
            return this;
        }

        public AlibabaCloudEcsAttestedDocumentProvider build() {
            return new AlibabaCloudEcsAttestedDocumentProvider(this);
        }
    }
}
