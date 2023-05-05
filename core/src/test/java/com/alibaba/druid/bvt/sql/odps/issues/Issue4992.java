package com.alibaba.druid.bvt.sql.odps.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import org.junit.Test;

import static org.junit.Assert.*;

public class Issue4992 {
    @Test
    public void testInsert() {
        String sql = "CREATE TABLE IF NOT EXISTS test_table \n" +
                "(\n" +
                "  user_id STRING COMMENT\"userid\"\n" +
                "  ,user_features STRING COMMENT\"用户特征\"\n" +
                ")\n" +
                ";";
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) SQLUtils.parseSingleStatement(sql, DbType.odps);
        SQLColumnDefinition column = stmt.getColumn("user_id");
        assertNotNull(column);
        assertNotNull(column.getParent());
        assertSame(stmt, column.getParent());
    }
}
