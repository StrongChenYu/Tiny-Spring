package com.csu.springframework.mybatis.executor;

import com.csu.springframework.mybatis.executor.statement.StatementHandler;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.ResultHandler;
import com.csu.springframework.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <T> List<T> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
//            Configuration configuration = this.configuration;??????????
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            // 模板方法三部曲
            // 严格意义不算模板方法
            // 1.
            Statement stmt = handler.prepare(connection);
            // 2.
            handler.parameterize(stmt);
            // 3.
            return handler.query(stmt, resultHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
