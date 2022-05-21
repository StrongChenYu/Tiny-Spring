package com.csu.springframework.mybatis.session.defaults;

import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.executor.Executor;
import com.csu.springframework.mybatis.mapping.Environment;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;
import com.csu.springframework.mybatis.session.SqlSessionFactory;
import com.csu.springframework.mybatis.transaction.Transaction;
import com.csu.springframework.mybatis.transaction.TransactionFactory;
import com.csu.springframework.mybatis.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSession(null, false);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel isolationLevel, boolean autoCommit) {
        Transaction tx = null;
        try {
            DataSource source = configuration.getEnvironment().getDataSource();

            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(source, isolationLevel, autoCommit);
            // executor要传入一个transaction
            Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSession(null, autoCommit);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel isolationLevel) {
        return openSession(isolationLevel, false);
    }
}
