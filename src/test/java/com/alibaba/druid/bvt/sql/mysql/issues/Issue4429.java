package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Issue4429 {
    protected final DbType dbType = DbType.mysql;

    @Test
    public void test_idle2() throws Exception {
        String sql = "SELECT \n" +
                "    GROUP_CONCAT(DISTINCT v\n" +
                "        ORDER BY v ASC\n" +
                "        SEPARATOR ';')\n" +
                "FROM\n" +
                "    t;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatement();
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);

        stmt.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());

        assertEquals("SELECT GROUP_CONCAT(DISTINCT v ORDER BY v ASC SEPARATOR ';')\n" +
                "FROM t", stmt.toString());
    }
}
