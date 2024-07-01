package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.test.TestUtils;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by tianzhen.wtz on 2014/12/26 0026 20:44.
 * 类说明：
 */
public class PGIntervalSQLTest extends TestCase {

    public void testIntervalSQL() {
        String sql1 = "select timestamp '2001-09-28 01:00' + interval '23 hours'";
        String sql1Result = "SELECT TIMESTAMP '2001-09-28 01:00' + INTERVAL '23 hours'";
        equal(sql1, sql1Result);

        String sql2 = "select interval '1 day' - interval '1 hour'";
        String sql2Result = "SELECT INTERVAL '1 day' - INTERVAL '1 hour'";
        equal(sql2, sql2Result);

        String sql3 = "select date_part('month', interval '2 years 3 months')";
        String sql3Result = "SELECT date_part('month', INTERVAL '2 years 3 months')";
        equal(sql3, sql3Result);
    }


    private void equal(String targetSql, String resultSql) {
        PGSQLStatementParser parser = new PGSQLStatementParser(targetSql);
        PGSelectStatement statement = parser.parseSelect();
        assertEquals(statement.toString(), resultSql);

    }

    public void testIntervalSQL_OracleToPg() {
        String sql = "SELECT (SYSTIMESTAMP - order_date) DAY(9) TO SECOND from orders WHERE order_id = 2458";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        String output = TestUtils.outputPg(statementList);
        assertEquals("SELECT (SYSTIMESTAMP - order_date) DAY(9) TO SECOND\nFROM orders\nWHERE order_id = 2458", output);
    }
}
