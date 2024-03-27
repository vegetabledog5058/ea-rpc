package com.siyi.example.consumer;

import com.siyi.earpc.proxy.ServiceProxyFactory;
import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        //静态代理
//        UserService userService = new UserServiceProxy();
        // 获取USerService的实现类对象
            UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("张三");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }

}
