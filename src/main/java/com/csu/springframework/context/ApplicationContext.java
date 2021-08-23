package com.csu.springframework.context;

import com.csu.springframework.beans.factory.HierarchicalBeanFactory;
import com.csu.springframework.beans.factory.ListableBeanFactory;
import com.csu.springframework.core.io.ResourceLoader;

public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {

}
