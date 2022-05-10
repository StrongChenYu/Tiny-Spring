package com.csu.springframework.context.support;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csu.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;
import com.csu.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.csu.springframework.context.ApplicationEvent;
import com.csu.springframework.context.ApplicationListener;
import com.csu.springframework.context.ConfigurableApplicationContext;
import com.csu.springframework.context.event.ApplicationEventMulticaster;
import com.csu.springframework.context.event.ContextClosedEvent;
import com.csu.springframework.context.event.ContextRefreshedEvent;
import com.csu.springframework.context.event.SimpleApplicationEventMulticaster;
import com.csu.springframework.core.io.DefaultResourceLoader;
import com.csu.springframework.core.io.convert.ConversionService;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void refresh() throws BeansException {
        /**
         * 1. 加载BeanDefinition
         * 加载beanDefinition，把xml文件中的内容读取出来，然后加载到beanDefinition中
         */
        refreshBeanFactory();

        /**
         * 2. 上一步中会初始化一个DefaultListableBeanFactory
         * 这一步主要是get到这个factory
         */
        ConfigurableListableBeanFactory factory = getBeanFactory();

        /**
         * 3. 因为有的bean实现了contextAware接口
         * 所有在这个地方就自己定义了一个postProcessor
         * 这个processor中含有context对象
         * 到时候调用postprocessor处理的时候
         * 就可以把这个类注入进去
         */
        factory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        /**
         * 4. 调用factoryPostProcessor
         * 注意：这个地方会调用getBean()方法！会初始化BeanFactoryPostProcessor
         */
        invokeBeanFactoryPostProcessor(factory);

        /**
         * 5. 注册beanPostProcessor
         * 注意：这个地方也会getBean()方法，那么会初始化所有BeanPostProcessor
         */
        registerBeanPostProcessor(factory);

        /**
         * 6. 关于事件的bean
         * 主要是先生成广播器
         */
        initApplicationEventMulticaster();

        /**
         * 7. 注册listener
         * 后把容器中的listener类型的bean get出来
         * 然后注册到广播器中
         */
        registerListener();

        // 8. 这里会把所有的bean全部调用一次getBean方法
        finishBeanFactoryInitialization(factory);

        // 9. finish
        finishRefresh();
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean("conversionService")) {
            Object conversionService = beanFactory.getBean("conversionService");
            if (conversionService instanceof ConversionService) {
                beanFactory.setConversionService((ConversionService) conversionService);
            }
        }

        /**
         * 这里说明会把所有的singleton类型的bean全部初始化
         * 在Spring里面也会做同样的事情
         */
        beanFactory.preInstantiateSingletons();
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
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
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
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

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
