package com.csu.springframework.aop.framework;

import com.csu.springframework.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKDynamicAopProxy implements AopProxy, InvocationHandler {

    private final AdvisedSupport support;

    public JDKDynamicAopProxy(AdvisedSupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), support.getTargetSource().getTargetClass(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(
                support.getTargetSource().getTarget(),
                method,
                args
        );

        if (support.getMethodMatcher().matches(method, support.getTargetSource().getTarget().getClass())) {
            // 在这里get到interceptor，包括beforeAdvice
            MethodInterceptor methodInterceptor = support.getMethodInterceptor();
            return methodInterceptor.invoke(invocation);
        }

        // ?????
        // 为什么不是method.invoke(proxy, args)
        return invocation.proceed();
    }
}
