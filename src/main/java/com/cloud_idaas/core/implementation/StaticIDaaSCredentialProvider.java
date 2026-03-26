package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.credential.IDaaSCredential;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;

public class StaticIDaaSCredentialProvider implements IDaaSCredentialProvider {

    private IDaaSCredential credential;

    private StaticIDaaSCredentialProvider(StaticCredentialProviderBuilder builder) {
        this.credential = builder.credential;
    }

    @Override
    public IDaaSCredential getCredential() {
        return credential;
    }

    public static StaticCredentialProviderBuilder builder() {
        return new StaticCredentialProviderBuilder();
    }

    public static final class StaticCredentialProviderBuilder {
        private IDaaSCredential credential;

        public StaticCredentialProviderBuilder setCredential(IDaaSCredential credential) {
            this.credential = credential;
            return this;
        }

        public StaticIDaaSCredentialProvider build() {
            return new StaticIDaaSCredentialProvider(this);
        }
    }
}
