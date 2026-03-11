package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_InsertTest extends TestCase {
    public void test_simple_insert() throws Exception {
        String sql = "INSERT INTO users (id, name, email) VALUES (1, '张三', 'zhangsan@example.com')";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLInsertStatement);

        SQLInsertStatement insertStmt = (SQLInsertStatement) stmt;
        assertEquals("users", insertStmt.getTableName().getSimpleName());
        assertEquals(3, insertStmt.getColumns().size());
        assertEquals(1, insertStmt.getValuesList().size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_insert_with_select() throws Exception {
        String sql = "INSERT INTO users_backup SELECT * FROM users WHERE status = 'active'";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLInsertStatement);

        SQLInsertStatement insertStmt = (SQLInsertStatement) stmt;
        assertEquals("users_backup", insertStmt.getTableName().getSimpleName());
        assertNotNull(insertStmt.getQuery());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users_backup")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_insert_multiple_values() throws Exception {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLInsertStatement);

        SQLInsertStatement insertStmt = (SQLInsertStatement) stmt;
        assertEquals("users", insertStmt.getTableName().getSimpleName());
        assertEquals(2, insertStmt.getColumns().size());
        assertEquals(3, insertStmt.getValuesList().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_insert_with_returning() throws Exception {
        String sql = "INSERT INTO users (name, email) VALUES ('李四', 'lisi@example.com') RETURNING id INTO :new_id";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLInsertStatement);

        SQLInsertStatement insertStmt = (SQLInsertStatement) stmt;
        assertEquals("users", insertStmt.getTableName().getSimpleName());
        assertEquals(2, insertStmt.getColumns().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
