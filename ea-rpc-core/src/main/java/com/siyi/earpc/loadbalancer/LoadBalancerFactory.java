package com.siyi.earpc.loadbalancer;

import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.spi.SpiLoader;

public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }
    /**
     * 默认负载均衡器(轮询)
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();
    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}