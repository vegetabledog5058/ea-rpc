package com.siyi.earpc.server;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 1. 转换为对象,从对象中获得参数
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //构建响应对象
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
            }

            //从注册器获取实现类,反射调用
            Class<?> implClass = LocalRegistry.get(rpcRequest.getName());
            try {
                Method methods = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = methods.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(methods.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());

            }
            doResponse(request, rpcResponse, serializer);

        });

    }

    void doResponse(HttpServerRequest httpServerRequest, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = httpServerRequest
                .response()
                .putHeader("content-type", "application/json");
        try {
            byte[] bytes = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
