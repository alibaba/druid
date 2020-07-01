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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.spring.IUserService;
import com.alibaba.druid.spring.User;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;

public class SpringIbatisFilterTest extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_spring() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                                                                                    "com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");

        DataSource dataSource = (DataSource) context.getBean("dataSource");

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE sequence_seed (value INTEGER, name VARCHAR(50) PRIMARY KEY)");
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE t_User (id BIGINT PRIMARY KEY, name VARCHAR(50))");
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.execute("insert into sequence_seed (value ,name) values (0, 'druid-spring-test')");
            stmt.close();
            conn.commit();
            conn.close();
        }

        // user-service
        IUserService service = (IUserService) context.getBean("user-service");
        User user = new User();
        user.setName("xx");
        service.addUser(user);

        TestFilter filter = (TestFilter) context.getBean("test-filter");
        Assert.assertEquals(2, filter.getConnectCount());

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE sequence_seed");
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE t_User");
            stmt.close();
            conn.close();
        }

        Assert.assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        
        Map<String, Object> wallStats = DruidStatService.getInstance().getWallStatMap(Collections.<String, String>emptyMap());
        
        System.out.println("wall-stats : " + JSONUtils.toJSONString(wallStats));
        
        context.close();

        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public static class TestFilter extends FilterAdapter {

        private AtomicLong connectCount = new AtomicLong();

        @Override
        public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
            connectCount.incrementAndGet();
            return chain.connection_connect(info);
        }

        public long getConnectCount() {
            return connectCount.get();
        }
    }
}
