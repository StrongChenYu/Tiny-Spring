package com.csu.springframework.mybatis.session;

import com.csu.springframework.mybatis.binding.MapperRegistry;
import com.csu.springframework.mybatis.datasource.druid.DruidDataSourceFactory;
import com.csu.springframework.mybatis.mapping.Environment;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.csu.springframework.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 把配置文件中的所有信息映射到这个类里面
 * 包括
 * 1. environment配置
 * 2. mapper配置
 * ....
 */
public class Configuration {

    protected Environment environment;

    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

//   mappedStatement的id应该是下面这个语句里面的id，是一个string类型
//   <select id="findAll" resultMap="RESULT_MAP">
//    select `user_id`, `name`, `sex`, `age`, `salary`, `borthday`, `face`
//    from `user`
//   </select>
    // 这个mapperStatement单纯的只是映射
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    private final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        //添加几个别名
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
    }

    // 下面四个是委托方法，有关UserDao之类的类
    public <T> void addMapper(Class<T> clazz) {
        mapperRegistry.addMapper(clazz);
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> T getMappers(Class<T> clazz, SqlSession sqlSession) {
        return mapperRegistry.getMapper(clazz, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    // 下面有关mappedStatement的方法
    public void addMappedStatement(MappedStatement statement) {
        mappedStatements.put(statement.getId(), statement);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    // get typeAliasRegistry
    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    // get environment
    public Environment getEnvironment() {
        return environment;
    }

    // set environment
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
