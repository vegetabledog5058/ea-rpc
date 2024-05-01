package com.siyi.example.provider;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.bootstrap.ProviderBootstrap;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import com.siyi.earpc.model.ServiceRegisterInfo;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.register.Registry;
import com.siyi.earpc.register.RegistryFactory;
import com.siyi.earpc.server.HttpServer;
import com.siyi.earpc.server.VertxHttpServer;
import com.siyi.earpc.server.tcp.VertxTcpClient;
import com.siyi.earpc.server.tcp.VertxTcpServer;
import com.siyi.example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eric
 */
public class RpcProviderExample {

    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> registerInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        registerInfoList.add(serviceRegisterInfo);
        // 服务提供者初始化
        ProviderBootstrap.init(registerInfoList);

    }

}
