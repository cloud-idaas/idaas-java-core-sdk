package com.cloud_idaas.core.cache;

/**
 * Pre-fetch strategy interface, defining the behavior when the pre-fetch time is reached
 */
@FunctionalInterface
public interface PrefetchStrategy extends AutoCloseable {

    /**
     * Pre-fetch operation executed when the pre-fetch time is reached
     *
     * @param valueUpdater Value updater, responsible for executing the specific value refresh operation
     */
    void prefetch(Runnable valueUpdater);

    default void close() {
    }

}