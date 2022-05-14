package com.csu.springframework.mybatis.transaction;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;

public interface TransactionFactory {
    /**
     * 根据connection创建一个新的transaction
     * @param connection datasource
     * @return
     */
    Transaction newTransaction(Connection connection);

    /**
     * 根据下面三个东西创建一个事务
     * @param dataSource
     * @param isolationLevel 隔离级别
     * @param autoCommit 是否自动提交
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel isolationLevel, boolean autoCommit);
}
