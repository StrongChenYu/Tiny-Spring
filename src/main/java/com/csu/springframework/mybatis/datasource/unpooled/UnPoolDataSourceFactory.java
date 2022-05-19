package com.csu.springframework.mybatis.datasource.unpooled;

import com.csu.springframework.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class UnPoolDataSourceFactory implements DataSourceFactory {

    protected Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        UnPooledDataSource unPooledDataSource = new UnPooledDataSource();
        unPooledDataSource.setDriver(properties.getProperty("driver"));
        unPooledDataSource.setUrl(properties.getProperty("url"));
        unPooledDataSource.setUserName(properties.getProperty("username"));
        unPooledDataSource.setPassword(properties.getProperty("password"));
        if (properties.getProperty("autocommit") != null) {
            unPooledDataSource.setAutoCommit(Boolean.valueOf(properties.getProperty("autocommit")));
        }
        unPooledDataSource.setClassLoader(Thread.currentThread().getContextClassLoader());
        unPooledDataSource.setDiverProperties(properties);
        return unPooledDataSource;
    }
}
