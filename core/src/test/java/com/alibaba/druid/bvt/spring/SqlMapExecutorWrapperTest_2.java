package com.alibaba.druid.bvt.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collections;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.spring.User;
import com.alibaba.druid.support.ibatis.SqlMapExecutorWrapper;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;

public class SqlMapExecutorWrapperTest_2 extends TestCase {
    private ClassPathXmlApplicationContext context = null;

    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");
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
    }

    protected void tearDown() throws Exception {
        DataSource dataSource = (DataSource) context.getBean("dataSource");
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
    }

    public void test_wrap() throws Exception {
        SqlMapClientImpl client = (SqlMapClientImpl) context.getBean("master-sqlMapClient");
        assertNotNull(client);

        SqlMapExecutorWrapper wrapper = new SqlMapExecutorWrapper(client, client);

        wrapper.insert("User.insert", new User(12345678, "aaa"));
        {
            Exception error = null;
            try {
                wrapper.insert("User.insert");
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }
        wrapper.update("User.update", new User(12345678, "bbb"));
        {
            Exception error = null;
            try {
                wrapper.update("User.update");
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }

        assertNotNull((User) wrapper.queryForObject("User.select"));
        assertNotNull((User) wrapper.queryForObject("User.select", Collections.emptyMap()));
        assertNotNull((User) wrapper.queryForObject("User.select", Collections.emptyMap(), new User()));

        assertEquals(1, wrapper.queryForList("User.select").size());
        assertEquals(1, wrapper.queryForList("User.select", Collections.emptyMap()).size());
        assertEquals(1, wrapper.queryForList("User.select", Collections.emptyMap(), 0, 2).size());

        wrapper.queryWithRowHandler("User.select", new RowHandler() {
            @Override
            public void handleRow(Object valueObject) {
            }
        });
        wrapper.queryWithRowHandler("User.select", Collections.emptyMap(), new RowHandler() {
            @Override
            public void handleRow(Object valueObject) {
            }
        });

        assertEquals(1, wrapper.queryForPaginatedList("User.select", 10).size());
        assertEquals(1, wrapper.queryForPaginatedList("User.select", Collections.emptyMap(), 10).size());

        assertNotNull(wrapper.queryForMap("User.select", Collections.emptyMap(), "id"));
        assertNotNull(wrapper.queryForMap("User.select", Collections.emptyMap(), "id", "name"));

        wrapper.delete("User.delete", 12345678L);
        {
            Exception error = null;
            try {
                wrapper.delete("User.delete");
            } catch (Exception ex) {
                error = ex;
            }
            assertNotNull(error);
        }

        wrapper.startBatch();
        wrapper.executeBatch();
        wrapper.executeBatchDetailed();
    }
}
