package com.csu.springframework.mybatis.mapping;

import java.util.Map;

public class BoundSql {

    // 这条语句的原始类型
    private String sql;
    // 我猜是sql语句中的占位符中的每一个类型，这个占位符就是要传入的参数
    private Map<Integer, String> parameterMappings;
    // 这个不不知道，因为不可能只有一个类型
    private String parameterType;
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }
}
