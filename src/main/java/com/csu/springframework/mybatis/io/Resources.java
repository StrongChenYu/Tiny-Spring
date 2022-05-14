package com.csu.springframework.mybatis.io;

import java.io.*;

public class Resources {

    /**
     * 将resource作为一个Reader返回
     * 有了reader就可以读取里面的内容
     * 通过读取到缓冲区或者一个一个字节读取的方式
     * @param resource string type of resource
     * @return java.io.Reader
     * @throws IOException
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader[] loaders = getClassLoaders();
        for (ClassLoader loader : loaders) {
            // 每一个classLoader都有自己的作用范围，去他们的每一个地方查找这个文件，如果找到的话会返回这个流
            // 如果没有找到的话会返回一个null
            InputStream resourceAsStream = loader.getResourceAsStream(resource);
            if (resourceAsStream != null) {
                return resourceAsStream;
            }
        }
        throw new RuntimeException("Could not find resource: " + resource);
    }

    /**
     * @return 作用域内所有的classLoader
     */
    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader()};
    }

    /**
     * 加载一个类
     * @param className 类名
     * @return 这个类
     * @throws ClassNotFoundException 找不到类抛出的异常
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }


    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader("H:/bug.txt");
        int a = 0;
        while ((a = reader.read()) != -1) {
            char b = (char) a;
            System.out.println(b);
        }
    }
}
