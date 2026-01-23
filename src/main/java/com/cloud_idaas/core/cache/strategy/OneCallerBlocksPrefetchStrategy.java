package com.cloud_idaas.core.cache.strategy;

import com.cloud_idaas.core.cache.PrefetchStrategy;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * OneCallerBlocks strategy implementation
 * The simplest strategy: only one caller will be blocked and execute the update operation, other callers return immediately
 */
public class OneCallerBlocksPrefetchStrategy implements PrefetchStrategy {

    private final AtomicBoolean currentlyRefreshing = new AtomicBoolean(false);

    @Override
    public void prefetch(Runnable valueUpdater) {
        if (currentlyRefreshing.compareAndSet(false, true)) {
            try {
                valueUpdater.run();
            } finally {
                currentlyRefreshing.set(false);
            }
        }
    }
}