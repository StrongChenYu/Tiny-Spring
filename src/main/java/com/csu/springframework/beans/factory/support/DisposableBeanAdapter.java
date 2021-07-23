package com.csu.springframework.beans.factory.support;

import cn.hutool.core.util.StrUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.DisposableBean;
import com.csu.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Method;

public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;
    private final String beanName;
    private final String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }

        if (StrUtil.isNotEmpty(destroyMethodName)
                && !(bean instanceof DisposableBean)
                && destroyMethodName.equals("destroy")) {

            Method destroyMethod = null;
            try {
                destroyMethod = bean.getClass().getMethod(destroyMethodName);
            } catch (NoSuchMethodException e) {
                throw new BeansException("Could not find an init method named '" + destroyMethodName + "' on bean with name '" + beanName + "'", e);
            }

            destroyMethod.invoke(bean);
        }
    }
}
