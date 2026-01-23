package com.cloud_idaas.core.http;


public enum ContentType {

    XML("application/xml"),

    JSON("application/json"),

    RAW("application/octet-stream"),

    FORM("application/x-www-form-urlencoded");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}