package com.csu.springframework.mybatis.mapping;

import com.csu.springframework.mybatis.session.Configuration;

/**
 * select * from table
 * 像这样的一条语句对应一个MappedStatement
 */
public class MappedStatement {

    // 配置信息，咱也不知道这有什么用
    private Configuration configuration;
    //   mappedStatement的id应该是下面这个语句里面的id，是一个string类型
//   <select id="findAll" resultMap="RESULT_MAP">
//    select `user_id`, `name`, `sex`, `age`, `salary`, `borthday`, `face`
//    from `user`
//   </select>
    private String id;
    // 语句的类型
    private SqlCommandType sqlCommandType;
    // 这条语句是啥
    private BoundSql boundSql;

    private MappedStatement() {}

    /**
     * 使用建造者模式构造
     */
    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, BoundSql boundSql) {
            mappedStatement.id = id;
            mappedStatement.configuration = configuration;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.boundSql = boundSql;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }
}
