package com.csu.springframework.aop;

public interface ClassFilter {
    boolean matches(Class<?> clazz);
}
