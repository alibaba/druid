package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

/**
 * @author shicai.xsc 2018/9/13 下午3:35
 * @desc
 * @since 5.0.0.0
 */
public class MySqlAlterTableTest53 extends TestCase {
    public void test_0() throws Exception {
        String sql = "ALTER TABLE t_str_spare  CONVERT TO CHARACTER SET UTF8 COLLATE utf8_bin";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        String formatSql = SQLUtils.toSQLString(stmt);
        assertEquals("ALTER TABLE t_str_spare\n" +
                "\tCONVERT TO CHARACTER SET UTF8 COLLATE utf8_bin", formatSql);
    }
}
