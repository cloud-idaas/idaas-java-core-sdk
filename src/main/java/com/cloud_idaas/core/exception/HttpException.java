package com.cloud_idaas.core.exception;

public class HttpException extends RuntimeException{
    private static final long serialVersionUID = 346435850125655034L;

    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
