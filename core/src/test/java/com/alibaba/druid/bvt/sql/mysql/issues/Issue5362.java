package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import org.junit.Test;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5362>Issue来源</a>
 */
public class Issue5362 {
    @Test
    public void test_parse_asorder() {
        for (DbType dbType : DbType.values()) {
            for (String sql : new String[]{
                "select b.order + 1 as order from book b",
            }) {
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
