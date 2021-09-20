package com.csu.springframework.context.support;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csu.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.context.ApplicationEvent;
import com.csu.springframework.context.ApplicationListener;
import com.csu.springframework.context.ConfigurableApplicationContext;
import com.csu.springframework.context.event.ApplicationEventMulticaster;
import com.csu.springframework.context.event.ContextClosedEvent;
import com.csu.springframework.context.event.ContextRefreshedEvent;
import com.csu.springframework.context.event.SimpleApplicationEventMulticaster;
import com.csu.springframework.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void refresh() throws BeansException {
        // 1. 加载BeanDefinition
        refreshBeanFactory();

        // 2. 获取beanFactory
        ConfigurableListableBeanFactory factory = getBeanFactory();

        // 3. 添加ApplicationContextAwareProcessor
        factory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        // 4. 调用factoryPostProcessor
        invokeBeanFactoryPostProcessor(factory);

        // 5. 注册beanPostProcessor
        registerBeanPostProcessor(factory);

        // 6. 初始化事件
        initApplicationEventMulticaster();

        // 7. 注册listener
        registerListener();

        // 8. 这里会把所有的bean全部调用一次getBean方法
        factory.preInstantiateSingletons();

        // 9. finish
        finishRefresh();
    }

    protected void invokeBeanFactoryPostProcessor(ConfigurableListableBeanFactory factory) {
        Map<String, BeanFactoryPostProcessor> beansOfType = factory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor value : beansOfType.values()) {
            value.postProcessBeanFactory(factory);
        }
    }

    protected void registerBeanPostProcessor(ConfigurableListableBeanFactory factory) {
        Map<String, BeanPostProcessor> beansOfType = factory.getBeansOfType(BeanPostProcessor.class);

        for (BeanPostProcessor value : beansOfType.values()) {
            factory.addBeanPostProcessor(value);
        }
    }

    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.addSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    private void registerListener() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        Collection<ApplicationListener> listeners = beanFactory.getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : listeners) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
    }


    private void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        return getBeanFactory().getBean(beanName, clazz);
    }

    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public Object getBean(String beanName, Object... args) {
        return getBeanFactory().getBean(beanName, args);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws BeansException { return getBeanFactory().getBean(clazz); }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        publishEvent(new ContextClosedEvent(this));

        getBeanFactory().destroySingletons();
    }

    protected abstract ConfigurableListableBeanFactory getBeanFactory();
    protected abstract void refreshBeanFactory();

}
