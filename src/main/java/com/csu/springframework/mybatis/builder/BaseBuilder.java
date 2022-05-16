package com.csu.springframework.mybatis.builder;

import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.type.TypeAliasRegistry;

public class BaseBuilder {

    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;


    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }
}
