package com.csu.springframework.mybatis.session.defaults;

import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statement) {
        System.out.println("你被代理了");
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return null;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return null;
    }
}
