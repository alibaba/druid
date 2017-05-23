/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.spring.boot;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.junit.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lihengming [89921218@qq.com]
 */
public class DruidDataSourceTestCase {
    private static final String PREFIX = "spring.datasource.druid.";

    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();


    @Test
    public void testDataSourceExists() throws Exception {
        this.context.register(DruidDataSourceConfiguration.class);
        this.context.refresh();
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "url=jdbc:h2:file:./demo-db");
        assertThat(this.context.getBean(DataSource.class)).isNotNull();
        assertThat(this.context.getBean(DruidDataSource.class)).isNotNull();
    }

    @Test
    public void testDataSourcePropertiesOverridden() throws Exception {
        this.context.register(DruidDataSourceConfiguration.class);
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "url=jdbc:h2:file:./demo-db");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "filters=stat,log4j");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "maxActive=30");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "initialSize=2");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "maxWait=1234");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "minIdle=2");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "timeBetweenEvictionRunsMillis=10000");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "minEvictableIdleTimeMillis=12345");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "testWhileIdle=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "testOnBorrow=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "testOnReturn=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "poolPreparedStatements=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "maxOpenPreparedStatements=15");

        this.context.refresh();
        DruidDataSource ds = this.context.getBean(DruidDataSource.class);

        assertThat(ds.getUrl()).isEqualTo("jdbc:h2:file:./demo-db");
        assertThat(ds.getProxyFilters().size()).isEqualTo(2);
        assertThat(ds.getMaxActive()).isEqualTo(30);
        assertThat(ds.getInitialSize()).isEqualTo(2);
        assertThat(ds.getMaxWait()).isEqualTo(1234);
        assertThat(ds.getMinIdle()).isEqualTo(2);
        assertThat(ds.getTimeBetweenEvictionRunsMillis()).isEqualTo(10000);
        assertThat(ds.getMinEvictableIdleTimeMillis()).isEqualTo(12345);
        assertThat(ds.isTestWhileIdle()).isTrue();
        assertThat(ds.isTestOnBorrow()).isTrue();
        assertThat(ds.isTestOnReturn()).isTrue();
        assertThat(ds.isPoolPreparedStatements()).isTrue();
        assertThat(ds.getMaxOpenPreparedStatements()).isEqualTo(15);
    }


    @Configuration
    @EnableConfigurationProperties
    protected static class DruidDataSourceConfiguration {

        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.druid")
        public DataSource dataSource() {
            return DruidDataSourceBuilder.create().build();
        }

    }
}
