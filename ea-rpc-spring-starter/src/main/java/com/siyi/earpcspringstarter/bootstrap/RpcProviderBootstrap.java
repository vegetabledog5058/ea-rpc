package com.siyi.earpcspringstarter.bootstrap;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.config.RegistryConfig;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.register.Registry;
import com.siyi.earpc.register.RegistryFactory;
import com.siyi.earpcspringstarter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Eric
 * RPC服务提供者
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获取bean的类
        Class<?> beanClass = bean.getClass();
        //获取bean的注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            //需要注册服务
            //1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            //默认值处理,如果是void.class,则获取接口
            if (interfaceClass == void.class) {
                //获取接口
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String version = rpcService.version();
            //2. 注册服务
            LocalRegistry.register(serviceName, beanClass);
            //全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //3. 服务注册
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(version);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
