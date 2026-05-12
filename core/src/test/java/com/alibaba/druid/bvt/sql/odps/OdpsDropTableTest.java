package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 2016/11/18.
 */
public class OdpsDropTableTest {
    @Test
    public void test_drop_table() throws Exception {
        String sql = "DROP TABLE a PURGE ;";
        assertEquals("DROP TABLE a PURGE;", SQLUtils.formatOdps(sql));
    }
}
