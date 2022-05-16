package com.csu.springframework.test.mybatis;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.builder.xml.XMLConfigBuilder;
import com.csu.springframework.mybatis.io.Resources;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.dom4j.Element;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public class ApiTest {

    @Test
    public void test_spring_integrate() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:mybatis/mybatis.xml");
        context.registerShutdownHook();

        TestBean bean = context.getBean(TestBean.class);
        System.out.println(bean);
        System.out.println(bean.getName());
    }

    @Test
    public void test_MapperProxyFactory() {

    }

    @Test
    public void test_XMlReader() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis/mybatis.xml");
        XMLConfigBuilder builder = new XMLConfigBuilder(reader);
        Element root = builder.getRoot();
        builder.environmentsElement(root.element("environments"));
    }

}
