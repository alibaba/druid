package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DruidDataSourceTest10 {
    @Test
    public void test() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setSocketTimeout(10);
        ds.setConnectTimeout(20);

        DruidDataSource ds1 = (DruidDataSource) ds.clone();
        assertEquals(ds.getConnectTimeout(), ds1.getConnectTimeout());
        assertEquals(ds.getSocketTimeout(), ds1.getSocketTimeout());
    }
}
