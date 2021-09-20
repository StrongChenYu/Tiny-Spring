package com.csu.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import com.csu.springframework.beans.factory.config.BeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathScanningCandidateComponentProvider {

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> definitions = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);

        for (Class<?> aClass : classes) {
            definitions.add(new BeanDefinition(aClass));
        }

        return definitions;
    }
}
