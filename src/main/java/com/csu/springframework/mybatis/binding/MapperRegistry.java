package com.csu.springframework.mybatis.binding;

import com.csu.springframework.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * 主要用来管理MapperProxyFactory对象，和DAO对象之间的关系
 */
public class MapperRegistry {

    private final Map<Class<?>, MapperProxyFactory<?>> mappers = new HashMap<>();

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
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

    private boolean hasMapper(Class<?> type) {
        return mappers.containsKey(type);
    }

    public void addMappers(String packageName) {
        //todo
    }
}
