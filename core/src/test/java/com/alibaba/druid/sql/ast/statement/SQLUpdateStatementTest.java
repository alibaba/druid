package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLUpdateStatementTest {
    @Test
    public void testGetTableName() {
        SQLUpdateStatement sqlUpdateStatement = new SQLUpdateStatement();
        SQLJoinTableSource tableSource1 = new SQLJoinTableSource();
        SQLJoinTableSource tableSource2 = new SQLJoinTableSource();
        SQLExprTableSource tableSource3 = new SQLExprTableSource();
        tableSource3.setExpr("xxx");
        tableSource2.setLeft(tableSource3);
        tableSource1.setLeft(tableSource2);
        sqlUpdateStatement.setDbType(DbType.mysql);
        sqlUpdateStatement.setTableSource(tableSource1);

        assertEquals(sqlUpdateStatement.getTableName(true), tableSource3.getName());
    }
}
