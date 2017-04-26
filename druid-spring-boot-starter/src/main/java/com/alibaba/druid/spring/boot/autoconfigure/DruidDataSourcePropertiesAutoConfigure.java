package com.alibaba.druid.spring.boot.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author lihengming<89921218@qq.com>
 */
@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class)
@Import({DruidSpringAopConfiguration.class, DruidStatViewServletConfiguration.class, DruidStatFilterConfiguration.class})
public class DruidDataSourcePropertiesAutoConfigure {
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource(DruidProperties properties) {
        //base datasource config,use spring datasource autoconfig.
        DruidDataSource datasource = (DruidDataSource) DataSourceBuilder
                .create()
                .type(DruidDataSource.class)
                .build();
        //druid config.
        configDruid(datasource, properties);
        return datasource;
    }

    private void configDruid(DruidDataSource datasource, DruidProperties properties) {
        if (properties.getMaxActive() != null) {
            datasource.setMaxActive(properties.getMaxActive());
        }
        if (properties.getInitialSize() != null) {
            datasource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMaxWait() != null) {
            datasource.setMaxWait(properties.getMaxWait());
        }
        if (properties.getMinIdle() != null) {
            datasource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getTimeBetweenEvictionRunsMillis() != null) {
            datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        }
        if (properties.getMinEvictableIdleTimeMillis() != null) {
            datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        }
        if (properties.getTestWhileIdle() != null) {
            datasource.setTestWhileIdle(properties.getTestWhileIdle());
        }
        if (properties.getTestOnBorrow() != null) {
            datasource.setTestOnBorrow(properties.getTestOnBorrow());
        }
        if (properties.getTestOnReturn() != null) {
            datasource.setTestOnReturn(properties.getTestOnReturn());
        }
        if (properties.getPoolPreparedStatements() != null) {
            datasource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        }
        if (properties.getMaxPoolPreparedStatementPerConnectionSize() != null) {
            datasource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        try {
            datasource.setFilters(properties.getFilters() != null ? properties.getFilters() : "stat");
        } catch (SQLException e) {
            throw new IllegalArgumentException("please check your spring.datasource.druid.filters property.", e);
        }
    }
}
