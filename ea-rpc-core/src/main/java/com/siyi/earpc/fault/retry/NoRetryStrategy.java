package com.siyi.earpc.fault.retry;

import com.siyi.earpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author Eric
 * 无重试策略
 */
public class NoRetryStrategy implements RetryStrategy{
    /**
     * 重试
     *
     * @param callable 重试的方法
     * @return
     * @throws Exception 重试的方法
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
