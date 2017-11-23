package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 2016/11/18.
 */
public class OdpsDropTableTest extends TestCase {
    public void test_drop_table() throws Exception {
        String sql = "DROP TABLE a PURGE ;";
        assertEquals("DROP TABLE a PURGE;", SQLUtils.formatOdps(sql));
    }
}
