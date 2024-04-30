package com.siyi.earpc.loadbalancer;

import com.siyi.earpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Eric
 * 随机负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer{
    // 随机负载均衡器索引
    private Random random = new Random();

    /**
     * 选择一个服务提供者
     *
     * @param requestParams        请求参数
     * @param serviceMetaInfosList 服务提供者列表
     * @return 服务提供者
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfosList) {
        int size = serviceMetaInfosList.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return serviceMetaInfosList.get(0);
        }
        // 随机
        return serviceMetaInfosList.get(random.nextInt(size));
    }
}
