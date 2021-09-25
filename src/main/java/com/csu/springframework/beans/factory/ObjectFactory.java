package com.csu.springframework.beans.factory;

import com.csu.springframework.beans.BeansException;

public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}
