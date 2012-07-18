package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.spring.User;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class SpringMybatisFilterTest extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
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
    }
}
