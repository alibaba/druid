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
package com.alibaba.druid.spring.boot.testcase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "initial-size=2");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "max-active=30");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "min-idle=2");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "max-wait=1234");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "pool-prepared-statements=true");
        //EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "max-open-prepared-statements=5");//Duplicated with following
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "max-pool-prepared-statement-per-connection-size=5");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "validation-query=select 'x'");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "validation-query-timeout=1");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "test-on-borrow=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "test-while-idle=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "test-on-return=true");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "time-between-eviction-runs-millis=10000");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "min-evictable-idle-time-millis=12345");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "connection-properties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000");
        EnvironmentTestUtils.addEnvironment(this.context, PREFIX + "async-close-connection-enable=true");

        this.context.refresh();
        DruidDataSource ds = this.context.getBean(DruidDataSource.class);

        assertThat(ds.getUrl()).isEqualTo("jdbc:h2:file:./demo-db");
        assertThat(ds.getInitialSize()).isEqualTo(2);
        assertThat(ds.getMaxActive()).isEqualTo(30);
        assertThat(ds.getMinIdle()).isEqualTo(2);
        assertThat(ds.getMaxWait()).isEqualTo(1234);
        assertThat(ds.isPoolPreparedStatements()).isTrue();
        //assertThat(ds.getMaxOpenPreparedStatements()).isEqualTo(5); //Duplicated with following
        assertThat(ds.getMaxPoolPreparedStatementPerConnectionSize()).isEqualTo(5);
        assertThat(ds.getValidationQuery()).isEqualTo("select 'x'");
        assertThat(ds.getValidationQueryTimeout()).isEqualTo(1);
        assertThat(ds.isTestWhileIdle()).isTrue();
        assertThat(ds.isTestOnBorrow()).isTrue();
        assertThat(ds.isTestOnReturn()).isTrue();
        assertThat(ds.getTimeBetweenEvictionRunsMillis()).isEqualTo(10000);
        assertThat(ds.getMinEvictableIdleTimeMillis()).isEqualTo(12345);
        assertThat(ds.getConnectProperties().size()).isEqualTo(2);
        assertThat(ds.isAsyncCloseConnectionEnable()).isEqualTo(true);
    }


    @Configuration
    @Import(DruidDataSourceAutoConfigure.class)
    protected static class DruidDataSourceConfiguration {


    }
}
