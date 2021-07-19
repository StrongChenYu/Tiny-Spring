package com.csu.springframework.core.io;

import cn.hutool.core.lang.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ResourceLoader {


    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");

        if (location.startsWith(CLASS_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASS_URL_PREFIX.length()));
        }

        try {
            return new UrlResource(new URL(location));
        } catch (MalformedURLException e) {
            return new FileSystemResource(location);
        }
    }
}
