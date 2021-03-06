package com.csu.springframework.beans.factory;

import com.csu.springframework.beans.BeansException;
import com.csu.springframework.beans.PropertyValue;
import com.csu.springframework.beans.PropertyValues;
import com.csu.springframework.beans.factory.config.BeanDefinition;
import com.csu.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csu.springframework.core.io.DefaultResourceLoader;
import com.csu.springframework.core.io.Resource;
import com.csu.springframework.util.StringValueResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private String location;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        try {
            Properties propertiesFiles = new Properties();
            propertiesFiles.load(resource.getInputStream());

            // 先把所有的definition都get出来
            String[] beanDefinitionNames = factory.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = factory.getBeanDefinition(beanDefinitionName);

                // 然后到definition中寻找是String并且，为${}这样子配置的String
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) {
                        continue;
                    }

                    String actualValue = resolvePlaceHolder((String) value, propertiesFiles);
                    propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), actualValue));

                }
            }

            StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(propertiesFiles);
            factory.addEmbeddedValueResolver(valueResolver);

        } catch (IOException e) {
            throw new BeansException("Could not load properties", e);
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String resolvePlaceHolder(String value, Properties properties) {
        StringBuilder valueBuffer = new StringBuilder(value);
        int prefixIdx = valueBuffer.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int suffixIdx = valueBuffer.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);

        if (prefixIdx != -1 && suffixIdx != -1 && prefixIdx < suffixIdx) {
            String fileKey = valueBuffer.substring(prefixIdx + 2, suffixIdx);
            String actualProperty = properties.getProperty(fileKey);
            valueBuffer.replace(prefixIdx, suffixIdx + 1, actualProperty);
        }

        return valueBuffer.toString();
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceHolder(strVal, properties);
        }
    }
}
