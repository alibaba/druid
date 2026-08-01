package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression for issue #5135: Oracle CREATE VIEW accepts the EDITIONABLE / NONEDITIONABLE
 * keywords (Oracle 11.2+) between FORCE and VIEW. The parser previously hit
 * {@code token IDENTIFIER EDITIONABLE} and threw {@code ParserException: TODO}.
 */
public class OracleCreateViewEditionableTest {
    @Test
    public void createEditionableView() {
        String sql = "CREATE OR REPLACE FORCE EDITIONABLE VIEW \"STUDENT_VIEW2\" (\"id\", \"name\") AS\n"
                + "SELECT STUDENT.\"id\", STUDENT.\"name\"\nFROM STUDENT";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());

        SQLCreateViewStatement view = (SQLCreateViewStatement) stmtList.get(0);
        assertTrue(view.isForce(), "FORCE must be captured");
        assertEquals(Boolean.TRUE, view.getEditionable(), "EDITIONABLE must be captured");

        String formatted = SQLUtils.formatOracle(sql);
        assertEquals("CREATE OR REPLACE FORCE EDITIONABLE VIEW \"STUDENT_VIEW2\" ("
                + "\n\t\"id\", "
                + "\n\t\"name\""
                + "\n)"
                + "\nAS"
                + "\nSELECT STUDENT.\"id\", STUDENT.\"name\""
                + "\nFROM STUDENT", formatted);
    }

    @Test
    public void createNonEditionableView() {
        String sql = "CREATE OR REPLACE FORCE NONEDITIONABLE VIEW v AS SELECT 1 FROM dual";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());

        SQLCreateViewStatement view = (SQLCreateViewStatement) stmtList.get(0);
        assertEquals(Boolean.FALSE, view.getEditionable(), "NONEDITIONABLE must be captured as false");
    }

    @Test
    public void createViewWithoutEditionable() {
        // Regression guard: views that omit the keyword must keep working and leave
        // editionable unset (null).
        String sql = "CREATE OR REPLACE VIEW v AS SELECT 1 FROM dual";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        SQLCreateViewStatement view = (SQLCreateViewStatement) stmtList.get(0);
        assertNull(view.getEditionable(), "editionable must be null when the keyword is absent");
    }
}
