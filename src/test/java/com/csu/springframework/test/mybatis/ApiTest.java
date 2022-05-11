package com.csu.springframework.test.mybatis;

import com.csu.springframework.context.support.ClassPathXmlApplicationContext;
import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;

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
        MapperRegistry registry = new MapperRegistry();
        registry.addMapper(UserDao.class);

        DefaultSqlSessionFactory factory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = factory.openSession();

        UserDao mapper = registry.getMapper(UserDao.class, sqlSession);
        mapper.queryByUserName("");
    }

}
