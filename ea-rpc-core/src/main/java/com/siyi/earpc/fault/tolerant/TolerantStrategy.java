package com.siyi.earpc.fault.tolerant;

import com.siyi.earpc.model.RpcResponse;

import java.util.Map;

/**
 * @author Eric
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return RpcResponse
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}