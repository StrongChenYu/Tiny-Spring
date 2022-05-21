package com.csu.springframework.mybatis.executor.result;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement statement) throws SQLException;
}
