package com.csu.springframework.test.beans.factory.support;

import com.csu.springframework.test.beans.BeansException;
import com.csu.springframework.test.beans.factory.BeanFactory;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;
import com.csu.springframework.test.beans.factory.config.DefaultSingletonBeanRegistry;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String beanName, Object... args) {
        return doGetBean(beanName, args);
    }


    @Override
    public Object getBean(String beanName) {
        return doGetBean(beanName, null);
    }


    private <T> T doGetBean(String beanName, Object[] args) {
        Object bean = getSingleton(beanName);
        if (bean != null) {
            return (T) bean;
        }

        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return (T) createBean(beanName, beanDefinition, args);
    }

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
