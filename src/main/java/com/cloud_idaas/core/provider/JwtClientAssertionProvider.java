package com.cloud_idaas.core.provider;

@FunctionalInterface
public interface JwtClientAssertionProvider {

    String getClientAssertion();
}
