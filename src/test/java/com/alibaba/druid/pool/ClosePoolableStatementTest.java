package com.alibaba.druid.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试开启PSCache后关闭DruidPooledPreparedStatement时的情况
 *
 * @author DigitalSonic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/alibaba/druid/pool/dataSource.xml")
public class ClosePoolableStatementTest {
    @Autowired
    private DruidDataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(5);
        dataSource.init();

        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("create table test (id int, val varchar(32), primary key (id))");
    }

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.execute("drop table test");
    }

    /**
     * 关注抛出异常后能否正确将PreparedStatement移出缓存，能正常执行后续SQL
     */
    @Test
    public void testClose() throws Exception {
        insertData(1, "a");
        try {
            insertData(1, "a");
        } catch (Exception e) {
            assertTrue(e instanceof DuplicateKeyException);
        }
        try {
            insertData(1, "a");
        } catch (Exception e) {
            assertTrue(e instanceof DuplicateKeyException);
            assertEquals(-1, e.getMessage().indexOf("closed"));
        }
    }

    private void insertData(final int id, final String val) {
        jdbcTemplate.update("insert into test (id, val) values (?, ?)", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, id);
                ps.setString(2, val);
            }
        });
    }
}
