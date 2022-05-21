package com.csu.springframework.mybatis.transaction.jdbc;

import com.csu.springframework.mybatis.transaction.Transaction;
import com.csu.springframework.mybatis.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction {

    private Connection connection;
    private DataSource dataSource;
    private TransactionIsolationLevel isolationLevel;
    private boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel isolationLevel, boolean autoCommit) {
        this.dataSource = dataSource;
        this.isolationLevel = isolationLevel;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel isolationLevel) {
        this.dataSource = dataSource;
        this.isolationLevel = isolationLevel;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * 肯定是先调用getConnection去get到具体的connection
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            // 因为有两个构造函数
            connection = dataSource.getConnection();

            if (isolationLevel != null) {
                connection.setTransactionIsolation(isolationLevel.getLevel());
            }

            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        // connection的auto commit没有打开的时候才去给他手动commit
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    // 下面两个就不知道为啥需要判断autocommit状态
    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.close();
        }
    }
}
