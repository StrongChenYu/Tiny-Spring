package com.csu.springframework.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.csu.springframework.mybatis.datasource.DateSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 阿里巴巴的数据库连接池
 */
public class DruidDataSourceFactory implements DateSourceFactory {

    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        // 这个数据库连接池使用到的driver
        dataSource.setDriverClassName(properties.getProperty("driver"));
        // 这个数据库的目的地
        dataSource.setUrl(properties.getProperty("url"));
        // 这个数据库的用户名
        dataSource.setUsername(properties.getProperty("username"));
        // 这个数据库用户名对应的密码
        dataSource.setUsername(properties.getProperty("password"));
        return dataSource;
    }
}
