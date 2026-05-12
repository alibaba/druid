package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class LockFairTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_fair() throws Exception {
        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        dataSource.setMaxWait(100);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        dataSource.setMaxWait(110);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        dataSource.setMaxWait(0);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
    }

    @Test
    public void test_fair_1() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
        dataSource.setMaxWait(100);

        assertEquals(false, ((ReentrantLock) dataSource.getLock()).isFair());
    }
}
