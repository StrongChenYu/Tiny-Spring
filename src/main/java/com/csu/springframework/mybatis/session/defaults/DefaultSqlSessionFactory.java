package com.csu.springframework.mybatis.session.defaults;

import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
