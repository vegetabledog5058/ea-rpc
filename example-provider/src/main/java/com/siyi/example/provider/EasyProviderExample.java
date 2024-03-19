package com.siyi.example.provider;

import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.server.HttpServer;
import com.siyi.earpc.server.VertHttpServerExample;
import com.siyi.earpc.server.VertxHttpServer;
import com.siyi.example.common.service.UserService;

/**
 * @author Eric
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(8080);
    }
}
