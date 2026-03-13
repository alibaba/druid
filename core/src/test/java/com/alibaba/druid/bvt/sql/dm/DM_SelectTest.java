package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_SelectTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_select_basic() {
        String sql = "SELECT * FROM t1 WHERE id = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_join() {
        String sql = "SELECT a.id, b.name FROM t1 a INNER JOIN t2 b ON a.id = b.aid WHERE a.status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_left_join() {
        String sql = "SELECT a.id, b.name FROM t1 a LEFT OUTER JOIN t2 b ON a.id = b.aid";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_full_join() {
        String sql = "SELECT a.id, b.name FROM t1 a FULL JOIN t2 b ON a.id = b.aid";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_cross_join() {
        String sql = "SELECT a.id, b.name FROM t1 a CROSS JOIN t2 b";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_subquery() {
        String sql = "SELECT * FROM (SELECT id, name FROM t1 WHERE status = 1) t WHERE t.id > 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_limit() {
        String sql = "SELECT * FROM t1 ORDER BY id LIMIT 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_limit_offset_comma() {
        // DM: LIMIT <offset>, <count>
        String sql = "SELECT * FROM t1 ORDER BY id LIMIT 20, 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_limit_offset_keyword() {
        // DM: LIMIT <count> OFFSET <offset>
        String sql = "SELECT * FROM t1 ORDER BY id LIMIT 10 OFFSET 20";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_offset_limit() {
        // DM: OFFSET <offset> LIMIT <count>
        String sql = "SELECT * FROM t1 ORDER BY id OFFSET 20 LIMIT 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_top() {
        // DM: SELECT TOP <n> ...
        String sql = "SELECT TOP 10 * FROM t1 ORDER BY id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_top_percent() {
        // DM: SELECT TOP <n> PERCENT ...
        String sql = "SELECT TOP 10 PERCENT * FROM t1 ORDER BY id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_fetch_next() {
        // DM: OFFSET ... ROWS FETCH NEXT <n> ROWS ONLY
        String sql = "SELECT * FROM t1 ORDER BY id OFFSET 5 FETCH NEXT 10 ROWS ONLY";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_group_by_having() {
        String sql = "SELECT dept_id, COUNT(*) cnt FROM employees GROUP BY dept_id HAVING COUNT(*) > 5";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_union() {
        String sql = "SELECT id, name FROM t1 UNION ALL SELECT id, name FROM t2";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_minus() {
        // DM supports MINUS (Oracle compatible)
        String sql = "SELECT id FROM t1 MINUS SELECT id FROM t2";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_intersect() {
        String sql = "SELECT id FROM t1 INTERSECT SELECT id FROM t2";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_with_cte() {
        String sql = "WITH cte AS (SELECT id, name FROM t1 WHERE status = 1) SELECT * FROM cte WHERE id > 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_recursive_cte() {
        String sql = "WITH RECURSIVE cte(id, name, parent_id) AS (" +
                "SELECT id, name, parent_id FROM dept WHERE parent_id IS NULL " +
                "UNION ALL " +
                "SELECT d.id, d.name, d.parent_id FROM dept d JOIN cte c ON d.parent_id = c.id" +
                ") SELECT * FROM cte";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_for_update() {
        String sql = "SELECT * FROM t1 WHERE id = 1 FOR UPDATE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_for_update_nowait() {
        String sql = "SELECT * FROM t1 WHERE id = 1 FOR UPDATE NOWAIT";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_for_update_wait() {
        // DM: FOR UPDATE WAIT N
        String sql = "SELECT * FROM t1 WHERE id = 1 FOR UPDATE WAIT 5";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_for_update_skip_locked() {
        // DM: FOR UPDATE SKIP LOCKED
        String sql = "SELECT * FROM t1 WHERE id = 1 FOR UPDATE SKIP LOCKED";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_distinct() {
        String sql = "SELECT DISTINCT name FROM t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_case_when() {
        String sql = "SELECT id, CASE WHEN status = 1 THEN 'active' WHEN status = 0 THEN 'inactive' ELSE 'unknown' END AS status_desc FROM t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_window_function() {
        String sql = "SELECT id, name, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn FROM employees";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_rank_dense_rank() {
        String sql = "SELECT id, RANK() OVER (ORDER BY salary DESC) AS rnk, DENSE_RANK() OVER (ORDER BY salary DESC) AS drnk FROM employees";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_connect_by() {
        // DM Oracle-compatible hierarchical query
        String sql = "SELECT id, name, LEVEL FROM dept START WITH parent_id IS NULL CONNECT BY PRIOR id = parent_id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_connect_by_nocycle() {
        String sql = "SELECT id, name FROM dept CONNECT BY NOCYCLE PRIOR id = parent_id START WITH parent_id IS NULL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_exists() {
        String sql = "SELECT * FROM t1 WHERE EXISTS (SELECT 1 FROM t2 WHERE t2.aid = t1.id)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_in_subquery() {
        String sql = "SELECT * FROM t1 WHERE id IN (SELECT aid FROM t2 WHERE status = 1)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_select_nvl() {
        // DM Oracle-compatible NVL function
        String sql = "SELECT NVL(name, 'unknown') FROM t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_decode() {
        // DM Oracle-compatible DECODE function
        String sql = "SELECT DECODE(status, 1, 'active', 0, 'inactive', 'unknown') FROM t1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_sysdate() {
        // DM Oracle-compatible SYSDATE
        String sql = "SELECT SYSDATE FROM DUAL";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_select_nulls_first_last() {
        // DM: ORDER BY ... NULLS FIRST|LAST
        String sql = "SELECT * FROM t1 ORDER BY name ASC NULLS FIRST";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
