package com.cloud_idaas.core.exception;

public class ServerException extends RuntimeException {
    private static final long serialVersionUID = 2501055020735740747L;

    private String errorCode;
    private String errorMessage;
    private String requestId;

    public ServerException() {
    }

    public ServerException(String errorCode, String message){
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public ServerException(String errorCode, String message, String requestId) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.requestId = requestId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
