package com.cloud_idaas.core.cache.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OneCallerBlocksPrefetchStrategy 单元测试
 */
class OneCallerBlocksPrefetchStrategyTest {

    private OneCallerBlocksPrefetchStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new OneCallerBlocksPrefetchStrategy();
    }

    // ==================== 基本功能测试 ====================

    @Test
    @DisplayName("prefetch: 应执行传入的 Runnable")
    void prefetch_ShouldExecuteRunnable() {
        AtomicInteger counter = new AtomicInteger(0);

        strategy.prefetch(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("prefetch: 多次调用应每次都执行")
    void prefetch_MultipleCalls_ShouldExecuteEachTime() {
        AtomicInteger counter = new AtomicInteger(0);

        strategy.prefetch(counter::incrementAndGet);
        strategy.prefetch(counter::incrementAndGet);
        strategy.prefetch(counter::incrementAndGet);

        assertEquals(3, counter.get());
    }

    @Test
    @DisplayName("prefetch: Runnable 抛出异常后应能继续执行后续调用")
    void prefetch_AfterException_ShouldContinueToWork() {
        AtomicInteger counter = new AtomicInteger(0);

        // 第一次调用：捕获异常，确保不中断测试
        try {
            strategy.prefetch(() -> {
                throw new RuntimeException("Test exception");
            });
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }

        // 后续调用应正常执行（因为 currentlyRefreshing 已重置）
        strategy.prefetch(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    // ==================== 并发测试 ====================

    @Test
    @DisplayName("并发: 多个线程同时调用时只有一个应执行 Runnable")
    void concurrency_MultipleThreadsOnlyOneShouldExecute() throws InterruptedException {
        AtomicInteger executionCount = new AtomicInteger(0);
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    strategy.prefetch(() -> {
                        executionCount.incrementAndGet();
                        try {
                            // 模拟耗时操作，让其他线程有机会进入
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 同时启动所有线程
        startLatch.countDown();
        endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 只有一个线程应该执行了 Runnable
        assertEquals(1, executionCount.get());
    }

    @Test
    @DisplayName("并发: 第一个调用完成后，后续调用应能执行")
    void concurrency_AfterFirstCompletes_SubsequentCallsShouldExecute() throws InterruptedException {
        AtomicInteger executionCount = new AtomicInteger(0);

        // 第一次调用
        strategy.prefetch(executionCount::incrementAndGet);
        assertEquals(1, executionCount.get());

        // 等待确保第一次调用完成
        Thread.sleep(50);

        // 第二次调用应该能执行
        strategy.prefetch(executionCount::incrementAndGet);
        assertEquals(2, executionCount.get());
    }

    // ==================== close 方法测试 ====================

    @Test
    @DisplayName("close: 调用 close 不应抛出异常")
    void close_ShouldNotThrowException() {
        assertDoesNotThrow(() -> strategy.close());
    }

    @Test
    @DisplayName("close: 多次调用 close 不应抛出异常")
    void close_MultipleCalls_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            strategy.close();
            strategy.close();
            strategy.close();
        });
    }

    // ==================== 接口实现测试 ====================

    @Test
    @DisplayName("接口: 应实现 PrefetchStrategy 接口")
    void interface_ShouldImplementPrefetchStrategy() {
        assertTrue(strategy instanceof com.cloud_idaas.core.cache.PrefetchStrategy);
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("边界: Runnable 执行时间很短应正常工作")
    void boundary_QuickRunnable_ShouldWork() {
        AtomicInteger counter = new AtomicInteger(0);

        strategy.prefetch(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("边界: Runnable 为空操作应正常工作")
    void boundary_NoOpRunnable_ShouldWork() {
        assertDoesNotThrow(() -> strategy.prefetch(() -> {}));
    }

    @Test
    @DisplayName("边界: 新实例应能正常工作")
    void boundary_NewInstance_ShouldWork() {
        OneCallerBlocksPrefetchStrategy newStrategy = new OneCallerBlocksPrefetchStrategy();
        AtomicInteger counter = new AtomicInteger(0);

        newStrategy.prefetch(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    // ==================== 状态重置测试 ====================

    @Test
    @DisplayName("状态: 异常后状态应正确重置")
    void state_AfterException_ShouldResetCorrectly() {
        AtomicInteger counter = new AtomicInteger(0);

        // 第一次调用抛出异常
        try {
            strategy.prefetch(() -> {
                throw new RuntimeException("Test exception");
            });
        } catch (RuntimeException e) {
            // 预期的异常
        }

        // 状态应该已重置，第二次调用应能执行
        strategy.prefetch(counter::incrementAndGet);
        assertEquals(1, counter.get());
    }
}
