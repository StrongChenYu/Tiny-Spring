package com.csu.springframework.mybatis.session.defaults;

import cn.hutool.core.date.DateTime;
import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.executor.Executor;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.Environment;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private Executor executor;


    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        System.out.println(statement);
        System.out.println("你被代理了");
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        List<T> result = executor.query(mappedStatement, parameter, Executor.NO_RESULT_HANDLER, mappedStatement.getBoundSql());
        return result.get(0);
    }



    public static void main(String[] args) {
        System.out.println("id".split("_")[0]);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMappers(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
