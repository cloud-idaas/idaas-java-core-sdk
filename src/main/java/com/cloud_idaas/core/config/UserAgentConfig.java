package com.cloud_idaas.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

public class UserAgentConfig implements Serializable {

    private static final long serialVersionUID = -4938854887261940034L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAgentConfig.class);

    static {
        Properties sysProps = System.getProperties();
        String coreVersion = "";
        Properties props = new Properties();
        try {
            props.load(UserAgentConfig.class.getClassLoader().getResourceAsStream("project.properties"));
            coreVersion = props.getProperty("core.version");
        } catch (IOException e) {
            LOGGER.error("Load project.properties failed.", e);
        }

        USER_AGENT_MESSAGE = String.format("%s/%s Java/%s OS(%s; %s)", "IDaaS core", coreVersion, sysProps.getProperty("java.version"), sysProps.getProperty("os.name"), sysProps.getProperty("os.arch"));
    }

    private static final String USER_AGENT_MESSAGE;

    public static String getUserAgentMessage() {
        return USER_AGENT_MESSAGE;
    }
}

