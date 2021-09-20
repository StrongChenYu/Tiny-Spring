package com.csu.springframework.test.xmlscan;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

public class ApiTest {


    @Test
    public void testPlaceHolder() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:scan/spring-property.xml");
        UserService userService = applicationContext.getBean("userService", UserService.class);
        System.out.println(userService.getToken());
    }

    @Test
    public void testAutoScanXml() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:scan/spring.xml");
        UserService userService = applicationContext.getBean("myUserService", UserService.class);
        System.out.println(userService.queryUserInfo());
    }
}
