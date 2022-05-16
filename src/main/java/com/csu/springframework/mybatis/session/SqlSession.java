package com.csu.springframework.mybatis.session;

public interface SqlSession {
    /**
     * 这个就是单独的一条sql语句，没有任何参数
     * @param statement 执行的sql语句
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement);

    /**
     * 这个函数就是一个sql语句中，有几个参数，这几个参数的值就是parameter
     * @param statement sql语句
     * @param parameter 参数
     * @param <T> 查询结果
     * @return
     */
    <T> T selectOne(String statement, Object parameter);


    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();
}
