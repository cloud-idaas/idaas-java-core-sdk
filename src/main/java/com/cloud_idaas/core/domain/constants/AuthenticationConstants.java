package com.cloud_idaas.core.domain.constants;

public interface AuthenticationConstants {

    /**
     * @see "https://kubernetes.io/docs/concepts/security/service-accounts/#get-a-token"
     */
    String KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/token";

    String ALIBABA_CLOUD_ECS_METADATA_SERVICE_URL = "http://100.100.100.200/latest/meta-data/";

    /**
     * @see "https://help.aliyun.com/zh/ack/ack-managed-and-ack-dedicated/user-guide/use-rrsa-to-authorize-pods-to-access-different-cloud-services"
     */
    String ALIBABA_CLOUD_ACK_OIDC_TOKEN_PATH_ENV = "ALIBABA_CLOUD_OIDC_TOKEN_FILE";

    /**
     * default client id environment variable name
     */
    String DEFAULT_CLIENT_ID_ENVIRONMENT_VARIABLE_NAME = "CLOUD_IDAAS_CLIENT_ID";

    /**
     * default client secret environment variable name
     */
    String DEFAULT_CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME = "CLOUD_IDAAS_CLIENT_SECRET";
}
