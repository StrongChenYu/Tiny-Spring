package com.csu.springframework.beans.factory;

/**
 * 这个disposable的含义是什么？
 * 一次性？
 * 还是可自由处理的？
 * 因为有destroy方法的存在
 * 我偏向与一次性处理
 */
public interface DisposableBean {
    void destroy() throws Exception;
}
