package com.siyi.earpc.bootstrap;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import com.siyi.earpc.model.ServiceRegisterInfo;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.register.Registry;
import com.siyi.earpc.register.RegistryFactory;
import com.siyi.earpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author Eric
 * 服务提供者启动类
 */
public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        RpcApplication.init();
        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //注册服务到本地
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
            // 将服务注册到注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //获取注册中心的实例
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            System.out.println(serviceMetaInfo.getServiceAddress());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //启动TCP服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }

}
