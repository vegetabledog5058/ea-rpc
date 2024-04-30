package com.siyi.earpc.loadbalancer;

import com.siyi.earpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * @author siyi
 * 一致性hash负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    private static final int VIRTUAL_NODE_NUM = 100;
    private final TreeMap<Integer, ServiceMetaInfo> virtualInvokers = new TreeMap<>();

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
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfosList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "$" + i);
                virtualInvokers.put(hash, serviceMetaInfo);
            }
        }
        //获取请求参数的hash值
        int hash = getHash(requestParams);
        //获取大于该hash值的第一个节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualInvokers.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualInvokers.firstEntry();
        }
        return entry.getValue();
    }

/**
 * hash算法
*/
    private int getHash(Object key) {
        return key.hashCode();
    }
}
