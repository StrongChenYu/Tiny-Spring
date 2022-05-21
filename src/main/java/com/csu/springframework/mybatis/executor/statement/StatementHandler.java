package com.csu.springframework.mybatis.executor.statement;

import com.csu.springframework.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface StatementHandler {

    /**
     * prepare大概的含义就是初始化这个statement然后设置一下基本的参数
     * @param connection
     * @return
     * @throws SQLException
     */
    Statement prepare(Connection connection) throws SQLException;

    void parameterize(Statement statement) throws SQLException;

    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

}
