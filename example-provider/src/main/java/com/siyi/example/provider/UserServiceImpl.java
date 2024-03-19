package com.siyi.example.provider;

import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;

/**
 * @author Eric
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
