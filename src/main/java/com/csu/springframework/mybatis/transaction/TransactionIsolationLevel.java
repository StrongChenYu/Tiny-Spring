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
}
