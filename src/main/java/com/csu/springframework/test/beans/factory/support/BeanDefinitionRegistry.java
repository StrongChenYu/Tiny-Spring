package com.csu.springframework.test.beans.factory.support;

import com.csu.springframework.test.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
