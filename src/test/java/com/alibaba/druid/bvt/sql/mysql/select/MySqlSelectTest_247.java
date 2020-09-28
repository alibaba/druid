package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

/**
 * @author Dagon0577
 * @date 2019/11/7 10:50
 */
public class MySqlSelectTest_247 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT SysNo,MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH(Name)AGAINST('张三' WITH QUERY EXPANSION)";

        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils
                .parseSingleStatement(sql, JdbcConstants.MYSQL);

        assertEquals("SELECT SysNo, MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION) AS score\n"
                + "FROM `test_table`\n" + "WHERE MATCH (Name) AGAINST ('张三' WITH QUERY EXPANSION)", stmt.toString());
    }
}
