package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_DeleteTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_delete_basic() {
        String sql = "DELETE FROM t1 WHERE id = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_delete_with_alias() {
        String sql = "DELETE FROM t1 a WHERE a.status = 0";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_delete_with_subquery() {
        String sql = "DELETE FROM t1 WHERE id IN (SELECT aid FROM t2 WHERE status = 0)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }
}
