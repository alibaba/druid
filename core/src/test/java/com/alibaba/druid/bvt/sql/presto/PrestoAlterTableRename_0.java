package com.alibaba.druid.bvt.sql.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrestoAlterTableRename_0 {

    @Test
    public void test_alter_schema() {
        String sql = "ALTER SCHEMA name RENAME TO new_name";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        assertEquals("ALTER SCHEMA name RENAME TO new_name", stmt.toString());
    }

    @Test
    public void test_alter_schema2() {
        String sql = "ALTER SCHEMA db.name RENAME TO new_name";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.presto);
        SQLStatement stmt = parser.parseStatement();

        assertEquals("ALTER SCHEMA db.name RENAME TO new_name", stmt.toString());
    }
}
