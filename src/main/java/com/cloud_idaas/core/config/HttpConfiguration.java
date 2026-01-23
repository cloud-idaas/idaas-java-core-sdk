package com.cloud_idaas.core.config;

import java.io.Serializable;

public class HttpConfiguration implements Serializable {

    private static final long serialVersionUID = 785896788594865623L;

    private int connectTimeout = 5000;

    private int readTimeout = 10000;

    private boolean unsafeIgnoreSSLCert;

    public HttpConfiguration() {
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean getUnsafeIgnoreSSLCert() {
        return unsafeIgnoreSSLCert;
    }

    public void setUnsafeIgnoreSSLCert(boolean unsafeIgnoreSSLCert) {
        this.unsafeIgnoreSSLCert = unsafeIgnoreSSLCert;
    }

    public static HttpConfiguration copy(HttpConfiguration source) {
        if (source == null){
            return null;
        }
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setConnectTimeout(source.getConnectTimeout());
        httpConfiguration.setReadTimeout(source.getReadTimeout());
        httpConfiguration.setUnsafeIgnoreSSLCert(source.getUnsafeIgnoreSSLCert());
        return httpConfiguration;
    }
}
