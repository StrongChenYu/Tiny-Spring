package com.csu.springframework.test.mybatis;

import com.csu.springframework.context.annotation.Component;

@Component
public class TestBean {

    private String name;

    public TestBean() {
        name = "chenyu";
    }

    public String getName() {
        return name;
    }
}
