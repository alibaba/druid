package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import junit.framework.TestCase;

import java.util.List;

public class DM_AdvancedTest extends TestCase {
    // =============== CTE (WITH) ===============
    public void test_with_clause() {
        String sql = "WITH dept_avg AS (SELECT dept_id, AVG(salary) as avg_sal FROM employees GROUP BY dept_id) " +
                "SELECT e.*, d.avg_sal FROM employees e JOIN dept_avg d ON e.dept_id = d.dept_id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_recursive_cte() {
        String sql = "WITH org_tree AS (" +
                "SELECT id, name, parent_id FROM org WHERE parent_id IS NULL " +
                "UNION ALL " +
                "SELECT o.id, o.name, o.parent_id FROM org o JOIN org_tree t ON o.parent_id = t.id" +
                ") SELECT * FROM org_tree";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== WINDOW FUNCTIONS ===============
    public void test_window_row_number() {
        String sql = "SELECT id, name, ROW_NUMBER() OVER (ORDER BY created_at DESC) as rn FROM users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_window_rank() {
        String sql = "SELECT id, score, RANK() OVER (PARTITION BY class_id ORDER BY score DESC) as rank FROM students";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("students")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_window_sum() {
        String sql = "SELECT id, amount, SUM(amount) OVER (PARTITION BY user_id ORDER BY created_at) as running_total FROM orders";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("orders")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_window_lag_lead() {
        String sql = "SELECT id, value, LAG(value, 1) OVER (ORDER BY id) as prev_val, " +
                "LEAD(value, 1) OVER (ORDER BY id) as next_val FROM data";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("data")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== CASE WHEN ===============
    public void test_case_when() {
        String sql = "SELECT id, CASE WHEN status = 1 THEN '激活' WHEN status = 0 THEN '禁用' ELSE '未知' END as status_name FROM users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_case_simple() {
        String sql = "SELECT id, CASE status WHEN 'A' THEN 'Active' WHEN 'I' THEN 'Inactive' END as status_desc FROM users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== UNION / INTERSECT / MINUS ===============
    public void test_union() {
        String sql = "SELECT id, name FROM users UNION SELECT id, name FROM admins";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("admins")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_union_all() {
        String sql = "SELECT id FROM table1 UNION ALL SELECT id FROM table2 UNION ALL SELECT id FROM table3";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(3, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_intersect() {
        String sql = "SELECT user_id FROM orders INTERSECT SELECT user_id FROM premium_users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_minus() {
        String sql = "SELECT id FROM all_users MINUS SELECT id FROM banned_users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== EXISTS / NOT EXISTS ===============
    public void test_exists() {
        String sql = "SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_not_exists() {
        String sql = "SELECT * FROM products p WHERE NOT EXISTS (SELECT 1 FROM order_items oi WHERE oi.product_id = p.id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== JOIN TYPES ===============
    public void test_inner_join() {
        String sql = "SELECT * FROM orders o INNER JOIN users u ON o.user_id = u.id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_left_outer_join() {
        String sql = "SELECT * FROM users u LEFT OUTER JOIN orders o ON u.id = o.user_id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_right_join() {
        String sql = "SELECT * FROM orders o RIGHT JOIN users u ON o.user_id = u.id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_full_outer_join() {
        String sql = "SELECT * FROM table1 t1 FULL OUTER JOIN table2 t2 ON t1.id = t2.id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_cross_join() {
        String sql = "SELECT * FROM colors CROSS JOIN sizes";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== SUBQUERIES ===============
    public void test_scalar_subquery() {
        String sql = "SELECT id, name, (SELECT COUNT(*) FROM orders WHERE user_id = u.id) as order_count FROM users u";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertEquals(2, visitor.getTables().size());
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_derived_table() {
        String sql = "SELECT * FROM (SELECT id, name, ROWNUM as rn FROM users WHERE status = 'active') WHERE rn <= 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== ORACLE COMPATIBLE SYNTAX ===============
    public void test_connect_by() {
        String sql = "SELECT id, name, LEVEL FROM org START WITH parent_id IS NULL CONNECT BY PRIOR id = parent_id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("org")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_decode() {
        String sql = "SELECT id, DECODE(status, 'A', 'Active', 'I', 'Inactive', 'Unknown') FROM users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_nvl() {
        String sql = "SELECT id, NVL(nickname, name) as display_name FROM users";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmtList.get(0).accept(visitor);
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("users")));
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    // =============== TRANSACTIONS ===============
    public void test_commit() {
        String sql = "COMMIT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        String formatted = SQLUtils.toSQLString(stmtList, DbType.dm);
        assertTrue(formatted.contains("COMMIT"));
    }

    public void test_rollback() {
        String sql = "ROLLBACK";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLRollbackStatement);
    }

    public void test_savepoint() {
        String sql = "SAVEPOINT sp1;";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLSavePointStatement);
    }

    // =============== DCL ===============
    public void test_grant() {
        String sql = "GRANT SELECT, INSERT ON users TO user1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLGrantStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_revoke() {
        String sql = "REVOKE DELETE ON users FROM user1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, stmtList.size());
        assertTrue(stmtList.get(0) instanceof SQLRevokeStatement);
        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
