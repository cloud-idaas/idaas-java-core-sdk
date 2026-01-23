package com.cloud_idaas.core.provider;

@FunctionalInterface
public interface OidcTokenProvider {

    String getOidcToken();
}
