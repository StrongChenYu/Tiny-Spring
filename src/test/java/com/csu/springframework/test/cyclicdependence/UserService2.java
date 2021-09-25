package com.csu.springframework.test.cyclicdependence;

import com.csu.springframework.beans.factory.annotation.Autowired;
import com.csu.springframework.beans.factory.annotation.Qualifier;
import com.csu.springframework.context.annotation.Component;

@Component("userService2")
public class UserService2 implements IUserService{

    @Autowired
    @Qualifier("userService1")
    private IUserService userService1;

    @Override
    public IUserService getIUserService() {
        return userService1;
    }

    @Override
    public void outPutOwnCode() {
        System.out.println("userService2");
    }
}
