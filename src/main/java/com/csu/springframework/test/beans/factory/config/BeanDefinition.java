package com.csu.springframework.test.beans.factory.config;

public class BeanDefinition {

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    private Class beanClass;

    public BeanDefinition() {

    }

    public Class getBeanClass() {
        return beanClass;
    }

    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }
}
