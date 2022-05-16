package com.csu.springframework.mybatis.binding;

import com.csu.springframework.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    private final Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class)) {
            // 如果是Object中的方法，就不代理
            return method.invoke(this, args);
        } else {
            // 代理逻辑
            return cacheMapperMethod(method).execute(sqlSession, args);
        }
    }

    /**
     * 去缓存中寻找mapperMethod
     * 如果没有找到就new一个
     * @param method
     * @return
     */
    private MapperMethod cacheMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            mapperMethod = new MapperMethod(sqlSession.getConfiguration(), mapperInterface, method);
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }


}
