package com.csu.springframework.test;

import com.csu.springframework.test.beans.UserService;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;
import com.csu.springframework.test.beans.factory.support.DefaultListableBeanFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class ApiTest {


    @Test
    public void testBeanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        factory.registerBeanDefinition("userService", beanDefinition);


        UserService userService1 = (UserService) factory.getBean("userService");
        userService1.queryUserInfo();

        UserService userService = (UserService) factory.getBean("userService", "陈宇");
        userService.queryUserInfo();

    }


    public static void main(String[] args) throws NoSuchMethodException {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(UserService.class);

        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });

        Object o = enhancer.create();
        System.out.println(o);
    }
}
