package com.csu.springframework.test.beans.factory.config;

public interface SingletonBeanRegistry {
    Object getSingleton(String beanName);
}
