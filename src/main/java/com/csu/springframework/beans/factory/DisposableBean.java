package com.csu.springframework.beans.factory;

public interface DisposableBean {
    void destroy() throws Exception;
}
