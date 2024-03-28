package com.siyi.example.consumer;

import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.constant.RpcConstant;
import com.siyi.earpc.utils.ConfigUtils;

/**
 * @author Eric
 */
public class RpcConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        System.out.println(rpc);

    }
}
