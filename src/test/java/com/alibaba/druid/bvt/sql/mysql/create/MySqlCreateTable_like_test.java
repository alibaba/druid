package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableLike;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTable_like_test extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE like_test (LIKE t1)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        SQLExprTableSource like = stmt.getLike();
        assertTrue(stmt.getTableElementList().size() == 1);
        assertTrue(like == null);
        assertTrue(stmt.getTableElementList().get(0) instanceof SQLTableLike);
        assertEquals("CREATE TABLE like_test (\n" +
                "\tLIKE t1\n" +
                ")", stmt.toString());
    }


    public void test_1() throws Exception {
        String sql = "CREATE TABLE like_test (`LIKE` t1)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertTrue(stmt.getLike() == null);

        assertEquals("CREATE TABLE like_test (\n" +
                "\t`LIKE` t1\n" +
                ")", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "CREATE TABLE like_test LIKE t1";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);

        assertEquals(1, statementList.size());

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertTrue(stmt.getLike() != null);

        assertEquals("CREATE TABLE like_test LIKE t1", stmt.toString());
    }
}
