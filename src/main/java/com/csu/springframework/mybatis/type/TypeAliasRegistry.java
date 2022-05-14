package com.csu.springframework.mybatis.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 每一个类型都有一个别名
 * 比如
 * int->Integer.class
 * short->Short.class
 */
public class TypeAliasRegistry {

    private Map<String, Class<?>> ALIAS_MAPPING = new HashMap<>();

    public TypeAliasRegistry() {
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("short", Short.class);
        registerAlias("byte", Byte.class);
        registerAlias("boolean", Boolean.class);
        registerAlias("long", Long.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("char", Character.class);
        registerAlias("string", String.class);
    }

    public void registerAlias(String alias, Class<?> value) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        ALIAS_MAPPING.put(key, value);
    }

    public <T> Class<T> resolveAlias(String alias) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        return (Class<T>) ALIAS_MAPPING.get(key);
    }
}
