package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_CreateTableTest extends TestCase {
    public void test_simple_create_table() throws Exception {
        String sql = "CREATE TABLE users (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(200), " +
                "created_at TIMESTAMP DEFAULT SYSDATE" +
                ")";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateTableStatement);

        SQLCreateTableStatement createStmt = (SQLCreateTableStatement) stmt;
        assertEquals("users", createStmt.getTableSource().getName().getSimpleName());
        assertEquals(4, createStmt.getColumnDefinitions().size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_create_table_with_constraints() throws Exception {
        String sql = "CREATE TABLE orders (" +
                "id INT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "amount DECIMAL(10,2), " +
                "status VARCHAR(20) DEFAULT 'pending', " +
                "CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateTableStatement);

        SQLCreateTableStatement createStmt = (SQLCreateTableStatement) stmt;
        assertEquals("orders", createStmt.getTableSource().getName().getSimpleName());
        assertEquals(4, createStmt.getColumnDefinitions().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_create_table_as_select() throws Exception {
        String sql = "CREATE TABLE users_backup AS SELECT * FROM users WHERE status = 'active'";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateTableStatement);

        SQLCreateTableStatement createStmt = (SQLCreateTableStatement) stmt;
        assertEquals("users_backup", createStmt.getTableSource().getName().getSimpleName());
        assertNotNull(createStmt.getSelect());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_drop_table() throws Exception {
        String sql = "DROP TABLE users CASCADE CONSTRAINTS";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLDropTableStatement);

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_create_index() throws Exception {
        String sql = "CREATE INDEX idx_users_email ON users(email)";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateIndexStatement);

        SQLCreateIndexStatement createIndexStmt = (SQLCreateIndexStatement) stmt;
        assertEquals("idx_users_email", createIndexStmt.getName().getSimpleName());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_create_sequence() throws Exception {
        String sql = "CREATE SEQUENCE seq_users START WITH 1 INCREMENT BY 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLCreateSequenceStatement);

        SQLCreateSequenceStatement seqStmt = (SQLCreateSequenceStatement) stmt;
        assertEquals("seq_users", seqStmt.getName().getSimpleName());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
