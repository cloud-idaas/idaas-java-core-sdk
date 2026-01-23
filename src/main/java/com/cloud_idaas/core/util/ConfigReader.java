package com.cloud_idaas.core.util;

import com.cloud_idaas.core.domain.constants.ConfigPathConstants;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration file reading utility class
 * Supports obtaining the configuration file path from JVM parameters or environment variables, compatible with specific paths and classpath-prefixed paths
 */
public class ConfigReader {

    /**
     * Get the configuration file path (priority: JVM parameter > environment variable)
     *
     * @return Configuration file path
     */
    private static String getConfigPath() {

        // First check JVM parameters
        String configPath = System.getProperty(ConfigPathConstants.JVM_CONFIG_PATH_KEY);
        if (configPath != null && !configPath.trim().isEmpty()) {
            return configPath;
        }


        // Then check environment variables
        configPath = System.getenv(ConfigPathConstants.ENV_CONFIG_PATH_KEY);
        if (configPath != null && !configPath.trim().isEmpty()) {
            return configPath;
        }

        // Default value
        return ConfigPathConstants.DEFAULT_CONFIG_PATH;
    }

    /**
     * Load specified configuration file content as string based on JVM parameters and environment variables
     *
     * @return Configuration file content as string
     * @throws ConfigException Thrown when configuration file does not exist or fails to read
     */
    public static String getConfigAsString() {

        // Get the configuration file path
        String configPath = getConfigPath();
        if (configPath.trim().isEmpty()) {
            throw new ConfigException(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), "IDaaS config not specified. Please using jvm -D" +
                    ConfigPathConstants.JVM_CONFIG_PATH_KEY + " or environment variables " + ConfigPathConstants.ENV_CONFIG_PATH_KEY + " specified.");
        }

        // Read the configuration file content as a string
        return loadFileAsString(configPath);
    }

    /**
     * Load file content as string
     *
     * @param configPath Configuration file path
     * @return File content as string
     * @throws ConfigException Thrown when file does not exist or fails to read
     */
    public static String loadFileAsString(String configPath) {
        try (InputStream inputStream = getFileInputStream(configPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();

        } catch (IOException e) {
            throw new ConfigException(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), "load config file failed: " + configPath);
        }
    }

    /**
     * Load JSON configuration file (using Gson)
     *
     * @param configPath Configuration file path
     * @return Properties Configuration object
     * @throws ConfigException Thrown when the configuration file does not exist or fails to read
     */
    private static Properties loadJsonProperties(String configPath) {
        Properties properties = new Properties();

        try (InputStream inputStream = getFileInputStream(configPath)) {

            // Parse JSON using Gson
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));


            // Convert JSON element to Properties
            if (jsonElement.isJsonObject()) {
                flattenJsonElement("", jsonElement.getAsJsonObject(), properties);
            }

        } catch (Exception e) {
            throw new ConfigException(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), "load JSON config file failed: " + configPath);
        }

        return properties;
    }

    /**
     * Recursively flatten JSON object to Properties (using Gson)
     *
     * @param prefix     Key prefix
     * @param element    JSON element
     * @param properties Properties object
     */
    private static void flattenJsonElement(String prefix, JsonElement element, Properties properties) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenJsonElement(key, entry.getValue(), properties);
            }
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                String key = prefix + "[" + i + "]";
                flattenJsonElement(key, jsonArray.get(i), properties);
            }
        } else {
            // Handle basic types (strings, numbers, booleans, etc.)
            properties.setProperty(prefix, element.getAsString());
        }
    }

    /**
     * Load configuration file
     *
     * @param configPath Configuration file path (supports specific paths and classpath-prefixed paths)
     * @return Properties Configuration object
     * @throws ConfigException Thrown when the configuration file does not exist or fails to read
     */
    private static Properties loadProperties(String configPath) {
        Properties properties = new Properties();

        try (InputStream inputStream = getFileInputStream(configPath)) {
            if (configPath.toLowerCase().endsWith(".xml")) {
                properties.loadFromXML(inputStream);
            } else if (configPath.toLowerCase().endsWith(".json")) {

                // Process JSON file
                return loadJsonProperties(configPath);
            } else {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new ConfigException(ErrorCode.LOAD_CONFIG_FILE_FAILED.getCode(), "load JSON config file failed: " + configPath);
        }

        return properties;
    }

    /**
     * Get file input stream (supports specific paths and classpath prefixed paths)
     *
     * @param configPath Configuration file path
     * @return InputStream File input stream
     * @throws IOException Thrown when the file does not exist or cannot be accessed
     */
    private static InputStream getFileInputStream(String configPath) throws IOException {

        // Handle classpath prefixed path
        if (configPath.startsWith("classpath:")) {
            String resourcePath = configPath.substring(10); // 去掉"classpath:"前缀
            InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new FileNotFoundException("Cannot find config by Classpath resource : " + resourcePath);
            }
            return inputStream;
        }


        // Handle file system path
        File file = new File(configPath);
        if (file.exists()) {
            return Files.newInputStream(file.toPath());
        }


        // Try to load from classpath (as fallback)
        InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configPath);
        if (inputStream != null) {
            return inputStream;
        }

        throw new FileNotFoundException("IDaaS config file not found, config path: " + configPath);
    }
}
