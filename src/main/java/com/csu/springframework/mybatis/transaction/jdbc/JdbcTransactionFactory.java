package com.csu.springframework.mybatis.transaction.jdbc;

import com.csu.springframework.mybatis.transaction.Transaction;
import com.csu.springframework.mybatis.transaction.TransactionFactory;
import com.csu.springframework.mybatis.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel isolationLevel, boolean autoCommit) {
        return new JdbcTransaction(dataSource, isolationLevel, autoCommit);
    }
}
