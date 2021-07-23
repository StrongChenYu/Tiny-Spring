package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.factory.DisposableBean;

public interface SingletonBeanRegistry {
    Object getSingleton(String beanName);
    void destroySingletons();
    void addSingleton(String beanName, Object object);
    void registerDisposableBean(String beanName, DisposableBean bean);
}
