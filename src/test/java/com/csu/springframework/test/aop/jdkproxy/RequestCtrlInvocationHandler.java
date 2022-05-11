package com.csu.springframework.test.aop.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK动态代理要实现InvocationHandler接口
 */
public class RequestCtrlInvocationHandler implements InvocationHandler {

    private Object target;

    public RequestCtrlInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("request")) {
            System.out.println("invoke method");
            return method.invoke(target, args);
        }
        return null;
    }
}
