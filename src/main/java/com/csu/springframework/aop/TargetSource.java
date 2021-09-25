package com.csu.springframework.aop;

import com.csu.springframework.util.ClassUtils;

public class TargetSource {
    private final Object target;


    public TargetSource(Object target) {
        this.target = target;
    }

    public Class<?>[] getTargetClass() {
        Class<?> clazz = this.target.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        Class<?>[] interfaces = clazz.getInterfaces();
        return interfaces;
    }

    public Object getTarget() {
        return target;
    }
}
