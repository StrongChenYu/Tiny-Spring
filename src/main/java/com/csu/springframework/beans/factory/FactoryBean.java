package com.csu.springframework.beans.factory;

/**
 * @Author Chen Yu
 * @Date 2021/8/20 19:21
 */
public interface FactoryBean<T> {
    T getObject() throws Exception;
    Class<?> getObjectType();
    boolean isSingleton();
}
