package com.csu.springframework.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.BeanFactory;
import com.csu.springframework.beans.factory.BeanFactoryAware;
import com.csu.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csu.springframework.util.ClassUtils;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor, InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> clazz, String beanName) throws BeansException {
        return null;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                String value = valueAnnotation.value();
                value = beanFactory.resolveEmbeddedValue(value);
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }

            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
            if (autowiredAnnotation != null) {
                Class<?> type = field.getType();

                Object dependentBean = null;
                if (qualifierAnnotation != null) {
                    String specifiedBeanName = qualifierAnnotation.value();
                    dependentBean = beanFactory.getBean(specifiedBeanName, type);
                } else {
                    dependentBean = beanFactory.getBean(type);
                }
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }

        return pvs;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
