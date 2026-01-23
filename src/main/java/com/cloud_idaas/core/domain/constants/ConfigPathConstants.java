package com.cloud_idaas.core.domain.constants;

public interface ConfigPathConstants {

    /*
     * JVM parameter name for config file path
     */
    String JVM_CONFIG_PATH_KEY = "cloud_idaas_config_path";

    /*
     * Environment variable name for config file path
     */
    String ENV_CONFIG_PATH_KEY = "CLOUD_IDAAS_CONFIG_PATH";

    /*
     * Default config file path
     */
    String DEFAULT_CONFIG_PATH = System.getProperty("user.home") + "/.cloud_idaas/client-config.json";

    /**
     * Environment variable name for human credential cache file path
     */
    String ENV_HUMAN_CREDENTIAL_CACHE_PATH_KEY = "CLOUD_IDAAS_HUMAN_CREDENTIAL_CACHE_PATH";

    /**
     * Default human credential cache file path
     */
    String DEFAULT_HUMAN_CREDENTIAL_CACHE_PATH_TEMPLATE = System.getProperty("user.home") + "/.cloud_idaas/human_credential_%s_%s.json";

}
