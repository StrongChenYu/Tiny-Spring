package com.csu.springframework.aop;


import org.aopalliance.intercept.MethodInterceptor;

public class AdvisedSupport {
    private TargetSource targetSource;
    private MethodMatcher methodMatcher;
    private MethodInterceptor methodInterceptor;
    private boolean proxyTargetClass = false;

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
