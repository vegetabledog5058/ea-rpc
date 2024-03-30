package com.siyi.example.provider;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.server.HttpServer;
import com.siyi.earpc.server.VertxHttpServer;
import com.siyi.example.common.service.UserService;

public class RpcProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
