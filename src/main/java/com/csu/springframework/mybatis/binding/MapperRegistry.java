package com.csu.springframework.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 主要用来管理MapperProxyFactory对象，和DAO对象之间的关系
 * 一个Dao => 对应一个MapperProxyFactory对象
 */
public class MapperRegistry {

    private final Map<Class<?>, MapperProxyFactory<?>> mappers = new HashMap<>();
    private Configuration configuration;

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public MapperRegistry() {

    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        /**
         * 根据type去获取这个mapper代理的类
         * 比如说对于UserDao来说
         * UserDao本身是一个interface
         * 那么这里就会根据UserDao的Class去new一个代理对象
         */
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) mappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry");
        }

        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry");
            }

            mappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public boolean hasMapper(Class<?> type) {
        return mappers.containsKey(type);
    }

    public void addMappers(String packageName) {
        Set<Class<?>> classes = ClassScanner.scanPackage(packageName);
        for (Class<?> aClass : classes) {
            addMapper(aClass);
        }
    }
}
