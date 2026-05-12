package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagerUtilsTest_Limit_SQLServer_3 {
    @Test
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 where id > 1";
        String result = PagerUtils.limit(sql, JdbcConstants.SQL_SERVER, 0, 10);
        assertEquals("SELECT TOP 10 *"
                + "\nFROM t1"
                + "\nWHERE id > 1", result);
    }
}
