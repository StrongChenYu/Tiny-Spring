package com.csu.springframework.mybatis.executor.statement;

import com.csu.springframework.mybatis.executor.Executor;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleStatementHandler extends BaseStatementHandler {

    protected SimpleStatementHandler(Configuration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        super(configuration, executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        // 这个statement里面并不需要填入参数
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.executeQuery(sql);
        return resultSetHandler.handleResultSets(statement);
    }
}
