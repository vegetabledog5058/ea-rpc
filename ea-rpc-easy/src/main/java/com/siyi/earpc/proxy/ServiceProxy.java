package com.siyi.earpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.serializer.JdkSerializer;
import com.siyi.earpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //发送请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .name(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            // TODO 这里硬编码可以依靠注册中心硬编码解决
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }

            RpcResponse response = serializer.deserialize(result, RpcResponse.class);
            return response.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
