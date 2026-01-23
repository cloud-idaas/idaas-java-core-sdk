package com.cloud_idaas.core.domain.constants;

public interface OAuth2Constants {

    String CLIENT_ID = "client_id";

    String CLIENT_SECRET = "client_secret";

    String SCOPE = "scope";

    String DEVICE_CODE = "device_code";

    String GRANT_TYPE = "grant_type";

    String CLIENT_CREDENTIALS_GRANT_TYPE_VALUE = "client_credentials";

    String TOKEN_EXCHANGE_GRANT_TYPE_VALUE = "urn:ietf:params:oauth:grant-type:token-exchange";

    String DEVICE_CODE_GRANT_TYPE_VALUE = "urn:ietf:params:oauth:grant-type:device_code";

    String REFRESH_TOKEN_GRANT_TYPE_VALUE = "refresh_token";

    String CLIENT_ASSERTION_TYPE = "client_assertion_type";

    String CLIENT_ASSERTION = "client_assertion";

    String APPLICATION_FEDERATED_CREDENTIAL_NAME = "application_federated_credential_name";

    String REFRESH_TOKEN_PARAMETER = "refresh_token";

    String CLIENT_X509_CERTIFICATE = "client_x509";

    String X509_CERT_CHAINS = "client_x509_chain";

    String SUBJECT_TOKEN = "subject_token";

    String SUBJECT_TOKEN_TYPE = "subject_token_type";

    String SUBJECT_TOKEN_TYPE_VALUE = "urn:ietf:params:oauth:token-type:jwt";

    String REQUESTED_TOKEN_TYPE = "requested_token_type";

    String REQUESTED_TOKEN_TYPE_VALUE = "urn:ietf:params:oauth:token-type:access_token";

    String AUDIENCE = "audience";
}
