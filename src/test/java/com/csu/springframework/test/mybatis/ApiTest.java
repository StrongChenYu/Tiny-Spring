package com.csu.springframework.test.mybatis;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.builder.xml.XMLConfigBuilder;
import com.csu.springframework.mybatis.io.Resources;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.SqlSessionFactory;
import com.csu.springframework.mybatis.session.SqlSessionFactoryBuilder;
import com.csu.springframework.mybatis.session.defaults.DefaultSqlSessionFactory;
import com.csu.springframework.test.mybatis.dao.UserDao;
import com.csu.springframework.test.mybatis.po.User;
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
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis/mybatis.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDao userDao = sqlSession.getMapper(UserDao.class);
        for (int i = 0; i < 100; i++) {
            User user = userDao.queryUserInfoById(0L);
            System.out.println(user);
        }
    }

}
