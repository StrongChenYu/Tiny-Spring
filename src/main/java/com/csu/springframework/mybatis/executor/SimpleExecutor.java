package com.csu.springframework.mybatis.executor;

import com.csu.springframework.mybatis.executor.statement.StatementHandler;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.ResultHandler;
import com.csu.springframework.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <T> List<T> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        Connection connection = null;
        try {
//            Configuration configuration = this.configuration;??????????
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            connection = transaction.getConnection();
            // 模板方法三部曲
            // 严格意义不算模板方法
            // 1.
            Statement stmt = handler.prepare(connection);
            // 2.
            handler.parameterize(stmt);
            // 3.
            return handler.query(stmt, resultHandler);
        } catch (Exception e) {
            // 忘关了woc
            // 到底关不关？？？
            // todo
            // 我觉得必须关
            // 因为数据库连接池会超时了帮你关掉的
            // 但是如果你不关，就一定要手动提交掉，不然会一直卡在这个地方
            // 如果关的是一个带有insert和update的事务
            // 那么这个close会被代理，如果没有设置autocommit，会自动回滚
            e.printStackTrace();
            return null;
        } finally {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
