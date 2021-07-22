package com.csu.springframework.beans.factory;

import com.csu.springframework.beans.BeansException;

public interface AutowireCapableBeanFactory extends BeanFactory {


    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)throws BeansException;

}
