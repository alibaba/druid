package com.alibaba.druid.bvt.pool;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidConnectionHolderTest4 extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setPoolPreparedStatements(true);

    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_toString() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();

        DruidConnectionHolder holder = conn.getConnectionHolder();

        Field field = DruidConnectionHolder.class.getDeclaredField("statementPool");
        field.setAccessible(true);
        Assert.assertNull(field.get(holder));

        holder.toString();

        Assert.assertNull(field.get(holder));

        holder.getStatementPool();

        Assert.assertNotNull(field.get(holder));

        holder.toString();

        PreparedStatement stmt = conn.prepareStatement("select 1");
        stmt.execute();
        stmt.close();

        conn.close();
        
        Assert.assertEquals(1, holder.getStatementPool().size());
        
        holder.toString();
    }
}
