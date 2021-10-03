package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Issue4444 {
    protected final DbType dbType = DbType.mysql;

    @Test
    public void test_idle2() throws Exception {
        String sql = "create role 'role1'";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement statement = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        statement.accept(visitor);
        assertEquals("CREATE ROLE 'role1'", statement.toString());
    }
}
