package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.domain.constants.AuthenticationConstants;
import com.cloud_idaas.core.util.StringUtil;

/**
 * File-backed Alibaba Cloud ACK RRSA OIDC identity.
 * Reuses the existing file cache and rotation behavior without a second cache layer.
 *
 * @author Cloud IDaaS
 * @since 0.0.8
 */
public class AlibabaCloudAckOidcTokenProvider extends FileOidcTokenProvider {

    /** @param builder provider settings */
    protected AlibabaCloudAckOidcTokenProvider(AlibabaCloudAckOidcTokenProviderBuilder builder) {
        super(builder.resolveTokenFilePath());
    }

    /** @return a builder using an explicit path, the platform environment variable, then its default path */
    public static AlibabaCloudAckOidcTokenProviderBuilder builder() {
        return new AlibabaCloudAckOidcTokenProviderBuilder();
    }

    /** Builder for the platform-specific file identity. */
    public static final class AlibabaCloudAckOidcTokenProviderBuilder {
        private String oidcTokenFilePath;

        private AlibabaCloudAckOidcTokenProviderBuilder() {
        }

        /**
         * @param path resolved token file path
         * @return this builder
         */
        public AlibabaCloudAckOidcTokenProviderBuilder oidcTokenFilePath(String path) {
            if (StringUtil.isBlank(path)) {
                throw new IllegalArgumentException("oidcTokenFilePath cannot be blank");
            }
            this.oidcTokenFilePath = path;
            return this;
        }

        private String resolveTokenFilePath() {
            if (oidcTokenFilePath != null) {
                return oidcTokenFilePath;
            }
            return StringUtil.defaultIfBlank(System.getenv(AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV),
                    AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_DEFAULT_PATH);
        }

        /** @return the file-backed provider */
        public AlibabaCloudAckOidcTokenProvider build() {
            return new AlibabaCloudAckOidcTokenProvider(this);
        }
    }
}
