package com.alibaba.druid.bvt.sql.sqlserver.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6564">Issue来源</a>
 */
public class Issue6564 {

    @Test
    public void test_for_xml_path_type() {
        String sql = "SELECT ',' + Name FROM Employees FOR XML PATH(''), TYPE";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        assertTrue("Output should contain FOR XML", output.toUpperCase().contains("FOR XML"));
        assertTrue("Output should contain PATH('')", output.contains("PATH('')"));
        assertTrue("Output should contain TYPE", output.toUpperCase().contains("TYPE"));
        // FOR XML should appear only once
        int idx1 = output.toUpperCase().indexOf("FOR XML");
        int idx2 = output.toUpperCase().indexOf("FOR XML", idx1 + 1);
        assertEquals("FOR XML should appear only once", -1, idx2);
    }

    @Test
    public void test_for_xml_auto() {
        String sql = "SELECT * FROM t FOR XML AUTO";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        assertTrue("Output should contain FOR XML AUTO", output.toUpperCase().contains("FOR XML AUTO"));
    }

    @Test
    public void test_for_xml_path_elements() {
        String sql = "SELECT * FROM t FOR XML PATH('root'), ELEMENTS";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.sqlserver);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        String output = stmtList.get(0).toString();
        assertTrue("Output should contain PATH('root')", output.contains("PATH('root')"));
        assertTrue("Output should contain ELEMENTS", output.toUpperCase().contains("ELEMENTS"));
    }
}
