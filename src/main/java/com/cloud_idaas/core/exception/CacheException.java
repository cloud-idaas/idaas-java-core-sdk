package com.cloud_idaas.core.exception;


public class CacheException extends RuntimeException {

    private static final long serialVersionUID = 4994943730705109000L;

    private String errorCode;

    private String errorMessage;

    public CacheException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public CacheException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public CacheException(Throwable  cause){
        super(cause);
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
