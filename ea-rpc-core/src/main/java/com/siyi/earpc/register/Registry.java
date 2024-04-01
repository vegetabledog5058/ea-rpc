package com.siyi.earpc.register;

import com.siyi.earpc.config.RegistryConfig;
import com.siyi.earpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author Eric
 */
public interface Registry {
    /**
     * 服务注册
     *
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务注册
     *
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务注销
     *
     * @param serviceMetaInfo 服务元信息
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    List<ServiceMetaInfo> discovery(String serviceName) throws Exception;

    /**
     * 服务销毁
     */
    void destroy();
}
