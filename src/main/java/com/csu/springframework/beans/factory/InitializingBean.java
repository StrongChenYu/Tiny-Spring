package com.csu.springframework.beans.factory;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
