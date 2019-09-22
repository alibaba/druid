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
package com.alibaba.druid.benckmark.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Case0 extends TestCase {

    private String   jdbcUrl;
    private String   user;
    private String   password;
    private String   driverClass;
    private int      initialSize                = 1;
    private int      minIdle                    = 3;
    private int      maxIdle                    = 8;
    private int      maxActive                  = 8;
    private String   validationQuery            = "SELECT 1";
    private boolean  testOnBorrow               = false;

    private long     minEvictableIdleTimeMillis = 3000;
    public final int LOOP_COUNT                 = 5;
    public final int COUNT                      = 1000 * 1000 * 1;

    protected void setUp() throws Exception {
         jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
         user = "dragoon25";
         password = "dragoon25";
         driverClass = "com.alibaba.druid.mock.MockDriver";

//        jdbcUrl = "jdbc:mysql://a.b.c.d:3306/masterdb";
//        user = "x";
//        password = "x";
    }

    public void test_druid() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        for (int i = 0; i < LOOP_COUNT; ++i) {
            p0(dataSource, "druid");
        }
        System.out.println();
    }

    public void f_test_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        for (int i = 0; i < LOOP_COUNT; ++i) {
            p0(dataSource, "dbcp");
        }
        System.out.println();
    }

    public void f_test_2() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minIdle);
        dataSource.setMaxConnectionsPerPartition(maxIdle);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setConnectionTestStatement(validationQuery);
        dataSource.setPartitionCount(1);

        for (int i = 0; i < LOOP_COUNT; ++i) {
            p0(dataSource, "boneCP");
        }
        System.out.println();
    }
    
    public void f_test_c3p0() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinPoolSize(minIdle);
        dataSource.setMaxPoolSize(maxIdle);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        for (int i = 0; i < LOOP_COUNT; ++i) {
            p0(dataSource, "c3p0");
        }
        System.out.println();
    }

    public void f_test_tomcat_jdbc() throws Exception {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxIdle);
        
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        
        for (int i = 0; i < LOOP_COUNT; ++i) {
            p0(dataSource, "tomcat-jdbc");
        }
        System.out.println();
    }

    private void p0(DataSource dataSource, String name) throws SQLException {
        long startMillis = System.currentTimeMillis();
        long startYGC = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();

        for (int i = 0; i < COUNT; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT 1");
//            rs.close();
//            stmt.close();
            conn.close();
        }
        long millis = System.currentTimeMillis() - startMillis;
        long ygc = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println(name + " millis : " + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC "
                           + fullGC);
    }
}
