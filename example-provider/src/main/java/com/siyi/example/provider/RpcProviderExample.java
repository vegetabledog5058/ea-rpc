package com.siyi.example.provider;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.register.Registry;
import com.siyi.earpc.register.RegistryFactory;
import com.siyi.earpc.server.HttpServer;
import com.siyi.earpc.server.VertxHttpServer;
import com.siyi.example.common.service.UserService;

/**
 * @author Eric
 */
public class RpcProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();
        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);
        // 将服务注册到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceAddress(rpcConfig.getServerHost()+":"+rpcConfig.getServerPort());
        String serverHost = rpcConfig.getServerHost();
        Integer serverPort = rpcConfig.getServerPort();
        System.out.println(serviceMetaInfo.getServiceAddress());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
