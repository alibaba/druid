package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_UpdateTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_update_basic() {
        String sql = "UPDATE t1 SET name = 'test', status = 1 WHERE id = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_update_with_subquery() {
        String sql = "UPDATE t1 SET name = (SELECT name FROM t2 WHERE t2.id = t1.aid) WHERE status = 0";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_update_with_join() {
        String sql = "UPDATE t1 a SET a.name = b.name FROM t2 b WHERE a.id = b.aid";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
