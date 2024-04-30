package com.siyi.earpc.fault.tolerant;

import com.siyi.earpc.model.RpcResponse;

import java.util.Map;

/**
 * @author Eric
 * 快速失败容错策略
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    /**
     * 容错
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return RpcResponse
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
