package com.csu.springframework.core.io;

public interface ResourceLoader {

    String CLASS_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
