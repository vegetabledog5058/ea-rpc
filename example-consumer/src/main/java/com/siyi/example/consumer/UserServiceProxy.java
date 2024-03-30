package com.siyi.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.serializer.JdkSerializer;
import com.siyi.earpc.serializer.Serializer;
import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;

import java.io.IOException;

/**
 * @author Eric
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //发起请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .name(UserService.class.getName())
                .methodName("getUser")
                .args(new Object[]{user})
                .parameterTypes(new Class[]{User.class})
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }

            RpcResponse response = serializer.deserialize(result, RpcResponse.class);
            return (User) response.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
