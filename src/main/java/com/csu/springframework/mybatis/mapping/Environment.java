package com.csu.springframework.mybatis.mapping;

import com.csu.springframework.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * 怎么配置Environment
 * 这个类其实就是去映射Environment配置信息
 * <environments default="development">
 *   <environment id="development">
 *     <transactionManager type="JDBC">
 *       <property name="..." value="..."/>
 *     </transactionManager>
 *     <dataSource type="POOLED">
 *       <property name="driver" value="${driver}"/>
 *       <property name="url" value="${url}"/>
 *       <property name="username" value="${username}"/>
 *       <property name="password" value="${password}"/>
 *     </dataSource>
 *   </environment>
 * </environments>
 */
public final class Environment {
    private final String id;
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 建造者模式
     */
    public static class Builder {
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Environment build() {
            return new Environment(id, transactionFactory, dataSource);
        }

    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
