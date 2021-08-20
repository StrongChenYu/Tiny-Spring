package com.csu.springframework.test.factorybean;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @Author Chen Yu
 * @Date 2021/8/20 19:56
 */
public class FactoryBeanTest {

    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 2. 调用代理方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.queryUserInfo("10001");
    }
}
