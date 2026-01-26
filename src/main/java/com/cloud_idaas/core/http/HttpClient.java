package com.cloud_idaas.core.http;

public interface HttpClient {

    /**
     * Send HTTP request
     *
     * @param request HTTP request
     * @return HttpResponse HTTP response
     */
    HttpResponse send(HttpRequest request);
}
