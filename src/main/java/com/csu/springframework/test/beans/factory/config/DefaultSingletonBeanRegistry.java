package com.csu.springframework.test.beans.factory.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private final Map<String, Object> singleObjects = new ConcurrentHashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return singleObjects.get(beanName);
    }

    protected void addSingleton(String beanName, Object object) {
        singleObjects.put(beanName, object);
    }
}
