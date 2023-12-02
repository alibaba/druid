package com.alibaba.druid.spring.boot3.demo.configurer;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

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
