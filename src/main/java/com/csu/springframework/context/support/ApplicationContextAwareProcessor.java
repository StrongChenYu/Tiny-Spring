package com.csu.springframework.context.support;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.ApplicationContextAware;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.context.ApplicationContext;

/**
 * 这个方法不仅仅是
 * 注入ApplicationContext的
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
