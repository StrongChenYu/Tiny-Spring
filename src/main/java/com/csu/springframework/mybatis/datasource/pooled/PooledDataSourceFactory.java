package com.csu.springframework.mybatis.datasource.pooled;

import com.csu.springframework.mybatis.datasource.unpooled.UnPoolDataSourceFactory;

import javax.sql.DataSource;

public class PooledDataSourceFactory extends UnPoolDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUserName(properties.getProperty("username"));
        pooledDataSource.setPassword(properties.getProperty("password"));
        if (properties.getProperty("autocommit") != null) {
            pooledDataSource.setAutoCommit(Boolean.valueOf(properties.getProperty("autocommit")));
        }
        pooledDataSource.setClassLoader(Thread.currentThread().getContextClassLoader());
        pooledDataSource.setDiverProperties(properties);

        return pooledDataSource;
    }
}
