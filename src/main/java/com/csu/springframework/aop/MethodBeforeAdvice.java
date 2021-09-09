package com.csu.springframework.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice {
    // 第三个参数不知道在干什么
    void before(Method method, Object[] args, Object target) throws Throwable;
}
