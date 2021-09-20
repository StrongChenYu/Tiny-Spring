package com.csu.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.*;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;

        try {
            bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (bean != null) {
                // 这里是不是需要处理一些东西，填充属性
                return bean;
            }

            //create bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 在apply之前把自动注入的属性都加入到properties中
            applyBeanPostProcessorsBeforeApplyPropertyValues(beanName, bean, beanDefinition);

            //support bean property
            applyPropertyValues(beanName, bean, beanDefinition);

            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        if (beanDefinition.isSingleton()) {
            addSingleton(beanName, bean);
        }
        return bean;
    }

    protected void applyBeanPostProcessorsBeforeApplyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);

                if (pvs != null) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }

    private Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean != null) {
            // before?????
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    private Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                Object proxy = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
                if (proxy != null) {
                    return proxy;
                }
            }
        }
        return null;
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        //注入aware对象
        injectAwareBean(beanName, bean, beanDefinition);

        //前置处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        //调用初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        //后置处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

        return wrappedBean;
    }

    private void injectAwareBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (!(bean instanceof Aware)) {
            return;
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        if (bean instanceof BeanClassLoaderAware) {
            ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
        }
    }

    protected void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        //如果bean实现了InitializingBean接口，那么直接调用
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
            //这里是不是该加一个return呀
        }

        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName)) {

            Method initMethod = null;
            try {
                 initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            } catch (NoSuchMethodException e) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'", e);
            }

            initMethod.invoke(bean);
        }
    }


    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

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

    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object bean = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);

            if (current == null) {
                return bean;
            }

            bean = current;
        }
        return bean;
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)throws BeansException {
        Object bean = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessAfterInitialization(bean, beanName);

            if (current == null) {
                return bean;
            }

            bean = current;
        }
        return bean;
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (!beanDefinition.isSingleton()) {
            return;
        }

        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }

}
