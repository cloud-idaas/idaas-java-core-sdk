package com.cloud_idaas.core.implementation.authentication.pkcs7;

import com.cloud_idaas.core.provider.Pkcs7AttestedDocumentProvider;

public class StaticPkcs7AttestedDocumentProvider implements Pkcs7AttestedDocumentProvider {

    private String attestedDocument;

    public StaticPkcs7AttestedDocumentProvider() {
    }

    public StaticPkcs7AttestedDocumentProvider(String attestedDocument) {
        this.attestedDocument = attestedDocument;
    }

    @Override
    public String getAttestedDocument() {
        return this.attestedDocument;
    }

    public void setAttestedDocument(String attestedDocument) {
        this.attestedDocument = attestedDocument;
    }
}
