package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression for issue #5680: Oracle PL/SQL explicit cursor forward declarations
 * ({@code CURSOR c1 RETURN departments%ROWTYPE;}) failed to parse because the parser
 * required {@code IS <select>} after the cursor name with no RETURN clause support.
 */
public class OracleCursorReturnTest {
    @Test
    public void cursorWithReturnForwardDeclaration() {
        // A cursor spec without a body: RETURN names the row type, no IS SELECT.
        String sql = "DECLARE\n"
                + "  CURSOR c1 RETURN departments%ROWTYPE;\n"
                + "BEGIN\n"
                + "  NULL;\n"
                + "END;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());

        String formatted = SQLUtils.formatOracle(sql);
        assertEquals("DECLARE\n"
                + "\tCURSOR c1 RETURN departments%ROWTYPE;\n"
                + "BEGIN\n"
                + "\tNULL;\n"
                + "END;", formatted);
    }

    @Test
    public void cursorWithReturnAndSelect() {
        // RETURN may also precede the IS SELECT body.
        String sql = "DECLARE\n"
                + "  CURSOR c1 RETURN departments%ROWTYPE IS\n"
                + "    SELECT * FROM departments;\n"
                + "BEGIN\n"
                + "  NULL;\n"
                + "END;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());

        // Verify the cursor parameter carries the return data type.
        SQLParameter cursorParam = findFirstCursorParameter(stmtList.get(0));
        assertNotNull(cursorParam);
        assertNotNull(cursorParam.getReturnDataType(), "cursor RETURN type must be captured");
        assertTrue(cursorParam.getReturnDataType().toString().contains("departments"));
    }

    @Test
    public void cursorWithoutReturnStillWorks() {
        // Regression guard: the existing `CURSOR c1 IS SELECT ...` form must keep working.
        String sql = "DECLARE\n"
                + "  CURSOR c1 IS\n"
                + "    SELECT * FROM departments;\n"
                + "BEGIN\n"
                + "  NULL;\n"
                + "END;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmtList.size());
    }

    private static SQLParameter findFirstCursorParameter(SQLStatement stmt) {
        final SQLParameter[] found = new SQLParameter[1];
        stmt.accept(new com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter() {
            @Override
            public boolean visit(SQLParameter x) {
                if (found[0] == null && x.getDataType() != null
                        && "CURSOR".equalsIgnoreCase(x.getDataType().getName())) {
                    found[0] = x;
                }
                return true;
            }
        });
        return found[0];
    }
}
