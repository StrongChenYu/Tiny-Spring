package com.csu.springframework.test.beans.factory.support;

import com.csu.springframework.test.beans.BeansException;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

public interface InstantiationStrategy {

    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args) throws BeansException;
}
