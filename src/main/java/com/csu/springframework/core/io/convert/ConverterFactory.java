package com.csu.springframework.core.io.convert;

public interface ConverterFactory<S,R> {
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
