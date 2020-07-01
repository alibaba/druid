package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class PagerUtilsTest_Limit_SQLServer_6 extends TestCase {

    public void test_db2_union() throws Exception {
        String sql = "SELECT t.name USER_NAME, t.xxx FROM t_sd_users t ORDER BY t.name ASC";
        String result = PagerUtils.limit(sql, JdbcConstants.SQL_SERVER, 10, 10);
        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT t.name AS USER_NAME, t.xxx, ROW_NUMBER() OVER (ORDER BY t.name ASC) AS ROWNUM\n" +
                "\tFROM t_sd_users t\n" +
                ") XX\n" +
                "WHERE ROWNUM > 10\n" +
                "\tAND ROWNUM <= 20", result);
    }
}
