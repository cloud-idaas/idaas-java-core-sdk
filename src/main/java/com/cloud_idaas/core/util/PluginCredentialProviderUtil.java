package com.cloud_idaas.core.util;

import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.provider.PluginCredentialProvider;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class PluginCredentialProviderUtil {

    private static final Map<String, PluginCredentialProvider> PLUGIN_CREDENTIAL_PROVIDER_MAP = new ConcurrentHashMap<>();

    static {
        ServiceLoader<PluginCredentialProvider> serviceLoader = ServiceLoader.load(PluginCredentialProvider.class);
        for (PluginCredentialProvider pluginCredentialProvider : serviceLoader){
            PLUGIN_CREDENTIAL_PROVIDER_MAP.put(pluginCredentialProvider.getName(), pluginCredentialProvider);
        }
    }

    public static PluginCredentialProvider getPluginCredentialProvider(String pluginName){
        if (pluginName == null){
            throw new ConfigException("PluginName can not be empty.");
        }
        PluginCredentialProvider pluginCredentialProvider = PLUGIN_CREDENTIAL_PROVIDER_MAP.get(pluginName);
        if (pluginCredentialProvider == null){
            throw new ConfigException(String.format("%s Plugin not found", pluginName));
        }
        return pluginCredentialProvider;
    }
}
