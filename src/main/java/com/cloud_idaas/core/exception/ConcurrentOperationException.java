package com.cloud_idaas.core.exception;

public class ConcurrentOperationException extends ClientException {
    private static final long serialVersionUID = -8555347444364435452L;

    public ConcurrentOperationException() {
        this("ConcurrentOperationFailed", "A concurrent operation is in progress, causing the current operation to fail.");
    }

    public ConcurrentOperationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
