package com.cloud_idaas.core.exception;

public class ConfigException extends ClientException {

    private static final long serialVersionUID = 8235407900491616001L;

    public ConfigException(String errorMessage) {
        super(errorMessage);
    }

    public ConfigException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
