package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_SelectTest extends TestCase {
    public void test_simple_select() throws Exception {
        String sql = "SELECT * FROM users";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_with_where() throws Exception {
        String sql = "SELECT id, name, age FROM users WHERE id = 1 AND status = 'active'";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertEquals(2, visitor.getConditions().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_with_join() throws Exception {
        String sql = "SELECT u.id, u.name, o.order_id FROM users u LEFT JOIN orders o ON u.id = o.user_id";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("orders")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_with_subquery() throws Exception {
        String sql = "SELECT * FROM users WHERE id IN (SELECT user_id FROM orders WHERE amount > 100)";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("orders")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_with_group_by() throws Exception {
        String sql = "SELECT status, COUNT(*) as cnt FROM users GROUP BY status HAVING COUNT(*) > 1";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_with_order_by() throws Exception {
        String sql = "SELECT * FROM users ORDER BY created_at DESC, id ASC";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertEquals(2, visitor.getOrderByColumns().size());

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_from_dual() throws Exception {
        String sql = "SELECT SYSDATE FROM DUAL";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_rownum() throws Exception {
        String sql = "SELECT * FROM users WHERE ROWNUM <= 10";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
