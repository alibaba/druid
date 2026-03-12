package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLSortTest_1 {
    @Test
    public void test_sort() throws Exception {
        String sql = "create view v1 as select * from v0; create view v0 as select * from t;";

        String sortedSql = SQLUtils.sort(sql, JdbcConstants.ORACLE);
        assertEquals("CREATE VIEW v0\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM t;\n" +
                "CREATE VIEW v1\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM v0;", sortedSql);
    }
}
