package com.cloud_idaas.core.http;

import okhttp3.internal.tls.OkHostnameVerifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DefaultHostnameVerifier implements HostnameVerifier {

    private boolean unsafeIgnoreSSLCert = false;

    private static final HostnameVerifier NOOP_INSTANCE = new DefaultHostnameVerifier(true);

    private DefaultHostnameVerifier(boolean unsafeIgnoreSSLCert) {
        this.unsafeIgnoreSSLCert = unsafeIgnoreSSLCert;
    }

    public static HostnameVerifier getInstance(boolean unsafeIgnoreSSLCert) {
        return (HostnameVerifier)(unsafeIgnoreSSLCert ? NOOP_INSTANCE : OkHostnameVerifier.INSTANCE);
    }

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return this.unsafeIgnoreSSLCert;
    }
}