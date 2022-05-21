package com.csu.springframework.mybatis.transaction;

import java.sql.Connection;

public enum TransactionIsolationLevel {

    NONE(Connection.TRANSACTION_NONE),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZATION(Connection.TRANSACTION_SERIALIZABLE);

    private final int levelId;

    TransactionIsolationLevel(int levelId) {
        this.levelId = levelId;
    }

    public int getLevel() {
        return levelId;
    }

    /**
     * 这个default是为了默认的情况
     * 但是默认的情况就不如不设置为好
     * 直接传null
     * @return
     */
    @Deprecated
    public static TransactionIsolationLevel getDefault() {
        return TransactionIsolationLevel.READ_COMMITTED;
    }
}
