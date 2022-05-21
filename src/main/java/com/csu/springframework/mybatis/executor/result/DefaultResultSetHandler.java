package com.csu.springframework.mybatis.executor.result;

import com.csu.springframework.mybatis.executor.Executor;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DefaultResultSetHandler implements ResultSetHandler {

    private final BoundSql boundSql;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public <E> List<E> handleResultSets(Statement statement) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        try {
            return result2Obj(resultSet, Class.forName(boundSql.getResultType()));
        } catch (Exception e) {
            throw new SQLException("error in parsing result cause: " + boundSql.getResultType() + "can't find");
        }
    }

    private <T> List<T> result2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> resultList = new ArrayList<>();
        try {
            // 获取mysql表的源数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colCnt = metaData.getColumnCount();

            while (resultSet.next()) {
                T res = (T) clazz.newInstance();
                for (int i = 1; i <= colCnt; i++) {
                    Object value = resultSet.getObject(i);
                    String colName = metaData.getColumnName(i);

                    String setMethodName = getSetName(colName);

                    Method method = null;
                    // todo: 各种日期...真麻烦
                    method = clazz.getMethod(setMethodName, value.getClass());
                    method.invoke(res, value);
                }
                resultList.add(res);
            }

            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSetName(String colName) {
        String[] names = colName.split("_");

        StringBuilder builder = new StringBuilder("set");
        // todo: 这里先假设 names长度必大于1

        for (int i = 0; i < names.length; i++) {
            String firstUpper = names[i].substring(0,1).toUpperCase(Locale.ENGLISH) + names[i].substring(1);
            builder.append(firstUpper);
        }

        return builder.toString();
    }
}
