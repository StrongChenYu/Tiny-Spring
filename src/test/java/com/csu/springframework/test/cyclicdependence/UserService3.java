package com.csu.springframework.test.cyclicdependence;

import com.csu.springframework.beans.factory.annotation.Autowired;
import com.csu.springframework.context.annotation.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService3 {

    @Autowired
    UserService1 userService1;

    public UserService1 getUserService1() {
        return userService1;
    }
}
