package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValues;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 注意这里的翻译
     * Instantiation
     * instance的“化”词
     * 在这个实例还没有调用构造函数
     * 在内存中不存在的时候
     * 运行这个方法
     * @param clazz
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInstantiation(Class<?> clazz, String beanName) throws BeansException;

    /**
     * 这个方法是对象在内存中以及有了位置
     * 但这个这个对象的各个属性全部都是默认值
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;

    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
