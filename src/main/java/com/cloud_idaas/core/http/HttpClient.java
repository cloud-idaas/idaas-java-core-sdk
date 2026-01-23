package com.cloud_idaas.core.http;

public interface HttpClient {

    /**
     * Send HTTP request
     * @param request
     * @return
     */
    HttpResponse send(HttpRequest request);
}
