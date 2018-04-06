/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import java.sql.SQLException;

import javax.annotation.Resource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.demo.DemoApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lihengming [89921218@qq.com]
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("multi-datasource")
public class DruidMultiDataSourceTestCase {

    @Resource
    private DruidDataSource dataSourceOne;
    @Resource
    private DruidDataSource dataSourceTwo;

    @Test
    public void testDataSourceOne() throws SQLException {

        assertThat(dataSourceOne.getUrl()).isEqualTo("jdbc:h2:file:./demo-db");
        assertThat(dataSourceOne.getUsername()).isEqualTo("sa");
        assertThat(dataSourceOne.getPassword()).isEqualTo("sa");
        assertThat(dataSourceOne.getDriverClassName()).isEqualTo("org.h2.Driver");

        assertThat(dataSourceOne.getInitialSize()).isEqualTo(5);

        assertThat(dataSourceOne.getMaxActive()).isEqualTo(10);
        assertThat(dataSourceOne.getMaxWait()).isEqualTo(10000);
    }
    @Test
    public void testDataSourceTwo() throws SQLException {

        assertThat(dataSourceTwo.getUrl()).isEqualTo("jdbc:h2:file:./demo-db");
        assertThat(dataSourceTwo.getUsername()).isEqualTo("sa");
        assertThat(dataSourceTwo.getPassword()).isEqualTo("sa");
        assertThat(dataSourceTwo.getDriverClassName()).isEqualTo("org.h2.Driver");

        assertThat(dataSourceTwo.getInitialSize()).isEqualTo(5);

        assertThat(dataSourceTwo.getMaxActive()).isEqualTo(20);
        assertThat(dataSourceTwo.getMaxWait()).isEqualTo(20000);
    }

}
