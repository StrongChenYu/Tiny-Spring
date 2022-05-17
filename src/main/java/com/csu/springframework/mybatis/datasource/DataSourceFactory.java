package com.csu.springframework.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 构建dataSource的factory
 * 工厂模式
 *
 * 1.非池化数据源 每次请求的时候打开连接
 * 2.池化数据源 每次请求的时候关闭连接
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
