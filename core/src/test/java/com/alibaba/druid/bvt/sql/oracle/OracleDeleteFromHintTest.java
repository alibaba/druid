package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OracleDeleteFromHintTest {
    @Test
    public void deleteFromHint() {
        // Hint placed after FROM, mirroring the report in issue #5200.
        String sql = "DELETE FROM /*+index(T PK_OM_WF_RECEPTION_WAITING)*/ OM_WF_RECEPTION_WAITING T WHERE REGION = 200 AND ORDERLINEID = ? AND STATUS = ?";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());

        OracleDeleteStatement stmt = (OracleDeleteStatement) stmtList.get(0);
        List<SQLHint> hints = stmt.getHints();
        assertEquals(1, hints.size(), "hint after FROM must be captured");
        assertTrue(hints.get(0).toString().contains("index(T PK_OM_WF_RECEPTION_WAITING)"),
                "captured hint text must match");

        // Re-serialize: the output visitor renders hints at the canonical position
        // right after DELETE, regardless of where they appeared in the source SQL.
        String formatted = SQLUtils.formatOracle(sql);
        assertEquals("DELETE /*+index(T PK_OM_WF_RECEPTION_WAITING)*/ FROM OM_WF_RECEPTION_WAITING T"
                + "\nWHERE REGION = 200\n\tAND ORDERLINEID = ?\n\tAND STATUS = ?", formatted);
    }

    @Test
    public void deleteHintBeforeFromStillWorks() {
        // Regression guard: the pre-existing `DELETE /*+hint*/ FROM t` path must keep working.
        String sql = "DELETE /*+index(a MTN_SMS_LOG_PK)*/ FROM MTN_SMS_LOG a WHERE id = 1";

        OracleStatementParser parser = new OracleStatementParser(sql);
        OracleDeleteStatement stmt = (OracleDeleteStatement) parser.parseStatement();

        List<SQLHint> hints = stmt.getHints();
        assertEquals(1, hints.size());
        assertTrue(hints.get(0).toString().contains("index(a MTN_SMS_LOG_PK)"));
    }

    @Test
    public void parseStatementDirectly() {
        // Mirrors the exact reproduction path from the issue report.
        String sql = "DELETE FROM  /*+index(T PK_OM_WF_RECEPTION_WAITING)*/ OM_WF_RECEPTION_WAITING T WHERE REGION = 200";
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }
}
