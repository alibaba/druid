package com.alibaba.druid.bvt.pool;

import java.lang.reflect.Field;
import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.util.JdbcConstants;

public class ValidationQueryLogDB2Test extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDbType(JdbcConstants.DB2);
        dataSource.setDriver(new MockDriver() {
            public int getMajorVersion() {
                return 10;
            }
        });
        dataSource.setValidationQuery("SELECT 'x'");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_oracle() throws Exception {
        Field field = DruidDataSource.class.getDeclaredField("LOG");
        field.setAccessible(true);
        Log log = (Log) field.get(null);
        log.resetStat();
        
        Connection conn = dataSource.getConnection();
        
        conn.close();
        
        
        Assert.assertEquals(1, log.getErrorCount());
    }
}
