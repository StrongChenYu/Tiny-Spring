package com.csu.springframework.context.support;

import com.csu.springframework.beans.factory.FactoryBean;
import com.csu.springframework.beans.factory.InitializingBean;
import com.csu.springframework.core.io.convert.*;
import com.csu.springframework.core.io.convert.support.DefaultConversionService;
import com.csu.springframework.core.io.convert.support.GenericConversionService;

import javax.annotation.Nullable;
import java.util.Set;

public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {

    @Nullable
    private Set<?> converters;

    @Nullable
    private GenericConversionService conversionService;

    @Override
    public ConversionService getObject() throws Exception {
        return conversionService;
    }

    @Override
    public Class<?> getObjectType() {
        return conversionService.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.conversionService = new DefaultConversionService();
        registerConverters(converters, conversionService);
    }

    private void registerConverters(Set<?> converters, ConverterRegistry registry) {
        if (converters == null) {
            return;
        }

        for (Object converter : converters) {
            if (converter instanceof GenericConverter) {
                registry.addConverter((GenericConverter) converter);
            } else if (converter instanceof Converter<?,?>) {
                registry.addConverter((Converter<?, ?>) converter);
            } else if (converter instanceof ConverterFactory<?, ?>) {
                registry.addConverterFactory((ConverterFactory<?, ?>) converter);
            } else {
                throw new IllegalArgumentException("Each converter object must implement one of the " + "Converter, ConverterFactory, or GenericConverter inter faces");
            }
        }
    }

    public void setConverters(@Nullable Set<?> converters) {
        this.converters = converters;
    }
}
