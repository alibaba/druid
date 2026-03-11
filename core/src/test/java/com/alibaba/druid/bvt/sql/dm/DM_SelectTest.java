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

    public void test_select_top() throws Exception {
        String sql = "SELECT TOP 0,4 \"ID\", \"NAME\" FROM \"SYSDBA\".\"TEST1\"";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("SYSDBA.TEST1")));

        String formatted = SQLUtils.toSQLString(statementList, DbType.dm);
        assertTrue(formatted.contains("TOP"));
        assertFalse(formatted.contains("LIMIT"));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_top_count() throws Exception {
        String sql = "SELECT TOP 4 \"ID\", \"NAME\" FROM \"SYSDBA\".\"TEST1\"";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        String formatted = SQLUtils.toSQLString(statementList, DbType.dm);
        assertTrue(formatted.contains("TOP"));
        assertFalse(formatted.contains("LIMIT"));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }

    public void test_select_listagg_with_connect_by() throws Exception {
        String sql = "SELECT project_id, user_id, LISTAGG(path_module_id_seq, ',')" +
                "  as moduleIds FROM ( SELECT DISTINCT t1.project_id, t1.user_id," +
                " t2.path_module_id_seq FROM t_face_map_path_user t1 LEFT JOIN" +
                " ( SELECT id, project_id,corp_code,is_delete," +
                " TRIM(REGEXP_SUBSTR(path_module_id_seq, '[^,]+', 1, LEVEL))" +
                " as path_module_id_seq from t_face_map_path" +
                " CONNECT BY LEVEL <= (REGEXP_COUNT(path_module_id_seq, ',') + 1)" +
                " AND id = PRIOR id AND PRIOR DBMS_RANDOM.VALUE IS NOT NULL ) t2" +
                " on t1.path_id = t2.id and t1.project_id = t2.project_id" +
                " WHERE t1.corp_code = ? and t2.corp_code = ?" +
                " and t2.is_delete = FALSE AND t1.project_id = ?" +
                " AND t1.user_id in (?, ?, ?, ?, ?, ?, ?)) GROUP BY project_id,user_id";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.dm);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertTrue(stmt instanceof SQLSelectStatement);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);
        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_face_map_path_user")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_face_map_path")));

        SQLParseAssertUtil.assertParseSql(sql, DbType.dm);
    }
}
