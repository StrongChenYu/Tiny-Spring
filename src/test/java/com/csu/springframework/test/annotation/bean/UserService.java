package com.csu.springframework.test.annotation.bean;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.*;
import com.csu.springframework.beans.factory.annotation.Autowired;
import com.csu.springframework.beans.factory.annotation.Value;
import com.csu.springframework.context.ApplicationContext;
import com.csu.springframework.context.annotation.Component;

import java.lang.reflect.Method;

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    @Value("${token}")
    private String token;

    public void queryInfo() {
        System.out.println(userDao.queryUserInfoByUserId("001"));
        System.out.println(token);
    }
}
