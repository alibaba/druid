package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 2016/11/18.
 */
public class OdpsInsertTest3 extends TestCase {
    public void test_for_insert_select_limit() throws Exception {
        String sql = "insert overwrite table ff partition (c='c',d='d') select /*+mapjoin(tt)*/ id,name from tt";
        assertEquals("INSERT OVERWRITE TABLE ff PARTITION (c='c', d='d')\n" +
                "SELECT /*+mapjoin(tt)*/ id, name\n" +
                "FROM tt", SQLUtils.formatOdps(sql));
    }
}
