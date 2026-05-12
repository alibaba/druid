package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupingSetsTest {
    @Test
    public void test_groupingSets() throws Exception {
        String sql = "SELECT brand, size, sum(sales) FROM items_sold GROUP BY GROUPING SETS ((brand), (size), ());";

        String result = SQLUtils.format(sql, (DbType) null);

        assertEquals("SELECT brand, size, sum(sales)"
                + "\nFROM items_sold"
                + "\nGROUP BY GROUPING SETS ((brand), (size), ());", result);
    }
}
