package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.lang.reflect.Field;
import java.sql.PreparedStatement;


import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class LastActiveTest_0 extends TestCase {
    private DruidDataSource dataSource;
    private Field field;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setMinIdle(0);
        dataSource.setMaxActive(100);
        dataSource.init();

        field = DruidPooledConnection.class.getDeclaredField("holder");
        field.setAccessible(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {
        long t0, t1;
        {
            DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
            t0 = getLastActiveTime(conn);
            PreparedStatement stmt = conn.prepareStatement("select 1");
            Thread.sleep(2);
            stmt.execute();

            stmt.close();
            conn.close();
        }
        Thread.sleep(1000);
        {
            DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
            t1 = getLastActiveTime(conn);
            PreparedStatement stmt = conn.prepareStatement("select 1");
            Thread.sleep(2);
            stmt.execute();

            stmt.close();
            conn.close();
        }
        assertNotEquals(t0, t1);
    }

    private long getLastActiveTime(DruidPooledConnection conn) throws IllegalAccessException {
        DruidConnectionHolder holder = (DruidConnectionHolder) field.get(conn);
        return holder.getLastActiveTimeMillis();
    }

}
