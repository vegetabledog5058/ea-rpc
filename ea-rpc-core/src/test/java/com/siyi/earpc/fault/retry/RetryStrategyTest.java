package com.siyi.earpc.fault.retry;

import com.siyi.earpc.model.RpcResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class RetryStrategyTest {
    RetryStrategy retryStrategy = new NoRetryStrategy();
    @Test
    public void doRetry() throws Exception {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}