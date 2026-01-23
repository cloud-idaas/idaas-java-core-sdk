package com.cloud_idaas.core.domain.constants;

public enum ClientDeployEnvironmentEnum {

    /**
     * When deploying to a common environment, only supported environment variables client assertion provider.
     */
    COMMON,

    COMPUTER,

    KUBERNETES,

    ALIBABA_CLOUD_ECS,

    ALIBABA_CLOUD_ECI,

    ALIBABA_CLOUD_ACK,

    AWS_EC2,

    AWS_ESK,

    GOOGLE_VM,

    HUAWEI_CLOUD_ECS,

    /**
     * When deploying to a custom environment, customer need manual specified the authentication provider.
     */
    CUSTOM,

    ;
}
