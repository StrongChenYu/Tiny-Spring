package com.csu.springframework.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 构建dataSource的factory
 * 工厂模式
 */
public interface DateSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
