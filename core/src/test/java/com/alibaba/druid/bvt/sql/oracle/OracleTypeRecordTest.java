package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression for issue #2755: Oracle PL/SQL {@code TYPE ... IS RECORD (...)}
 * declarations failed to parse.
 */
public class OracleTypeRecordTest {
    @Test
    public void typeIsRecord() {
        String sql = "DECLARE\n"
                + "  TYPE TR_EQUIP_SPEC_RECORD IS RECORD (ID VARCHAR2(36), NAME VARCHAR2(200));\n"
                + "  V_SPEC_ROW TR_EQUIP_SPEC_RECORD;\n"
                + "BEGIN\n"
                + "  NULL;\n"
                + "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());
    }

    @Test
    public void typeIsRecordInFullBlock() {
        // Issue's full block with REF CURSOR + RECORD + variable of the record type.
        String sql = "DECLARE\n"
                + "  TYPE T_EQUIP_SPEC IS REF CURSOR;\n"
                + "  V_EQUIP_SPEC T_EQUIP_SPEC;\n"
                + "  TYPE TR_EQUIP_SPEC_RECORD IS RECORD (ID VARCHAR2(36), NAME VARCHAR2(200));\n"
                + "  V_SPEC_ROW TR_EQUIP_SPEC_RECORD;\n"
                + "BEGIN\n"
                + "  NULL;\n"
                + "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "oracle");
        assertEquals(1, stmts.size());
    }
}
