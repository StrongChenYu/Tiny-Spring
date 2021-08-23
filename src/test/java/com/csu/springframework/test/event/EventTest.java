package com.csu.springframework.test.event;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

public class EventTest {

    @Test
    public void Test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.publishEvent(new CustomEvent(applicationContext, 1231312L, "Success"));
        applicationContext.registerShutdownHook();
    }
}
