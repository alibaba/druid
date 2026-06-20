package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DM (Dameng) CREATE TABLE with IDENTITY(seed, increment) column constraint.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6583">Issue #6583</a>
 */
public class Issue6583 {
    @Test
    public void test_create_table_with_identity() {
        String sql = "CREATE TABLE TEST.TEST_CREATE (\n"
                + "    id INT IDENTITY(1,1),\n"
                + "    name VARCHAR(64) NOT NULL,\n"
                + "    PRIMARY KEY (id)\n"
                + ")";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_with_identity_default() {
        String sql = "CREATE TABLE T (id INT IDENTITY)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
    }
}
