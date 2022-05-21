package com.csu.springframework.mybatis.binding;

import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.mapping.SqlCommandType;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * 这个类像是一个中转类
 */
public class MapperMethod {

    private final SqlCommand sqlCommand;

    public MapperMethod(Configuration configuration, Class<?> mapperInterface, Method method) {
        sqlCommand = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        switch (sqlCommand.sqlCommandType) {
            case INSERT:
            case SELECT:
                return sqlSession.selectOne(sqlCommand.getName(), args);
            case UPDATE:
            case DELETE:
            default:
                throw new RuntimeException("Unknown execution method for: " + sqlCommand.getName());
        }
    }

    public static class SqlCommand {
        // 这个name就是这个语句的id
        private final String name;
        private final SqlCommandType sqlCommandType;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementId = mapperInterface.getName() + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
            name = mappedStatement.getId();
            sqlCommandType = mappedStatement.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getSqlCommandType() {
            return sqlCommandType;
        }
    }
}
