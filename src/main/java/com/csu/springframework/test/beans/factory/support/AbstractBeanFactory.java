package com.csu.springframework.test.beans.factory.support;

import com.csu.springframework.test.beans.BeansException;
import com.csu.springframework.test.beans.factory.BeanFactory;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;
import com.csu.springframework.test.beans.factory.config.DefaultSingletonBeanRegistry;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String beanName) {
        Object bean = getSingleton(beanName);
        if (bean != null) {
            return bean;
        }

        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return createBean(beanName, beanDefinition);
    }

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
