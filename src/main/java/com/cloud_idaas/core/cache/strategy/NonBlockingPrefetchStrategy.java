package com.cloud_idaas.core.cache.strategy;

import com.cloud_idaas.core.cache.PrefetchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * NonBlocking strategy implementation
 * Non-blocking strategy: Uses a background thread pool to asynchronously update the cache
 */
public class NonBlockingPrefetchStrategy implements PrefetchStrategy {

    private static final int MAX_CONCURRENT_REFRESHES = 100;

    private static final Semaphore CONCURRENT_REFRESH_LEASES = new Semaphore(MAX_CONCURRENT_REFRESHES);

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingPrefetchStrategy.class);

    /**
     * Global executor service for all NonBlocking instances
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, Integer.MAX_VALUE,
            60, SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setName("idaas-java-core-sdk-non-blocking-refresh");
                    return t;
                }
            });

    private final AtomicBoolean currentlyPrefetching = new AtomicBoolean(false);

    public NonBlockingPrefetchStrategy() {
    }

    @Override
    public void prefetch(Runnable valueUpdater) {
        if (!CONCURRENT_REFRESH_LEASES.tryAcquire()) {
            LOGGER.warn("Only 100 concurrent refreshes are allowed");
            return;
        }

        if (currentlyPrefetching.compareAndSet(false, true)) {
            try {
                EXECUTOR.submit(() -> {
                    try {
                        valueUpdater.run();
                    } finally {
                        currentlyPrefetching.set(false);
                        CONCURRENT_REFRESH_LEASES.release();
                    }
                });
            } catch (Throwable t) {
                currentlyPrefetching.set(false);
                CONCURRENT_REFRESH_LEASES.release();
            }
        } else{
            CONCURRENT_REFRESH_LEASES.release();
        }
    }
}