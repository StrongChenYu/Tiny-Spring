package com.csu.springframework.test.aop;

import java.util.Random;

public interface IUserService {

    String queryUserInfo();
    String register(String userName);
    void testToken();
}
