package com.csu.springframework.context.event;

import com.csu.springframework.beans.factory.BeanFactory;
import com.csu.springframework.context.ApplicationEvent;
import com.csu.springframework.context.ApplicationListener;

import java.util.Collection;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    public SimpleApplicationEventMulticaster(BeanFactory factory) {
        setBeanFactory(factory);
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        Collection<ApplicationListener<ApplicationEvent>> applicationListeners = getApplicationListener(event);
        for (ApplicationListener<ApplicationEvent> applicationListener : applicationListeners) {
            applicationListener.onApplicationEvent(event);
        }
    }
}
