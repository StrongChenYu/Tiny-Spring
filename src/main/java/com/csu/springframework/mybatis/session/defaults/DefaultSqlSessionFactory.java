package com.csu.springframework.mybatis.session.defaults;

import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
