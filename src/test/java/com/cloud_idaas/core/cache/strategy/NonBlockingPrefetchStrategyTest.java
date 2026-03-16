package com.cloud_idaas.core.cache.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NonBlockingPrefetchStrategy 单元测试
 */
class NonBlockingPrefetchStrategyTest {

    private NonBlockingPrefetchStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new NonBlockingPrefetchStrategy();
    }

    @AfterEach
    void tearDown() throws Exception {
        strategy.close();
    }

    // ==================== 基本功能测试 ====================

    @Test
    @DisplayName("prefetch: 应异步执行传入的 Runnable")
    void prefetch_ShouldExecuteRunnableAsynchronously() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        strategy.prefetch(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        // 等待异步执行完成
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "异步任务应在超时前完成");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("prefetch: 多次顺序调用应每次都执行")
    void prefetch_MultipleSequentialCalls_ShouldExecuteEachTime() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            strategy.prefetch(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
            assertTrue(latch.await(5, TimeUnit.SECONDS), "任务 " + (i + 1) + " 应完成");
        }

        assertEquals(3, counter.get());
    }

    @Test
    @DisplayName("prefetch: Runnable 抛出异常后应能继续执行后续调用")
    void prefetch_AfterException_ShouldContinueToWork() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        // 第一次调用抛出异常
        CountDownLatch latch1 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            latch1.countDown();
            throw new RuntimeException("Test exception");
        });
        assertTrue(latch1.await(5, TimeUnit.SECONDS));

        // 等待确保异常处理完成
        Thread.sleep(100);

        // 后续调用应正常执行
        CountDownLatch latch2 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            counter.incrementAndGet();
            latch2.countDown();
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS));
        assertEquals(1, counter.get());
    }

    // ==================== 并发测试 ====================

    @Test
    @DisplayName("并发: 同时只有一个预取操作在执行")
    void concurrency_OnlyOnePrefetchAtATime() throws InterruptedException {
        AtomicInteger concurrentCount = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        CountDownLatch executionLatch = new CountDownLatch(1); // 只需一个任务执行

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    strategy.prefetch(() -> {
                        int current = concurrentCount.incrementAndGet();
                        maxConcurrent.updateAndGet(max -> Math.max(max, current));
                        try {
                            Thread.sleep(200);
                            executionLatch.countDown(); // 标记任务已执行
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        concurrentCount.decrementAndGet();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 启动所有线程
        startLatch.countDown();
        executionLatch.await(5, TimeUnit.SECONDS);
        endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 此时 maxConcurrent 应为 1
        assertEquals(1, maxConcurrent.get());
    }


    @Test
    @DisplayName("并发: 第一个调用完成后，后续调用应能执行")
    void concurrency_AfterFirstCompletes_SubsequentCallsShouldExecute() throws InterruptedException {
        AtomicInteger executionCount = new AtomicInteger(0);

        // 第一次调用
        CountDownLatch latch1 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            executionCount.incrementAndGet();
            latch1.countDown();
        });
        assertTrue(latch1.await(5, TimeUnit.SECONDS));

        // 等待确保第一次调用完全完成
        Thread.sleep(100);

        // 第二次调用应该能执行
        CountDownLatch latch2 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            executionCount.incrementAndGet();
            latch2.countDown();
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS));
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
    void boundary_QuickRunnable_ShouldWork() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        strategy.prefetch(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("边界: Runnable 为空操作应正常工作")
    void boundary_NoOpRunnable_ShouldWork() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        strategy.prefetch(latch::countDown);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("边界: 新实例应能正常工作")
    void boundary_NewInstance_ShouldWork() throws InterruptedException {
        NonBlockingPrefetchStrategy newStrategy = new NonBlockingPrefetchStrategy();
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        try {
            newStrategy.prefetch(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });

            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertEquals(1, counter.get());
        } finally {
            newStrategy.close();
        }
    }

    // ==================== 非阻塞特性测试 ====================

    @Test
    @DisplayName("非阻塞: prefetch 方法应立即返回")
    void nonBlocking_PrefetchShouldReturnImmediately() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(1);
        AtomicInteger executionCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        
        strategy.prefetch(() -> {
            executionCount.incrementAndGet();
            startLatch.countDown();
            try {
                // 模拟长时间运行的任务
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            endLatch.countDown();
        });

        long elapsed = System.currentTimeMillis() - startTime;

        // prefetch 应该立即返回，不应等待任务完成
        assertTrue(elapsed < 500, "prefetch 应立即返回，但耗时 " + elapsed + "ms");
        
        // 等待任务开始执行
        assertTrue(startLatch.await(5, TimeUnit.SECONDS));
        
        // 清理
        endLatch.await(10, TimeUnit.SECONDS);
    }

    // ==================== 状态重置测试 ====================

    @Test
    @DisplayName("状态: 异常后状态应正确重置")
    void state_AfterException_ShouldResetCorrectly() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        // 第一次调用抛出异常
        CountDownLatch latch1 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            latch1.countDown();
            throw new RuntimeException("Test exception");
        });
        assertTrue(latch1.await(5, TimeUnit.SECONDS));

        // 等待确保异常处理完成
        Thread.sleep(200);

        // 状态应该已重置，第二次调用应能执行
        CountDownLatch latch2 = new CountDownLatch(1);
        strategy.prefetch(() -> {
            counter.incrementAndGet();
            latch2.countDown();
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS));
        assertEquals(1, counter.get());
    }

    // ==================== 多实例测试 ====================

    @Test
    @DisplayName("多实例: 多个策略实例应独立工作")
    void multipleInstances_ShouldWorkIndependently() throws InterruptedException {
        NonBlockingPrefetchStrategy strategy1 = new NonBlockingPrefetchStrategy();
        NonBlockingPrefetchStrategy strategy2 = new NonBlockingPrefetchStrategy();

        try {
            AtomicInteger counter1 = new AtomicInteger(0);
            AtomicInteger counter2 = new AtomicInteger(0);
            CountDownLatch latch1 = new CountDownLatch(1);
            CountDownLatch latch2 = new CountDownLatch(1);

            strategy1.prefetch(() -> {
                counter1.incrementAndGet();
                latch1.countDown();
            });
            strategy2.prefetch(() -> {
                counter2.incrementAndGet();
                latch2.countDown();
            });

            assertTrue(latch1.await(5, TimeUnit.SECONDS));
            assertTrue(latch2.await(5, TimeUnit.SECONDS));
            assertEquals(1, counter1.get());
            assertEquals(1, counter2.get());
        } finally {
            strategy1.close();
            strategy2.close();
        }
    }
}
