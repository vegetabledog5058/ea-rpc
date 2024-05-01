package com.siyi.example.consumer;

import com.siyi.earpc.bootstrap.ConsumerBootstrap;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.constant.RpcConstant;
import com.siyi.earpc.proxy.ServiceProxyFactory;
import com.siyi.earpc.utils.ConfigUtils;
import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;

/**
 * @author Eric
 */
public class RpcConsumerExample {
    public static void main(String[] args) {
        // 服务消费者初始化
        ConsumerBootstrap.init();
        // 服务消费者调用
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("张三");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName()+"sada");
        } else {
            System.out.println("user == null");
        }
    }
}
