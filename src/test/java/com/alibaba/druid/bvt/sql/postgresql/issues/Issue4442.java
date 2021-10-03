package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.wall.WallUtils;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Issue4442 {
    @Test
    public void test_idle2() throws Exception {
        String sql = "INSERT INTO users (id, level)\n" +
                "VALUES (1, 0)\n" +
                "ON CONFLICT (id) DO UPDATE\n" +
                "SET level = users.level + 1;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        SQLStatement statement = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.postgresql);
        statement.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
        assertTrue(WallUtils.isValidatePostgres(sql));
    }
}
