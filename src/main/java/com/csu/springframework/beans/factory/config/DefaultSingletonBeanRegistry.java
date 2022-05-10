package com.csu.springframework.beans.factory.config;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.DisposableBean;
import com.csu.springframework.beans.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    // 一级缓存
    private final Map<String, Object> singleObjects = new ConcurrentHashMap<>();

    // 二级缓存
    private final Map<String, Object> earlySingletonObjects = new HashMap<>();

    // 三级缓存
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    // 需要销毁的bean的集合
    private final Map<String, DisposableBean> disposableBeans = new ConcurrentHashMap<>();

    protected static final Object NULL_OBJECT = new Object();

    @Override
    public Object getSingleton(String beanName) {
        /**
         * 这个的思路是这样的：
         * 1. 一级缓存存放普通的bean
         * 2. 二级缓存存放aop代理的bean
         * 3. 三级缓存存放beanFactory的bean
         *
         * 如果一级缓存没有，就去二级缓存中去找
         * 二级缓存中没有，就去三级缓存中寻找
         * 然后把三级缓存，通过FactoryBean生成的bean存放到一级缓存中
         */
        Object singletonObject = singleObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = earlySingletonObjects.get(beanName);

            if (singletonObject == null) {
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);

                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    @Override
    public void registerSingleton(String beanName, Object object) {
        singleObjects.put(beanName, object);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    /**
     * singletonFactories是三级缓存
     * 也就是说
     * 把singletonFactory对象放入到三级缓存中，并且从二级缓存中移除
     * @param beanName
     * @param singletonFactory
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!this.singletonFactories.containsKey(beanName)) {
            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
        }
    }

    @Override
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    @Override
    public void destroySingletons() {
        Set<String> keySet = this.disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();

        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' throw an exception", e);
            }
        }
    }
}
