package com.csu.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.*;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.beans.factory.config.BeanReference;
import com.csu.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csu.springframework.core.io.convert.ConversionService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;

        try {
            //create bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            /**
             * 这个地方有问题感觉
             * 因为进入if后的逻辑明显是
             * 判断有无AOP的类
             * 如果存在这个postProcessor的话
             * 就会把这个代理对象放入到三级缓存中
             * 那为什么在开始的时候要判断isSingleton()呢?
             */
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                // getEarlyBeanReference函数会生成一个代理类
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }

            /**
             * 这个地方在控制在初始化之后，是否要进行属性的注入
             * 如果不能的话就直接返回这个bean了
             * 这里返回的bean全部都是初始化的bean
             * 没有填充任何的属性
             */
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }

            /**
             * 那么这个函数就是填充属性了
             */
            applyBeanPostProcessorsBeforeApplyPropertyValues(beanName, bean, beanDefinition);

            //support bean property
            applyPropertyValues(beanName, bean, beanDefinition);

            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        Object exposedObject = bean;
        if (beanDefinition.isSingleton()) {
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
        return exposedObject;
    }

    /**
     * 这个函数的作用其实是在控制bean的生命周期
     * @param beanName
     * @param bean
     * @return 是否可以继续运行
     */
    protected boolean applyBeanPostProcessorsAfterInstantiation(String beanName, Object bean) {
        boolean continueWithPropertyPopulation = true;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                 if (!((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessAfterInstantiation(bean, beanName)) {
                     continueWithPropertyPopulation = false;
                     break;
                 }
            }
        }
        return continueWithPropertyPopulation;
    }

    /**
     * 这个函数跟AOP有关
     * @param beanName bean的名字
     * @param definition bean的meta data
     * @param bean bean
     * @return 被代理的类
     */
    protected Object getEarlyBeanReference(String beanName, BeanDefinition definition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject, beanName);
                if (exposedObject == null) {
                    return null;
                }
            }
        }
        return exposedObject;
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
            } else {
                Class<?> sourceType = value.getClass();
                Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), name);

                ConversionService conversionService = getConversionService();
                if (conversionService != null && conversionService.canConvert(sourceType, targetType)) {
                    value = conversionService.convert(value, targetType);
                }
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
        /**
         * 这里的职责其实应该放到
         * getInstantiationStrategy().instantiate()
         * 这个方法里面
         */
        Constructor constructorToUser = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            //todo: remember add more condition
            /**
             * 这里只添加了构造函数的参数列表长度和传入参数的列表长度相同的情况
             * 还需要判断：
             * 每一个args的类型和期望的类型是否相同
             */
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
