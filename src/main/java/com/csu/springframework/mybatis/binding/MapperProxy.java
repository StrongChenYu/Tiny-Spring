package com.csu.springframework.mybatis.binding;

import com.csu.springframework.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;
    private SqlSession sqlSession;
    private Class<T> mapperInterfaces;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterfaces) {
        this.sqlSession = sqlSession;
        this.mapperInterfaces = mapperInterfaces;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            // 如果是Object中的方法，就不代理
            return method.invoke(this, args);
        } else {
            // 代理逻辑
            return sqlSession.selectOne("");
        }
    }
}
