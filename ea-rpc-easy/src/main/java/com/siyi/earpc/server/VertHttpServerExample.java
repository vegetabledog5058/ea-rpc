package com.siyi.earpc.server;

import io.vertx.core.Vertx;

public class VertHttpServerExample implements HttpServer {
    @Override
    public void start(int port) {
        // 1. 创建实例:
        Vertx vertx = Vertx.vertx();
        // 2. 创建http请求
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // 3. 监听端口的通知执行任务
        server.requestHandler(request -> {
            System.out.println("receive request:"+ request.method()+"request url:"+request.uri());
        // 4. 发送响应
            request.response().putHeader("content-type","text/plain").end("hello from vert.x HTTP Server");
        });
        // 5. 启动http服务器监听端口
        server
                .listen(8080,res->
                 {
                    if (res.succeeded()) {
                        System.out.println("Server is now listening!");
                    } else {
                        System.out.println("Failed to bind!");
                    }
                });
    }
}
