package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PGMergeStatementTest extends PGTest {
    public void testMerge_basic() {
        String sql = "MERGE INTO target t USING source s ON t.id = s.id "
                + "WHEN MATCHED THEN UPDATE SET t.val = s.val "
                + "WHEN NOT MATCHED THEN INSERT (id, val) VALUES (s.id, s.val)";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);
        assertNotNull(stmt.getInto());
        assertNotNull(stmt.getUsing());
        assertNotNull(stmt.getOn());
        assertEquals(2, stmt.getWhens().size());
        assertTrue(stmt.getWhens().get(0) instanceof SQLMergeStatement.WhenUpdate);
        assertTrue(stmt.getWhens().get(1) instanceof SQLMergeStatement.WhenInsert);

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("MERGE"));
        assertTrue(output.contains("UPDATE"));
        assertTrue(output.contains("INSERT"));
    }

    public void testMerge_matchedDoNothing() {
        String sql = "MERGE INTO target t USING source s ON t.id = s.id "
                + "WHEN MATCHED THEN DO NOTHING "
                + "WHEN NOT MATCHED THEN INSERT (id, val) VALUES (s.id, s.val)";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);
        assertEquals(2, stmt.getWhens().size());
        assertTrue(stmt.getWhens().get(0) instanceof SQLMergeStatement.WhenDoNothing);
        assertFalse(((SQLMergeStatement.WhenDoNothing) stmt.getWhens().get(0)).isNot());
        assertTrue(stmt.getWhens().get(1) instanceof SQLMergeStatement.WhenInsert);

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("DO NOTHING"));
    }

    public void testMerge_notMatchedDoNothing() {
        String sql = "MERGE INTO target t USING source s ON t.id = s.id "
                + "WHEN MATCHED AND s.deleted THEN DELETE "
                + "WHEN MATCHED THEN UPDATE SET val = s.val "
                + "WHEN NOT MATCHED THEN DO NOTHING";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);
        assertEquals(3, stmt.getWhens().size());
        assertTrue(stmt.getWhens().get(0) instanceof SQLMergeStatement.WhenDelete);
        assertTrue(stmt.getWhens().get(1) instanceof SQLMergeStatement.WhenUpdate);
        assertTrue(stmt.getWhens().get(2) instanceof SQLMergeStatement.WhenDoNothing);

        SQLMergeStatement.WhenDoNothing doNothing = (SQLMergeStatement.WhenDoNothing) stmt.getWhens().get(2);
        assertTrue(doNothing.isNot());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("DO NOTHING"));
        assertTrue(output.contains("DELETE"));
    }

    public void testMerge_customerAccount() {
        String sql = "MERGE INTO customer_account ca "
                + "USING recent_transactions t ON t.customer_id = ca.customer_id "
                + "WHEN MATCHED THEN UPDATE SET balance = balance + transaction_value "
                + "WHEN NOT MATCHED THEN INSERT (customer_id, balance) VALUES (t.customer_id, t.transaction_value)";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);
        assertEquals(2, stmt.getWhens().size());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("MERGE"));
        assertTrue(output.contains("customer_account"));
        assertTrue(output.contains("recent_transactions"));
    }

    public void testMerge_doNothingRoundtrip() {
        String sql = "MERGE INTO target t USING source s ON t.id = s.id "
                + "WHEN MATCHED THEN DO NOTHING "
                + "WHEN NOT MATCHED THEN DO NOTHING";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLMergeStatement stmt = (SQLMergeStatement) statementList.get(0);
        assertEquals(2, stmt.getWhens().size());
        assertTrue(stmt.getWhens().get(0) instanceof SQLMergeStatement.WhenDoNothing);
        assertTrue(stmt.getWhens().get(1) instanceof SQLMergeStatement.WhenDoNothing);

        assertFalse(((SQLMergeStatement.WhenDoNothing) stmt.getWhens().get(0)).isNot());
        assertTrue(((SQLMergeStatement.WhenDoNothing) stmt.getWhens().get(1)).isNot());

        String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
        assertTrue(output.contains("WHEN MATCHED THEN DO NOTHING"));
        assertTrue(output.contains("WHEN NOT MATCHED THEN DO NOTHING"));
    }
}
