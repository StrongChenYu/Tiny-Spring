package com.csu.springframework.test.mybatis;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

public class ApiTest {

    @Test
    public void test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:mybatis/mybatis.xml");
        context.registerShutdownHook();

        TestBean bean = context.getBean(TestBean.class);
        System.out.println(bean);
        System.out.println(bean.getName());
    }
}
