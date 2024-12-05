package com.alibaba.druid.bvt.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLDropTableStatementTest {
    @Test
    public void test_0() throws Exception {
        SQLDropTableStatement stmt = new SQLDropTableStatement();
        stmt.addTableSource("abc");
        assertEquals(stmt, stmt.clone());
        assertEquals(stmt.hashCode(), stmt.clone().hashCode());
    }
}
