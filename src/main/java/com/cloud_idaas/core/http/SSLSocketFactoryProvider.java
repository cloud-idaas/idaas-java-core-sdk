package com.cloud_idaas.core.http;

import com.cloud_idaas.core.exception.HttpException;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSLSocketFactoryProvider {

    //获取 SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory(boolean unsafeIgnoreSSLCert) {
        try {
            X509TrustManager compositeX509TrustManager = getX509TrustManager(unsafeIgnoreSSLCert);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, new TrustManager[] {compositeX509TrustManager}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public static X509TrustManager getX509TrustManager(boolean unsafeIgnoreSSLCert){
        try {
            X509TrustManagerImp compositeX509TrustManager;
            if (unsafeIgnoreSSLCert) {
                compositeX509TrustManager = new X509TrustManagerImp(true);
            } else {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore)null);
                List<TrustManager> trustManagerList = new ArrayList(Arrays.asList(trustManagerFactory.getTrustManagers()));
                List<X509TrustManager> finalTrustManagerList = new ArrayList();

                for (TrustManager trustManager : trustManagerList) {
                    if (trustManager instanceof X509TrustManager) {
                        finalTrustManagerList.add((X509TrustManager)trustManager);
                    }
                }
                compositeX509TrustManager = new X509TrustManagerImp(finalTrustManagerList);
            }
            return compositeX509TrustManager;
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public static HostnameVerifier getHostnameVerifier(boolean unsafeIgnoreSSLCert) {
        return DefaultHostnameVerifier.getInstance(unsafeIgnoreSSLCert);
    }
}
