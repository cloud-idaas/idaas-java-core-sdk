package com.cloud_idaas.core.util;

import com.cloud_idaas.core.config.IDaaSClientConfig;
import com.cloud_idaas.core.domain.constants.HttpConstants;

public class NormalizeUtil {

    public static void normalizeEndpoints(IDaaSClientConfig config) {
        config.setIssuer(convertHttps(config.getIssuer()));
        config.setTokenEndpoint(convertHttps(config.getTokenEndpoint()));
        config.setDeviceAuthorizationEndpoint(convertHttps(config.getDeviceAuthorizationEndpoint()));
        config.setDeveloperApiEndpoint(convertHttps(config.getDeveloperApiEndpoint()));
        config.setOpenApiEndpoint(convertHttps(config.getOpenApiEndpoint()));
    }

    private static String convertHttps(String endpoint) {
        if (endpoint == null || endpoint.isEmpty()) {
            return endpoint;
        }
        if (endpoint.startsWith(HttpConstants.HTTPS + HttpConstants.SCHEME_SEPARATOR)) {
            return endpoint;
        }
        if (endpoint.startsWith(HttpConstants.HTTP + HttpConstants.SCHEME_SEPARATOR)) {
            return HttpConstants.HTTPS + HttpConstants.SCHEME_SEPARATOR + endpoint.substring(7);
        }
        return HttpConstants.HTTPS + HttpConstants.SCHEME_SEPARATOR + endpoint;
    }
}
