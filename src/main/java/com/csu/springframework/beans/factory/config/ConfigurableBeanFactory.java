package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.factory.HierarchicalBeanFactory;
import com.csu.springframework.util.StringValueResolver;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void addEmbeddedValueResolver(StringValueResolver resolver);

    String resolveEmbeddedValue(String value);
}
