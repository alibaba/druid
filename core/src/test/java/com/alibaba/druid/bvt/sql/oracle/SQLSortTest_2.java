package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SQLSortTest_2 extends TestCase {
    public void test_sort() throws Exception {
        String sql = "CREATE SYNONYM offices \n" +
                "   FOR hr.locations;create table hr.locations(fid varchar(200));";

        String sortedSql = SQLUtils.sort(sql, JdbcConstants.ORACLE);
        assertEquals("CREATE TABLE hr.locations (\n" +
                "\tfid varchar(200)\n" +
                ");\n" +
                "CREATE SYNONYM offices FOR hr.locations;", sortedSql);
    }
}
