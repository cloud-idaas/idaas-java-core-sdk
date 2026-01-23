package com.cloud_idaas.core.exception;

public class SSLException extends ClientException {

    private static final long serialVersionUID = 3601672453770355700L;

    public SSLException() {
    }

    public SSLException(String errorCode, String message) {
        super(errorCode, message);
    }
}
