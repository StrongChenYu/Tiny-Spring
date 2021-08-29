package com.csu.springframework.test.aop.jdkproxy;

public class RequestImpl implements RequestAble {
    @Override
    public void request() {
        System.out.println("invoke request");
    }
}
