package com.cloud_idaas.core.exception;

public class IDaaSUnexpectedException extends RuntimeException {

    private static final long serialVersionUID = 5580054229455385096L;

    private String errorCode;

    private String errorMessage;

    public IDaaSUnexpectedException(){
    }

    public IDaaSUnexpectedException(String message){
        super(message);
        this.errorMessage = message;
    }

    public IDaaSUnexpectedException(String errorCode, String message){
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public IDaaSUnexpectedException(Throwable cause){
        super(cause);
    }

    public IDaaSUnexpectedException(String message, Throwable cause){
        super(message, cause);
        this.errorMessage = message;
    }

    public IDaaSUnexpectedException(String errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
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
