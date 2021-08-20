package com.csu.springframework.test;

import cn.hutool.core.io.IoUtil;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import com.csu.springframework.core.io.DefaultResourceLoader;
import com.csu.springframework.core.io.Resource;
import com.csu.springframework.test.beans.MyBeanFactoryPostProcessor;
import com.csu.springframework.test.beans.MyBeanPostProcessor;
import com.csu.springframework.test.beans.UserDao;
import com.csu.springframework.test.beans.UserService;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

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
        propertyValues.addPropertyValue(new PropertyValue("uId", "Service For User"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        BeanDefinition beanDefinition1 = new BeanDefinition(UserService.class, propertyValues);
        factory.registerBeanDefinition("userService", beanDefinition1);

        BeanDefinition beanDefinition2 = new BeanDefinition(UserDao.class, null);
        factory.registerBeanDefinition("userDao", beanDefinition2);

        UserService userService = (UserService) factory.getBean("userService");
        userService.queryUserInfo("001");
    }

    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() throws IOException {
        resourceLoader = new DefaultResourceLoader();
    }


    @Test
    public void testClassPath() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void testFile() throws IOException {
        Resource resource = resourceLoader.getResource("src/test/resources/important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }


    @Test
    public void testUrl() throws IOException {
        Resource resource = resourceLoader.getResource("https://www.baidu.com");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void testXMLConfig() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinition("classpath:spring.xml");

        UserService service = factory.getBean("userService", UserService.class);
        UserDao userDao = factory.getBean("userDao", UserDao.class);
        Assert.assertNotNull(userDao);
        service.queryUserInfo("001");
    }

    @Test
    public void testBeanFactoryPostProcessorAndBeanPostProcessor() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinition("classpath:spring.xml");

        BeanFactoryPostProcessor myBeanFactoryPostProcessor = (BeanFactoryPostProcessor) factory.getBean("myBeanFactoryPostProcessor");
        myBeanFactoryPostProcessor.postProcessBeanFactory(factory);

        factory.addBeanPostProcessor((BeanPostProcessor) factory.getBean("myBeanPostProcessor"));

        UserService userService = factory.getBean("userService", UserService.class);

        System.out.println(userService);

    }

    @Test
    public void testXML() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        UserService userService = applicationContext.getBean("userService", UserService.class);
        System.out.println("测试结果：" + userService);
    }

    @Test
    public void testFactoryBean() {

    }

    public static void main(String[] args) throws NoSuchMethodException {
        Object o = new Object();
        ClassLoader classLoader = ApiTest.class.getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("important.properties");
        System.out.println(resourceAsStream);
    }
}
