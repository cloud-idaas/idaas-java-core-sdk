package com.cloud_idaas.core.http;

import okhttp3.FormBody;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class HttpRequest implements Serializable {

    private static final long serialVersionUID = 1805486047681541L;

    private HttpMethod method;

    private String url;

    private Map<String, List<String>> headers;

    private String body;

    private Map<String, List<String>> formBody;

    private ContentType contentType;

    public HttpRequest() {
    }

    public HttpRequest(Builder  builder){
        this.method = builder.method;
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.formBody = builder.formBody;
        this.contentType = builder.contentType;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, List<String>> getFormBody() {
        return formBody;
    }

    public void setFormBody(Map<String, List<String>> formBody) {
        this.formBody = formBody;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public static final class Builder {
        private HttpMethod method;
        private String url;
        private Map<String, List<String>> headers;
        private String body;
        private Map<String, List<String>> formBody;
        private ContentType contentType;

        public Builder httpMethod(HttpMethod method){
            this.method = method;
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers){
            this.headers = headers;
            return this;
        }

        public Builder body(String body){
            this.body = body;
            return this;
        }

        public Builder formBody(Map<String, List<String>> formBody){
            this.formBody = formBody;
            return this;
        }

        public Builder contentType(ContentType contentType){
            this.contentType = contentType;
            return this;
        }

        public HttpRequest build(){
            return new HttpRequest(this);
        }
    }
}
