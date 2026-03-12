package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsAlterViewTest {
    @Test
    public void test_if() throws Exception {
        String sql = "alter view view_name rename to new_view_name;";
        assertEquals("ALTER VIEW view_name RENAME TO new_view_name;", SQLUtils.formatOdps(sql));
    }
}
