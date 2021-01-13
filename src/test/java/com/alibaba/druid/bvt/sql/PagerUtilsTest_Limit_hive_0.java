package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class PagerUtilsTest_Limit_hive_0 extends TestCase {

    public void test_hive_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.hive, 0, 10);
        System.out.println(result);
        Assert.assertEquals("SELECT *\n" +
                "FROM test\n" +
                "LIMIT 10", result);
    }

    public void test_odps_0() throws Exception {
        String result = PagerUtils.limit("SELECT * FROM test", DbType.odps, 0, 10);
        System.out.println(result);
        Assert.assertEquals("SELECT *\n" +
                "FROM test\n" +
                "LIMIT 10", result);
    }
}