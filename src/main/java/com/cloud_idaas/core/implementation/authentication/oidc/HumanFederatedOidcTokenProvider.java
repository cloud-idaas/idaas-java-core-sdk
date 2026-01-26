package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.DeviceCodeResponse;
import com.cloud_idaas.core.domain.constants.ConfigPathConstants;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.http.OAuth2TokenUtil;
import com.cloud_idaas.core.implementation.AbstractRefreshedCredentialProvider;
import com.cloud_idaas.core.provider.OidcTokenProvider;
import com.cloud_idaas.core.util.*;
import org.apache.commons.lang3.exception.UncheckedInterruptedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HumanFederatedOidcTokenProvider extends AbstractRefreshedCredentialProvider<IDaaSTokenResponse> implements OidcTokenProvider {

    private static final int MAX_WAIT_INTERVAL_SECONDS = 180;

    private final TokenAuthnMethod authnMethod = TokenAuthnMethod.NONE;
    private final String clientId;
    private final String scope = "openid offline_access";
    private final String tokenEndpoint;
    private final String deviceAuthorizationEndpoint;
    private transient RefreshResult<IDaaSTokenResponse> refreshResult;
    private final AtomicBoolean firstStartup = new AtomicBoolean(true);
    private final String localHumanCachePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(HumanFederatedOidcTokenProvider.class);

    private HumanFederatedOidcTokenProvider(IDaaSHumanOIDCTokenProviderBuilder builder) {
        super(builder);
        if (StringUtil.isBlank(builder.clientId)) {
            throw new IllegalArgumentException("clientId is blank");
        }
        if (StringUtil.isBlank(builder.tokenEndpoint)) {
            throw new IllegalArgumentException("tokenEndpoint is blank");
        }

        this.clientId = builder.clientId;
        this.tokenEndpoint = builder.tokenEndpoint;
        this.deviceAuthorizationEndpoint = builder.deviceAuthorizationEndpoint;

        String envPath = System.getenv(ConfigPathConstants.ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY);
        if (StringUtil.isNoneBlank(envPath)) {
            this.localHumanCachePath = envPath;
        } else {
            this.localHumanCachePath = String.format(ConfigPathConstants.DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE,
                    IDaaSCredentialProviderFactory.getIDaasInstanceId(), this.clientId);
        }

        // To prevent concurrent operation, get credential when constructor
        this.cachedResultSupplier.get();
    }

    public TokenAuthnMethod getAuthnMethod() {
        return authnMethod;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getDeviceAuthorizationEndpoint() {
        return deviceAuthorizationEndpoint;
    }

    public static IDaaSHumanOIDCTokenProviderBuilder builder() {
        return HumanFederatedOidcTokenProvider.IDaaSHumanOIDCTokenProviderBuilder.anIDaaSHumanOIDCTokenProvider();
    }

    @Override
    public RefreshResult<IDaaSTokenResponse> refreshCredential() {
        if (firstStartup.get()) {
            RefreshResult<IDaaSTokenResponse> localCacheResult = tryReuseLocalCache();
            if (localCacheResult != null) {
                this.firstStartup.compareAndSet(true, false);
                return localCacheResult;
            }
            // Get token use device authorization
            IDaaSTokenResponse tokenResponse = getTokenWithDeviceAuthorization();
            // staleTime: 4/5 of expiresIn, prefetchTime: 2/3 of expiresIn
            Instant staleTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 5));
            Instant prefetchTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 3));
            this.firstStartup.compareAndSet(true, false);
            LOGGER.info("token authn time: {}", Clock.systemUTC().instant().toEpochMilli());
            FileUtil.writeFile(this.localHumanCachePath, JSONUtil.toJSONString(tokenResponse));
            refreshResult = RefreshResult.builder(tokenResponse)
                    .staleTime(staleTime)
                    .prefetchTime(prefetchTime)
                    .build();
            return refreshResult;
        } else {
            // refresh token must saved by itself and the cache result supplier cannot be used,
            // it will lead to repeat calls and stack overflow.
            final String refreshToken = refreshResult.getValue().getRefreshToken();
            IDaaSTokenResponse tokenResponse = refreshToken(refreshToken);
            Instant staleTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 5));
            Instant prefetchTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 3));
            LOGGER.info("token refresh time: {}", Clock.systemUTC().instant().toEpochMilli());
            // Every time refresh token, write to local cache
            FileUtil.writeFile(this.localHumanCachePath, JSONUtil.toJSONString(tokenResponse));
            refreshResult = RefreshResult.builder(tokenResponse)
                    .staleTime(staleTime)
                    .prefetchTime(prefetchTime)
                    .build();
            return refreshResult;
        }
    }

    private RefreshResult<IDaaSTokenResponse> tryReuseLocalCache() {
        // Application run first, try to use local cache, to avoid repeated human authentication
        try {
            String localFileCachedResult = FileUtil.readFile(this.localHumanCachePath);
            IDaaSTokenResponse localToken = JSONUtil.parseObject(localFileCachedResult, IDaaSTokenResponse.class);
            if (localToken != null) {
                ValidatorUtil.validateLocalToken(localToken);
                if (localToken.willSoonExpire()) {
                    final String refreshToken = localToken.getRefreshToken();
                    IDaaSTokenResponse tokenResponse = refreshToken(refreshToken);
                    Instant staleTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 5));
                    Instant prefetchTime = Instant.ofEpochSecond(tokenResponse.getExpiresAt() - TimeUnit.SECONDS.toSeconds(tokenResponse.getExpiresIn() / 3));
                    LOGGER.info("token refresh time: {}", Clock.systemUTC().instant().toString());
                    FileUtil.writeFile(this.localHumanCachePath, JSONUtil.toJSONString(tokenResponse));
                    refreshResult = RefreshResult.builder(tokenResponse)
                            .staleTime(staleTime)
                            .prefetchTime(prefetchTime)
                            .build();
                    return refreshResult;
                } else {
                    return RefreshResult.builder(localToken)
                            .staleTime(Instant.ofEpochSecond(localToken.getExpiresAt() - TimeUnit.SECONDS.toSeconds(localToken.getExpiresIn() / 5)))
                            .prefetchTime(Instant.ofEpochSecond(localToken.getExpiresAt() - TimeUnit.SECONDS.toSeconds(localToken.getExpiresIn() / 3)))
                            .build();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Read local cache failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public String getOidcToken() {
        return cachedResultSupplier.get().getAccessToken();
    }

    private IDaaSTokenResponse refreshToken(String refreshToken) {
        if (StringUtil.isEmpty(refreshToken)) {
            throw new CredentialException(ErrorCode.REFRESH_TOKEN_EMPTY.getCode(), "refresh token is empty");
        }
        return OAuth2TokenUtil.refreshToken(clientId, refreshToken, tokenEndpoint);
    }

    private IDaaSTokenResponse getTokenWithDeviceAuthorization() {
        final DeviceCodeResponse deviceCodeResponse;
        deviceCodeResponse = OAuth2TokenUtil.getDeviceCode(clientId, scope, this.deviceAuthorizationEndpoint);

        try {
            // Print relevant information first, then help the user open manually
            LOGGER.info("Open the verification URL in your browser: {}", deviceCodeResponse.getVerificationUriComplete());
            BrowserUtil.open(new URI(deviceCodeResponse.getVerificationUriComplete()));

        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Open browser failed: {}, Please open the verification URL in your browser: {}", e.getMessage(), deviceCodeResponse.getVerificationUriComplete());
        }

        try {
            final long pollInterval = (deviceCodeResponse.getInterval() == null ? 5 : deviceCodeResponse.getInterval());
            IDaaSTokenResponse token;
            for (int i = 0; ; i++) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(pollInterval));
                token = OAuth2TokenUtil.getTokenByDeviceCode(this.clientId, deviceCodeResponse.getDeviceCode(), this.tokenEndpoint);
                if (token != null) {
                    break;
                }
                if (i >= MAX_WAIT_INTERVAL_SECONDS / pollInterval) {
                    LOGGER.error("waiting for token timeout.");
                    break;
                }
            }
            return token;
        } catch (InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }

    }

    public static final class IDaaSHumanOIDCTokenProviderBuilder
            extends AbstractRefreshedCredentialProvider.BuilderImpl<HumanFederatedOidcTokenProvider, IDaaSHumanOIDCTokenProviderBuilder> {
        private String clientId;
        private String tokenEndpoint;
        private String deviceAuthorizationEndpoint;

        private IDaaSHumanOIDCTokenProviderBuilder() {}

        private static IDaaSHumanOIDCTokenProviderBuilder anIDaaSHumanOIDCTokenProvider() {return new IDaaSHumanOIDCTokenProviderBuilder();}

        public IDaaSHumanOIDCTokenProviderBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public IDaaSHumanOIDCTokenProviderBuilder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public IDaaSHumanOIDCTokenProviderBuilder deviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
            this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
            return this;
        }

        public HumanFederatedOidcTokenProvider build() {
            return new HumanFederatedOidcTokenProvider(this);
        }
    }
}
