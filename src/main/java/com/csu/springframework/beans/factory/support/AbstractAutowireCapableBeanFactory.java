package com.csu.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    private final InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;

        try {
            //create bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            //support bean property
            applyPropertyValues(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        addSingleton(beanName, bean);
        return bean;
    }

    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValueList()) {

            String name = propertyValue.getName();
            Object value = propertyValue.getValue();

            if (value instanceof BeanReference) {
                String beanReferenceBeanName = ((BeanReference) value).getBeanName();

                value = getBean((beanReferenceBeanName));
            }

            BeanUtil.setFieldValue(bean, name, value);
            //setBeanField(bean, name, value, beanName);
        }

    }

    /**
     * getDeclared只能获取到自己的属性，无法获取到父类的属性
     * 而CGlib代理是通过生成一个子类的方式获取到的
     * 2021.7.18 已经修复bug了
     * @param bean
     * @param fieldName
     * @param value
     * @param beanName
     */
    private void setBeanField(Object bean, String fieldName, Object value, String beanName) {

        try {
            Class<?> clazz = bean.getClass();
            Field declaredField = getField(clazz, fieldName);
            Class<?> type = declaredField.getType();

            declaredField.setAccessible(true);
            declaredField.set(bean, type.cast(value));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BeansException("Error setting: " + fieldName + " property values in " + beanName);
        }

    }

    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getName().equals(fieldName)) {
                return declaredField;
            }
        }

        if (clazz.getSuperclass() != null) {
            return getField(clazz.getSuperclass(), fieldName);
        }

        throw new NoSuchFieldException();
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUser = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            //todo remember add more condition
            if (args != null && constructor.getParameterTypes().length == args.length) {
                constructorToUser = constructor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUser, args);
    }


    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

}
