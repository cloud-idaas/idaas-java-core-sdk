package com.cloud_idaas.core.exception;

public class EncodingException extends RuntimeException {

    private static final long serialVersionUID = 8235407900491616001L ;

    private String errorCode;

    private String errorMessage;

    public EncodingException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public EncodingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public EncodingException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
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
}
