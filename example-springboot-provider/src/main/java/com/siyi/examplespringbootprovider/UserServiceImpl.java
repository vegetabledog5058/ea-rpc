package com.siyi.examplespringbootprovider;

import com.siyi.earpcspringstarter.annotation.RpcService;
import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}