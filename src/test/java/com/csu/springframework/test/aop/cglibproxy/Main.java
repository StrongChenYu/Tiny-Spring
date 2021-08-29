package com.csu.springframework.test.aop.cglibproxy;

import net.sf.cglib.proxy.Enhancer;

public class Main {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(RequestAble.class);
        enhancer.setCallback(new RequestCtrlCallback());

        RequestAble proxy = (RequestAble) enhancer.create();
        proxy.request();
    }
}
