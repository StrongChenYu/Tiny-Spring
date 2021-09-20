package com.csu.springframework.test.annotation;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import com.csu.springframework.test.annotation.bean.UserService;
import org.junit.Test;

public class ApiTest {

    @Test
    public void Test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:annotationConfig/spring.xml");
        UserService bean = context.getBean(UserService.class);
        bean.queryInfo();;
    }
}
