package com.csu.springframework.mybatis.executor;

import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.ResultHandler;
import com.csu.springframework.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    <T> List<T> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollBack);
}
