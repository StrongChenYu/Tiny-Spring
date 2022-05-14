package com.csu.springframework.mybatis.mapping;

/**
 * 用来枚举SQL语句的类型
 */
public enum SqlCommandType {
    UNKNOWN,

    INSERT,

    UPDATE,

    SELECT,

    DELETE,
}
