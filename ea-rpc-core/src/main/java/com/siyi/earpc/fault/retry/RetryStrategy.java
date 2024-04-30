package com.siyi.earpc.fault.retry;

import com.siyi.earpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author Eric
 */
public interface RetryStrategy {
    /**
     * 重试
     *
     * @param callable 重试的方法
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
