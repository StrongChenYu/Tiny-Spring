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
        if (support.getMethodMatcher().matches(method, support.getTargetSource().getTarget().getClass())) {
            MethodInterceptor methodInterceptor = support.getMethodInterceptor();
            return methodInterceptor.invoke(
                    new ReflectiveMethodInvocation(
                            support.getTargetSource().getTarget(),
                            method,
                            args
                    )
            );
        }
        // ?????
        // 为什么不是method.invoke(proxy, args)
        return method.invoke(support.getTargetSource().getTarget(), args);
    }
}
