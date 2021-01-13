package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author shicai.xsc 2018/9/13 下午3:35
 * @desc
 * @since 5.0.0.0
 */
public class MySqlAlterTableTest49 extends TestCase {
    public void test_0() throws Exception {
        String sql = "alter table a add column auto_id int not null auto_increment by group primary key;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, stmtList.size());
        SQLStatement stmt = stmtList.get(0);
        String output = stmt.toString();
        assertEquals("ALTER TABLE a\n" +
                "\tADD COLUMN auto_id int NOT NULL PRIMARY KEY AUTO_INCREMENT BY GROUP;", output);
    }

}
