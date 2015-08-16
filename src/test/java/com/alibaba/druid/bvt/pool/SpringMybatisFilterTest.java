/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import java.sql.Statement;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.spring.User;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class SpringMybatisFilterTest extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_spring() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                                                                                    "com/alibaba/druid/pool/mybatis/spring-config-mybatis.xml");

        DataSource dataSource = (DataSource) context.getBean("dataSource");

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE sequence_seed (value INTEGER, name VARCHAR(50))");
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE t_User (id BIGINT, name VARCHAR(50))");
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

        UserMapper userMapper = (UserMapper) context.getBean("userMapper");

        {
            User user = new User();
            user.setName("xx");

            userMapper.addUser(user);
        }
        
        {
            userMapper.errorSelect(1);
        }
        
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

        context.close();

        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public static interface UserMapper {

        @Insert(value = "insert into t_User (id, name) values (#{user.id}, #{user.name})")
        void addUser(@Param("user") User user);
        
        @Select(value = "delete from t_User where id = #{id}")
        void errorSelect(@Param("id") long id);
    }
}
