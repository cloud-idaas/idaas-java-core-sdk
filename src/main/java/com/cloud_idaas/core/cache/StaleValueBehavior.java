package com.cloud_idaas.core.cache;

/**
 * Enum for stale value behavior strategy
 * Defines how to handle stale values when cache refresh fails
 */
public enum StaleValueBehavior {
    /**
     * Strict mode: throws exception if refresh fails
     */
    STRICT,

    /**
     * Lenient mode: allows returning stale value and retries within a certain time
     */
    ALLOW
}