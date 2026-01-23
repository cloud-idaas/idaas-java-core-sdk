package com.cloud_idaas.core.http;

import com.cloud_idaas.core.config.HttpConfiguration;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import okhttp3.OkHttpClient;

public class HttpClientFactory {

    private static final HttpConfiguration httpConfiguration = IDaaSCredentialProviderFactory.getHttpConfig();

    private static volatile HttpClient singletonOKHttpClient;

    private static final Object LOCK = new Object(); // Used for synchronization lock

    public static HttpClient getDefaultHttpClient() {
        if (singletonOKHttpClient == null) {
            synchronized (LOCK) {
                if (singletonOKHttpClient == null) {
                    singletonOKHttpClient = DefaultOKHttpClientImp.builder()
                            .connectTimeout(httpConfiguration.getConnectTimeout())
                            .readTimeout(httpConfiguration.getReadTimeout())
                            .build();
                }
            }
        }
        return singletonOKHttpClient;
    }
}
