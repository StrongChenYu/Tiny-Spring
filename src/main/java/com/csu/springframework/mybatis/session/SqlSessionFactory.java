package com.csu.springframework.mybatis.session;

import com.csu.springframework.mybatis.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;

public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(TransactionIsolationLevel isolationLevel, boolean autoCommit);

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(TransactionIsolationLevel isolationLevel);
}
