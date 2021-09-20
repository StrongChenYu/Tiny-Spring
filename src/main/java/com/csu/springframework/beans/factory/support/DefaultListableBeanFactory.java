package com.csu.springframework.beans.factory.support;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanPostProcessor;

import java.util.*;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new BeansException("No bean named " + beanName + " is defined");
        }

        return beanDefinition;
    }


    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    /**
     * todo
     */
    public String[] getBeanDefinitionNames() {
        Set<String> strings = beanDefinitionMap.keySet();
        return strings.toArray(new String[strings.size()]);
    }


    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> beansMap = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class<?> clazz = beanDefinition.getBeanClass();

            if (type.isAssignableFrom(clazz)) {
                T bean = (T) getBean(beanName);
                beansMap.put(beanName, bean);
            }
        });

        return beansMap;
    }


    @Override
    public void preInstantiateSingletons() throws BeansException {
        for (String s : beanDefinitionMap.keySet()) {
            this.getBean(s);
        }
    }


    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> beanClass = entry.getValue().getBeanClass();
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }

        if (beanNames.size() == 1) {
            return getBean(beanNames.get(0), requiredType);
        }

        throw new BeansException(requiredType + "expected single bean but found " + beanNames.size() + ": " + beanNames);

    }
}
