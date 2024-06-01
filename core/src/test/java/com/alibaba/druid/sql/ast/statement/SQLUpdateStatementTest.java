package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLUpdateStatementTest {
    @Test
    public void testGetTableName() {
        SQLUpdateBuilderImpl updateBuilder = (SQLUpdateBuilderImpl)SQLBuilderFactory.createUpdateBuilder("UPDATE a\n" +
                "LEFT JOIN b ON b.id = a.id\n" +
                "LEFT JOIN c ON c.id = a.id\n" +
                "set a.id=11,b.id=11,c.id=11", DbType.mysql);
        SQLUpdateStatement sqlUpdateStatement = updateBuilder.getSQLUpdateStatement();
        SQLName tableName = sqlUpdateStatement.getTableName();
        String simpleName = sqlUpdateStatement.getTableName(true).getSimpleName();
        // 原有逻辑返回null
        assertTrue(tableName == null);
        // 新增逻辑 返回表名a
        assertTrue(simpleName.equals("a"));
    }
}
