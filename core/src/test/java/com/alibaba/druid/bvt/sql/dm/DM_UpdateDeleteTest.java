package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class DM_UpdateDeleteTest extends TestCase {
    public void test_simple_update() throws Exception {
        String sql = "UPDATE users SET name = '王五', updated_at = SYSDATE WHERE id = 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_update_with_subquery() throws Exception {
        String sql = "UPDATE users SET status = 'inactive' WHERE id IN (SELECT user_id FROM inactive_users)";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_simple_delete() throws Exception {
        String sql = "DELETE FROM users WHERE id = 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_delete_with_condition() throws Exception {
        String sql = "DELETE FROM users WHERE status = 'deleted' AND updated_at < SYSDATE - 30";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_delete_all() throws Exception {
        String sql = "DELETE FROM temp_table";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
