package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.BeansException;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    Object postProcessBeforeInstantiation(Class<?> clazz, String beanName) throws BeansException;
}
