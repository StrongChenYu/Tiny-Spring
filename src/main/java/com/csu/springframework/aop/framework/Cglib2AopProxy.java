package com.csu.springframework.aop.framework;

import com.csu.springframework.aop.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class Cglib2AopProxy implements AopProxy {

    private final AdvisedSupport support;

    public Cglib2AopProxy(AdvisedSupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(support.getTargetSource().getTarget().getClass());
        enhancer.setInterfaces(support.getTargetSource().getTargetClass());
        enhancer.setCallback(new DynamicAdvisedInterceptor(support));
        return enhancer.create();
    }

    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport support;

        private DynamicAdvisedInterceptor(AdvisedSupport advisedSupport) {
            this.support = advisedSupport;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation(
                    support.getTargetSource().getTarget(),
                    method,
                    args,
                    proxy
            );

            if (support.getMethodMatcher().matches(method, support.getTargetSource().getTarget().getClass())) {
                return support.getMethodInterceptor().invoke(methodInvocation);
            }
            return methodInvocation.proceed();
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.target, this.arguments);
        }
    }


}
