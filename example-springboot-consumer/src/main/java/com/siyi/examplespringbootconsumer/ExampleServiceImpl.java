package com.siyi.examplespringbootconsumer;

import com.siyi.earpcspringstarter.annotation.RpcReference;
import com.siyi.example.common.model.User;
import com.siyi.example.common.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Eric
 */
@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("siyi");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}