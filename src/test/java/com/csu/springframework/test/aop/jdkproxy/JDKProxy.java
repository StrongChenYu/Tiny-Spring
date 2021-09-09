package com.csu.springframework.test.aop.jdkproxy;

import java.lang.reflect.Proxy;

public class JDKProxy {

    public static void main(String[] args) {
        RequestAble requestAble = (RequestAble) Proxy.newProxyInstance(
                JDKProxy.class.getClassLoader(),
                new Class[]{RequestAble.class},
                new RequestCtrlInvocationHandler(new RequestImpl())
        );
        requestAble.request().request();
    }
}
