package com.csu.springframework.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ClassUtils {

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader c1 = null;

        try {
            c1 = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {

        }

        if (c1 == null) {
            c1 = ClassUtils.class.getClassLoader();
        }

        return c1;
    }

    public static void main(String[] args) {
        ClassLoader classLoader = ClassUtils.class.getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("1234.txt");
        System.out.println(resourceAsStream);

    }
}
