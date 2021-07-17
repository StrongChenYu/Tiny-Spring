package com.csu.springframework.test.beans.factory.support;

import com.csu.springframework.test.beans.BeansException;
import com.csu.springframework.test.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleInstantiationStrategy implements InstantiationStrategy {

    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args) throws BeansException {
        Class beanClass = beanDefinition.getBeanClass();

        try {

            if (ctor == null) {
                //没有构造函数的信息就默认构造函数
                return beanClass.getDeclaredConstructor().newInstance();
            } else {
                //有函数构造信息就传入参数
                return beanClass.getDeclaredConstructor(ctor.getParameterTypes()).newInstance(args);
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new BeansException("Failed to instantiate[" + beanClass.getName() + "]", e);
        }
    }
}
