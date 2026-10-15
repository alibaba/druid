package com.alibaba.druid.bvt.sql.redshift;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RedshiftListaggTest {
    @Test
    public void testListaggWithinGroup() {
        String sql = "SELECT listagg(col1, ',') WITHIN GROUP (ORDER BY col2) AS agg_col FROM t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.redshift);
        assertEquals(1, stmts.size());

        String result = SQLUtils.toSQLString(stmts.get(0), DbType.redshift);
        assertTrue(result.toUpperCase().contains("LISTAGG"));
        assertTrue(result.toUpperCase().contains("WITHIN GROUP"));
    }

    @Test
    public void testListaggDistinct() {
        String sql = "SELECT listagg(DISTINCT col1, ',') AS agg_col FROM t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.redshift);
        assertEquals(1, stmts.size());

        String result = SQLUtils.toSQLString(stmts.get(0), DbType.redshift);
        assertTrue(result.toUpperCase().contains("LISTAGG"));
        assertTrue(result.toUpperCase().contains("DISTINCT"));
    }

    @Test
    public void testListaggDistinctWithCaseWhen() {
        String sql = "SELECT listagg(DISTINCT CASE WHEN flag = 1 THEN col1 END, ',') AS agg_col FROM t";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.redshift);
        assertEquals(1, stmts.size());

        String result = SQLUtils.toSQLString(stmts.get(0), DbType.redshift);
        assertTrue(result.toUpperCase().contains("LISTAGG"));
    }

    @Test
    public void testInsertWithCteListagg() {
        String sql = "INSERT INTO t1\n"
                + "WITH cte AS (\n"
                + "  SELECT id\n"
                + "    , listagg(DISTINCT CASE WHEN flag = 1 THEN col1 END, ',') AS agg1\n"
                + "    , listagg(col2, ',') WITHIN GROUP (ORDER BY col3) AS agg2\n"
                + "  FROM t2\n"
                + "  GROUP BY id\n"
                + ")\n"
                + "SELECT * FROM cte";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.redshift);
        assertEquals(1, stmts.size());

        String result = SQLUtils.toSQLString(stmts.get(0), DbType.redshift);
        assertTrue(result.toUpperCase().contains("LISTAGG"));
        assertTrue(result.toUpperCase().contains("WITHIN GROUP"));
    }
}
