package com.alibaba.druid.bvt.sql.sqlserver.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SQL Server FOR XML clause must be rendered once, with the mode/path first and the options
 * comma-separated (including the first option); previously the first option was dropped and the
 * path was emitted as a duplicate "FOR XML" clause.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6564">Issue #6564</a>
 */
public class Issue6564 {
    private static String rt(String sql) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.sqlserver);
        assertEquals(1, stmts.size());
        return SQLUtils.toSQLString(stmts.get(0), DbType.sqlserver).replaceAll("\\s+", " ").trim();
    }

    @Test
    public void test_for_xml_path_with_type() {
        assertEquals("SELECT ',' + Name FROM Employees FOR XML PATH(''), TYPE",
                rt("SELECT ',' + Name FROM Employees FOR XML PATH(''), TYPE"));
    }

    @Test
    public void test_for_xml_path_only() {
        assertEquals("SELECT Name FROM Employees FOR XML PATH('')",
                rt("SELECT Name FROM Employees FOR XML PATH('')"));
    }

    @Test
    public void test_for_xml_auto_options_keep_first() {
        assertEquals("SELECT id FROM t FOR XML AUTO, TYPE, XMLSCHEMA, ELEMENTS XSINIL",
                rt("SELECT id FROM t FOR XML AUTO, TYPE, XMLSCHEMA, ELEMENTS XSINIL"));
    }
}
