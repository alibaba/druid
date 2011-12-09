package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HADataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class HADataSourceTest2 extends TestCase {

    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    private HADataSource    dataSourceHA;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        
        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:ha1");
        dataSourceA.setFilters("trace");

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:ha2");
        dataSourceB.setFilters("stat");

        dataSourceHA = new HADataSource();
        dataSourceHA.setMaster(dataSourceA);
        dataSourceHA.setSlave(dataSourceB);
    }

    protected void tearDown() throws Exception {
        dataSourceHA.close();
        
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }
        
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

   public void test_switch() throws Exception {
       Connection conn = dataSourceHA.getConnection();
       
       MockConnection mockConn = conn.unwrap(MockConnection.class);
       conn.close();
   }
}
