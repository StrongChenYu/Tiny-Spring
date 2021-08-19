package com.csu.springframework.beans.factory;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.context.ApplicationContext;

public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
