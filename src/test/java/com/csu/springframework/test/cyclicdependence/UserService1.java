package com.csu.springframework.test.cyclicdependence;

import com.csu.springframework.beans.factory.annotation.Autowired;
import com.csu.springframework.beans.factory.annotation.Qualifier;
import com.csu.springframework.context.annotation.Component;

import java.util.Objects;

@Component("userService1")
public class UserService1 implements IUserService {

    @Autowired
    @Qualifier("userService2")
    IUserService userService2;

    @Override
    public IUserService getIUserService() {
        return userService2;
    }

    @Override
    public void outPutOwnCode() {
        System.out.println("userService1");
    }

}
