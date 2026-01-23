package com.cloud_idaas.core.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrResponse implements Serializable {

    private static final long serialVersionUID = -3188017459371773632L;

    private String error;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("request_id")
    private String requestId;

    public ErrResponse() {
    }

    public ErrResponse(String error, String errorDescription, String requestId) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
