package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.ConfigurableListableBeanFactory;

public interface BeanFactoryPostProcessor {
    //todo
    void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException;
}
