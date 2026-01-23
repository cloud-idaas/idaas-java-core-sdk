package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;

public class AwsEc2Pkcs7AttestedDocumentProvider implements Pkcs7AttestedDocumentProvider {

    @Override
    public String getAttestedDocument() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
