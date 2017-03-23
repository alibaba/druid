package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 23/03/2017.
 */
public class Issue1654 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "ALTER TABLE db_manage.zcy_gpcatalog_node_t ADD INDEX index_code USING BTREE (code);";
        String formatedSql = SQLUtils.format(sql, JdbcConstants.MYSQL);
        assertEquals("ALTER TABLE db_manage.zcy_gpcatalog_node_t\n" +
                "\tADD INDEX index_code USING BTREE (code);", formatedSql);
    }
}
