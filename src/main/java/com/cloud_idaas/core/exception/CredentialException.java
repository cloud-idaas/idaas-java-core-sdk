package com.cloud_idaas.core.exception;

public class CredentialException extends RuntimeException {

    private static final long serialVersionUID = 9650629581514976L;

    private String errorCode;

    private String errorMessage;

    public CredentialException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public CredentialException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public CredentialException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
