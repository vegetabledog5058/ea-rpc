package com.siyi.earpc.fault.retry;

/**
 * @author Eric
 * 重试策略的key
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";

}