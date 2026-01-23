package com.cloud_idaas.core.http;

import com.cloud_idaas.core.exception.HttpException;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class X509TrustManagerImp implements X509TrustManager{

    private List<X509TrustManager> trustManagers = new ArrayList();

    private boolean unsafeIgnoreSSLCert = false;

    public boolean isUnsafeIgnoreSSLCert() {
        return this.unsafeIgnoreSSLCert;
    }

    public X509TrustManagerImp(boolean unsafeIgnoreSSLCert) {
        this.unsafeIgnoreSSLCert = unsafeIgnoreSSLCert;
    }

    public X509TrustManagerImp(List<X509TrustManager> trustManagers) {
        this.trustManagers = trustManagers;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType){
        if (!this.unsafeIgnoreSSLCert) {
            for(X509TrustManager trustManager : this.trustManagers) {
                try {
                    trustManager.checkServerTrusted(chain, authType);
                    return;
                } catch (CertificateException e) {
                }
            }

            throw new HttpException("None of the TrustManagers trust this certificate chain");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> certificates = new ArrayList();

        for(X509TrustManager trustManager : this.trustManagers) {
            certificates.addAll(Arrays.asList(trustManager.getAcceptedIssuers()));
        }

        X509Certificate[] certificatesArray = new X509Certificate[certificates.size()];
        return (X509Certificate[])certificates.toArray(certificatesArray);
    }
}
