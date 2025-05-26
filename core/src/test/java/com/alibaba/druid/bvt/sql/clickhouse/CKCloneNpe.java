package com.alibaba.druid.bvt.sql.clickhouse;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CKCloneNpe {
    @Test
    public void test_0() throws Exception {
        String sql = "CREATE TABLE test_local (  \n"
                + "`test_list` Array(UInt64)) \n"
                + "ENGINE = MergeTree()";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.clickhouse);
        SQLStatement stmt = statementList.get(0);
        assertEquals(1, statementList.size());
        assertTrue(stmt instanceof SQLCreateTableStatement);
        stmt.clone();
    }
}
