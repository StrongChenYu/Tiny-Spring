package com.csu.springframework.mybatis.session.defaults;

import cn.hutool.core.date.DateTime;
import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.Environment;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        System.out.println(statement);
        System.out.println("你被代理了");
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        Connection conn = null;
        try {
            // 这里调用各种方法去get封装的对象
            // 这个statement其实是statement的Id
            // 需要通过这个id来把xml文件中的那个元素get出来
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            Environment environment = configuration.getEnvironment();

            // get一个connection
            BoundSql boundSql = mappedStatement.getBoundSql();

            conn = environment.getDataSource().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(boundSql.getSql());

            //把preparedStatement中的？号填满
            // todo: this is just for test
            preparedStatement.setLong(1, 0);
            ResultSet resultSet = preparedStatement.executeQuery();

            // List<T> resultList = result2Obj(resultSet, (T) Class.forName(boundSql.getResultType()));
            // 注意啊，这里不能按T强制转换
            // T和？属于不同的class对象，class对象之间不能强制转换
            List<T> resultList = result2Obj(resultSet, Class.forName(boundSql.getResultType()));

            return resultList.get(0);
        } catch (Exception e) {
            // todo: 这个连接到底关不关？
            e.printStackTrace();
            return null;
        } finally {
            // 把连接关了！！！！
            try {
                conn.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
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

    public static void main(String[] args) {
        System.out.println("id".split("_")[0]);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMappers(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
