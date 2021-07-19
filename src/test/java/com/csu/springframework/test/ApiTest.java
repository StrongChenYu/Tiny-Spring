package com.csu.springframework.test;

import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.test.beans.UserDao;
import com.csu.springframework.test.beans.UserService;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.support.DefaultListableBeanFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;

public class ApiTest {


    @Test
    public void testBeanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        BeanDefinition beanDefinition = new BeanDefinition(UserService.class, null);
        factory.registerBeanDefinition("userService", beanDefinition);


        UserService userService1 = (UserService) factory.getBean("userService");
        userService1.queryUserInfo();

    }

    @Test
    public void testBeanPropertySetting() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "Service For User"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        BeanDefinition beanDefinition1 = new BeanDefinition(UserService.class, propertyValues);
        factory.registerBeanDefinition("userService", beanDefinition1);

        BeanDefinition beanDefinition2 = new BeanDefinition(UserDao.class, null);
        factory.registerBeanDefinition("userDao", beanDefinition2);

        UserService userService = (UserService) factory.getBean("userService");
        userService.queryUserInfo("001");
    }


    public static void main(String[] args) throws NoSuchMethodException {
        Object o = new Object();
        ClassLoader classLoader = o.getClass().getClassLoader();
        classLoader.getResourceAsStream("/123.txt");
    }
}
