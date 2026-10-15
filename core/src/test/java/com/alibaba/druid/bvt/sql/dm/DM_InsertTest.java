package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_InsertTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_insert_values() {
        String sql = "INSERT INTO t1 (id, name, status) VALUES (1, 'test', 1)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_insert_multi_values() {
        String sql = "INSERT INTO t1 (id, name) VALUES (1, 'a'), (2, 'b'), (3, 'c')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_insert_select() {
        String sql = "INSERT INTO t1 (id, name) SELECT id, name FROM t2 WHERE status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_insert_with_alias() {
        String sql = "INSERT INTO t1 a (id, name) VALUES (1, 'test')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
