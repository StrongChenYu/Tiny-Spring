package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValues;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    Object postProcessBeforeInstantiation(Class<?> clazz, String beanName) throws BeansException;
    boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
