package com.csu.springframework.core.io.convert.support;

import com.csu.springframework.core.io.convert.Converter;
import com.csu.springframework.core.io.convert.ConverterFactory;
import com.csu.springframework.util.NumberUtils;

public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<>(targetType);
    }

    public static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;
        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }
}
