package com.csu.springframework.beans.factory.support;

import com.csu.springframework.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
