package com.csu.springframework.test.cyclicdependence;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Assert;
import org.junit.Test;

public class ApiTest {

    @Test
    public void Test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:cyclicdependence/spring.xml");
        IUserService service1 = context.getBean("userService1",IUserService.class);
        IUserService service2 = context.getBean("userService2",IUserService.class);

        System.out.println(service1.getIUserService());
        System.out.println(service2.getIUserService());

        service1.outPutOwnCode();
        service2.getIUserService().outPutOwnCode();

        service2.outPutOwnCode();
        service1.getIUserService().outPutOwnCode();


        System.out.println((service1.getIUserService() == service2));
        System.out.println((service2.getIUserService() == service1));
    }
}
