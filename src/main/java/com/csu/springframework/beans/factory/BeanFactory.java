package com.csu.springframework.beans.factory;


import com.csu.springframework.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName);

    Object getBean(String beanName, Object... args);

    <T> T getBean(String beanName, Class<T> clazz);

    <T> T getBean(Class<T> clazz) throws BeansException;
}
