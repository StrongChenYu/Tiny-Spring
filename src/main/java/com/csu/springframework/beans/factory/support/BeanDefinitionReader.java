package com.csu.springframework.beans.factory.support;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.core.io.Resource;
import com.csu.springframework.core.io.ResourceLoader;

public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    void loadBeanDefinition(Resource resource) throws BeansException;

    void loadBeanDefinition(Resource... resources) throws BeansException;

    void loadBeanDefinition(String path) throws BeansException;
}
