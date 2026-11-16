package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.domain.constants.AuthenticationConstants;
import com.cloud_idaas.core.util.StringUtil;

/**
 * File-backed AWS EKS projected service account OIDC identity.
 * Reuses the existing file cache and rotation behavior without a second cache layer.
 *
 * @author Cloud IDaaS
 * @since 0.0.8
 */
public class AwsEksOidcTokenProvider extends FileOidcTokenProvider {

    /** @param builder provider settings */
    protected AwsEksOidcTokenProvider(AwsEksOidcTokenProviderBuilder builder) {
        super(builder.resolveTokenFilePath());
    }

    /** @return a builder using an explicit path, the platform environment variable, then its default path */
    public static AwsEksOidcTokenProviderBuilder builder() {
        return new AwsEksOidcTokenProviderBuilder();
    }

    /** Builder for the platform-specific file identity. */
    public static final class AwsEksOidcTokenProviderBuilder {
        private String oidcTokenFilePath;

        private AwsEksOidcTokenProviderBuilder() {
        }

        /**
         * @param path resolved token file path
         * @return this builder
         */
        public AwsEksOidcTokenProviderBuilder oidcTokenFilePath(String path) {
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
            return StringUtil.defaultIfBlank(System.getenv(AuthenticationConstants.AWS_EKS_OIDC_TOKEN_PATH_ENV),
                    AuthenticationConstants.AWS_EKS_OIDC_TOKEN_DEFAULT_PATH);
        }

        /** @return the file-backed provider */
        public AwsEksOidcTokenProvider build() {
            return new AwsEksOidcTokenProvider(this);
        }
    }
}
