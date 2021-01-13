package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 22/03/2017.
 */
public class Issue1645 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "explain extended select * from foo";
        String formatedSql = SQLUtils.format(sql, JdbcConstants.MYSQL);
        assertEquals("EXPLAIN EXTENDED SELECT *\n" +
                "FROM foo", formatedSql);
    }
}
