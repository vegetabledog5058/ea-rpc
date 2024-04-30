package com.siyi.earpc.loadbalancer;

import com.siyi.earpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Eric
 *  负载均衡器
 */
public interface LoadBalancer  {
    /**
     * 选择一个服务提供者
     * @param requestParams 请求参数
     * @param serviceMetaInfosList 服务提供者列表
     * @return 服务提供者
     */
    ServiceMetaInfo select(Map<String,Object>requestParams, List<ServiceMetaInfo> serviceMetaInfosList );
}
