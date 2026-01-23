package com.cloud_idaas.core.http;

import java.io.Serializable;
import java.util.Map;

public class HttpResponse implements Serializable {

    private static final long serialVersionUID = 452235848368513340L;

    private int statusCode;

    private Map<String, String> headers;

    private String body;

    private HttpResponse() {
    }

    public HttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }
}
