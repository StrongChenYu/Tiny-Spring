package com.csu.springframework.mybatis.session;

import com.csu.springframework.mybatis.builder.xml.XMLConfigBuilder;
import com.csu.springframework.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder builder = new XMLConfigBuilder(reader);
        return new DefaultSqlSessionFactory(builder.parse());
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
