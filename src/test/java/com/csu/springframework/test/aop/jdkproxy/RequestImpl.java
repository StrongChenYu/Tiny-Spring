package com.csu.springframework.test.aop.jdkproxy;

public class RequestImpl implements RequestAble {
    @Override
    public RequestAble request() {
        System.out.println("invoke request");
        return this;
    }
}
