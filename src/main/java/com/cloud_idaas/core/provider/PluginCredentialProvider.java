package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;

public interface PluginCredentialProvider {

    String getName();

    IDaaSTokenResponse getIDaaSCredential(String scope);
}
