package com.cloud_idaas.core.http;

import com.cloud_idaas.core.config.UserAgentConfig;
import com.cloud_idaas.core.domain.ErrResponse;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.HttpException;
import com.cloud_idaas.core.exception.ServerException;
import com.cloud_idaas.core.util.ExceptionAnalyzer;
import com.cloud_idaas.core.util.JSONUtil;
import com.cloud_idaas.core.util.StringUtil;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class DefaultOKHttpClientImp implements HttpClient{

    private int connectTimeout;

    private int readTimeout;

    private final OkHttpClient okHttpClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOKHttpClientImp.class);

    public DefaultOKHttpClientImp(Builder builder){
        this.connectTimeout = builder.connectTimeout == null ? 5000 : builder.connectTimeout;
        this.readTimeout = builder.readTimeout == null ? 10000 : builder.readTimeout;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public HttpResponse send(HttpRequest httpRequest) {
        Request.Builder requestBuilder = new Request.Builder();
        addHeaderToRequest(requestBuilder, httpRequest);
        HttpMethod method = httpRequest.getMethod();
        Request request;
        if (method == HttpMethod.GET){
            request = requestBuilder.url(httpRequest.getUrl()).get().build();
        } else if (method == HttpMethod.POST){
            RequestBody requestBody = buildRequestBody(httpRequest);
            request = requestBuilder.url(httpRequest.getUrl()).post(requestBody).build();
        } else {
            RequestBody requestBody = buildRequestBody(httpRequest);
            request = requestBuilder.url(httpRequest.getUrl()).put(requestBody).build();
        }
        return executeRequest(request);
    }

    private void addHeaderToRequest(Request.Builder requestBuilder, HttpRequest httpRequest) {
        Map<String, List<String>> headers = httpRequest.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (String key : headers.keySet()) {
            requestBuilder.addHeader(key, String.join(",", headers.get(key)));
        }
        requestBuilder.addHeader(HttpConstants.USER_AGENT, UserAgentConfig.getUserAgentMessage());
    }

    private RequestBody buildRequestBody(HttpRequest httpRequest){
        if (httpRequest.getContentType() == null){
                return RequestBody.create(new byte[]{}, null);
        } else if (httpRequest.getContentType() == ContentType.FORM){
            Map<String, List<String>> formBodyParams = httpRequest.getFormBody();
            if (formBodyParams == null || formBodyParams.isEmpty()){
                return RequestBody.create(new byte[]{}, MediaType.parse(httpRequest.getContentType().getType()));
            } else {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for (String key : formBodyParams.keySet()) {
                    formBodyBuilder.add(key,  String.join(",", formBodyParams.get(key)));
                }
                return formBodyBuilder.build();
            }
        } else {
            if (StringUtil.isEmpty(httpRequest.getBody())){
                return RequestBody.create(new byte[]{}, MediaType.parse(httpRequest.getContentType().getType()));
            } else {
                return RequestBody.create(httpRequest.getBody(), MediaType.parse(httpRequest.getContentType().getType()));
            }
        }
    }

    private HttpResponse executeRequest(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            final int responseCode = response.code();
            final String responseBody = Objects.requireNonNull(response.body()).string();
            if (response.isSuccessful()){
                return new HttpResponse(responseCode, responseBody);
            } else if (responseCode >= 400 && responseCode < 500) {
                ErrResponse errResponse;
                try {
                    errResponse = convertResponseBodyToErrResponse(responseBody);
                } catch (JsonSyntaxException e){
                    throw new ClientException(String.valueOf(responseCode), responseBody);
                }
                LOGGER.error("Client Error: {}", errResponse.getError());
                LOGGER.error("Client Error Detail: {}", errResponse.getErrorDescription());
                LOGGER.error("Client Error RequestId: {}", errResponse.getRequestId());
                throw new ClientException(errResponse.getError(), errResponse.getErrorDescription(), errResponse.getRequestId());
            } else {
                ErrResponse errResponse;
                try {
                    errResponse = convertResponseBodyToErrResponse(responseBody);
                } catch (JsonSyntaxException e){
                    throw new ServerException(String.valueOf(responseCode), responseBody);
                }
                LOGGER.error("Server Error: {}", errResponse.getError());
                LOGGER.error("Server Error Detail: {}", errResponse.getErrorDescription());
                LOGGER.error("Server Error RequestId: {}", errResponse.getRequestId());
                throw new ServerException(errResponse.getError(), errResponse.getErrorDescription(), errResponse.getRequestId());
            }
        } catch (IOException e) {
            if (ExceptionAnalyzer.isTargetCauseExist(e, ConnectException.class, ErrorCode.CONNECT_TIME_OUT.getCode())){
                throw new ClientException("Connect Timeout", e.getMessage());
            }
            if (ExceptionAnalyzer.isTargetCauseExist(e, SocketTimeoutException.class, ErrorCode.READ_TIME_OUT.getCode())){
                throw new ServerException("Read Timeout", e.getMessage());
            }
            throw new HttpException(String.format("Connect Failed: %s.", e.getMessage()));
        }
    }

    public static final class Builder {
        private Integer connectTimeout;
        private Integer readTimeout;

        public Builder connectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public DefaultOKHttpClientImp build(){
            return new DefaultOKHttpClientImp(this);
        }
    }

    private ErrResponse convertResponseBodyToErrResponse(String responseBody){
        Map<String, String> map = JSONUtil.parseObject(responseBody, Map.class);
        String error = map.containsKey("error") ? map.get("error") : map.get("Code");
        String errorDescription = map.containsKey("error_description") ? map.get("error_description") : map.get("Message");
        String requestId = map.containsKey("request_id") ? map.get("request_id") : map.get("RequestId");
        return new ErrResponse(error, errorDescription, requestId);
    }
}
