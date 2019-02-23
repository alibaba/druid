package com.alibaba.druid.spring.boot.demo.configurer;

import javax.sql.DataSource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/*@Configuration
@Profile("issue-1796")*/
public class Issue1796Configurer {

    @Bean
    public DataSource dataSource(Environment environment) {
        return DruidDataSourceBuilder
                .create()
                .build(environment, "spring.datasource.druid.");
    }
}
