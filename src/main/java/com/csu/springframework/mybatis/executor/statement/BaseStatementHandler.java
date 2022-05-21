package com.csu.springframework.mybatis.executor.statement;

import com.csu.springframework.mybatis.executor.Executor;
import com.csu.springframework.mybatis.executor.result.ResultSetHandler;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseStatementHandler implements StatementHandler {
    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;
    protected final ResultSetHandler resultSetHandler;

    protected final BoundSql boundSql;

    protected BaseStatementHandler(Configuration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = configuration;
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
        this.boundSql = boundSql;
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        try {
            Statement statement = instantiateStatement(connection);
            statement.setQueryTimeout(30);
            statement.setFetchSize(10000);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;


}
