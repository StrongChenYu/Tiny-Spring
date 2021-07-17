package com.csu.springframework.test;

import com.csu.springframework.test.beans.UserService;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;
import com.csu.springframework.test.beans.factory.support.DefaultListableBeanFactory;
import org.junit.Test;

public class ApiTest {

    @Test
    public void testBeanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        factory.registerBeanDefinition("userService", beanDefinition);

        UserService userService = (UserService) factory.getBean("userService");
        userService.queryUserInfo();


    }
}
