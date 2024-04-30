package com.siyi.earpc.fault.retry;

import com.github.rholder.retry.*;
import com.siyi.earpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric
 * 固定间隔重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    /**
     * 重试
     *
     * @param callable 重试的方法
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 定义重试器
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 发生异常时重试
                .retryIfExceptionOfType(Exception.class)
                // 每次重试等待3秒
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 允许执行4次（首次执行 + 最多重试3次）
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试第{}次", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
