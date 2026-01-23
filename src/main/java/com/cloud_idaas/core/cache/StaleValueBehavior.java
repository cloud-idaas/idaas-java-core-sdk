package com.cloud_idaas.core.cache;

/**
 * 陈旧值行为策略枚举
 * 定义当缓存刷新失败时如何处理陈旧值
 */
public enum StaleValueBehavior {
    /**
     * 严格模式：如果刷新失败则抛出异常
     */
    STRICT,

    /**
     * 宽松模式：允许返回陈旧值并在一定时间内重试
     */
    ALLOW
}