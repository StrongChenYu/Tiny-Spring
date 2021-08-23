package com.csu.springframework.context.event;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.BeanFactory;
import com.csu.springframework.beans.factory.BeanFactoryAware;
import com.csu.springframework.context.ApplicationEvent;
import com.csu.springframework.context.ApplicationListener;
import com.csu.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    public final Set<ApplicationListener <ApplicationEvent>> applicationListeners = new LinkedHashSet<>();
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    /**
     * 找到所有监听event事件的listener
     * 然后打包成集合然后返回
     * @param event
     * @return
     */
    protected Collection<ApplicationListener<ApplicationEvent>> getApplicationListener(ApplicationEvent event) {
        LinkedList<ApplicationListener<ApplicationEvent>> allListener = new LinkedList<>();
        for (ApplicationListener<ApplicationEvent> applicationListener : applicationListeners) {
            if (supportsEvent(applicationListener, event)) {
                allListener.add(applicationListener);
            }
        }
        return allListener;
    }

    /**
     * 检查这个listener监听的是不是event事件
     * 具体检查方式是通过
     * 检查这个listener implement ApplicationListener
     * implement中后面的泛型，和传入的参数event是不是同一个类
     * 或者可不可以赋值
     * 所以用到了反射里的一系列方法，去get那个泛型代表的类
     * @param applicationListener
     * @param event
     * @return
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
        Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();

        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
        Type genericInterface = targetClass.getGenericInterfaces()[0];

        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();

        Class<?> eventClass;
        try {
            eventClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("wrong event class name: " + className);
        }

        return eventClass.isAssignableFrom(event.getClass());
    }
}
