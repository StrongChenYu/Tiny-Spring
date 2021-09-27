package com.csu.springframework.core.io.convert;

public interface Converter<S, T> {
    T convert(S source);
}
