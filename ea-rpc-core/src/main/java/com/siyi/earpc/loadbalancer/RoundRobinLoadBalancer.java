package com.siyi.earpc.loadbalancer;

import com.siyi.earpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eric
 *  轮询负载均衡器
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * 当前轮询的位置
     */
    private AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 选择一个服务提供者
     *
     * @param requestParams        请求参数
     * @param serviceMetaInfosList 服务提供者列表
     * @return 服务提供者
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfosList) {
        if (serviceMetaInfosList.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfosList.size();
        if (size == 1) {
            return serviceMetaInfosList.get(0);
        }
        // 轮询
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfosList.get(index);
    }
}
