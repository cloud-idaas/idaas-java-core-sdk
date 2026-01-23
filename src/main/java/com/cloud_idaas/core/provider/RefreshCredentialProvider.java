package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.cache.RefreshResult;

public interface RefreshCredentialProvider<T> {

    RefreshResult<T> refreshCredential();
}
