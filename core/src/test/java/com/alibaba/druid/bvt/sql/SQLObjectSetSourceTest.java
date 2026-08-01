package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Locks in the parameter order of {@code SQLObject.setSource(int line, int column)}.
 * See issue #6311: the interface declaration previously named the parameters in the swapped
 * order (column, line), while the implementation and every caller used (line, column).
 * Behavior was unaffected (parameter names are not part of the bytecode), but the misleading
 * signature could invite wrong-position calls. This test pins the live semantics so a future
 * swap is caught.
 */
public class SQLObjectSetSourceTest {
    @Test
    public void setSource_lineFirstColumnSecond() {
        SQLCharExpr expr = new SQLCharExpr("x");
        // Call positionally, the way every caller in the parser does: line first, then column.
        expr.setSource(2, 5);
        assertEquals(2, expr.getSourceLine(), "first setSource argument must be the line");
        assertEquals(5, expr.getSourceColumn(), "second setSource argument must be the column");
    }
}
