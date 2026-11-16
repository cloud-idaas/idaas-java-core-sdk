package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.domain.constants.AuthenticationConstants;
import com.cloud_idaas.core.util.StringUtil;

/**
 * File-backed Kubernetes service account OIDC identity.
 * Reuses the existing file cache and rotation behavior without a second cache layer.
 *
 * @author Cloud IDaaS
 * @since 0.0.8
 */
public class KubernetesOidcTokenProvider extends FileOidcTokenProvider {

    /** @param builder provider settings */
    protected KubernetesOidcTokenProvider(KubernetesOidcTokenProviderBuilder builder) {
        super(builder.oidcTokenFilePath);
    }

    /** @return a builder using the platform's default token path */
    public static KubernetesOidcTokenProviderBuilder builder() {
        return new KubernetesOidcTokenProviderBuilder();
    }

    /** Builder for the platform-specific file identity. */
    public static final class KubernetesOidcTokenProviderBuilder {
        private String oidcTokenFilePath = AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH;

        private KubernetesOidcTokenProviderBuilder() {
        }

        /**
         * @param path resolved token file path
         * @return this builder
         */
        public KubernetesOidcTokenProviderBuilder oidcTokenFilePath(String path) {
            if (StringUtil.isBlank(path)) {
                throw new IllegalArgumentException("oidcTokenFilePath cannot be blank");
            }
            this.oidcTokenFilePath = path;
            return this;
        }

        /** @return the file-backed provider */
        public KubernetesOidcTokenProvider build() {
            return new KubernetesOidcTokenProvider(this);
        }
    }
}
