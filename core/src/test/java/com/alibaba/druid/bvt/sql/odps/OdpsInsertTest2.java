package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 2016/11/18.
 */
public class OdpsInsertTest2 extends TestCase {
    public void test_for_insert_select_limit() throws Exception {
        String sql = "insert into table a select * from b limit 1";
        assertEquals("INSERT INTO TABLE a\n" +
                "SELECT *\n" +
                "FROM b\n" +
                "LIMIT 1", SQLUtils.formatOdps(sql));
    }
}
