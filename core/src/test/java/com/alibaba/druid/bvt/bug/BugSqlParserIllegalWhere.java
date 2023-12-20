package com.alibaba.druid.bvt.bug;

import java.util.List;
import java.util.regex.Pattern;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.fastjson2.JSON;
import junit.framework.TestCase;

/**
 * 此测试类用于检测试sql中where拼错导致SQL无效， 但经SQLStatementParser解析却丢失where的严复问题.
 * 
 * @author qxo
 */
public class BugSqlParserIllegalWhere extends TestCase {
    public void test4deleteWhere() throws Exception {
        Object[][] samples = {
                    { "update test_tab1 set b= 1 swhere a=1", false },
                    { "select * from test_tab1 swhere  a=1", false },
                    { "delete from test_tab1 \n swhere  a=1", false },
                    { "delete from test_tab1 where a=1", true },
                    { "delete from test_tab1 \n where a=1     \n", true }
                };

        for (final Object[] arr : samples) {
            String sql = (String) arr[0];
            final boolean ok = Boolean.TRUE.equals(arr[1]);
            try {
                System.out.println("before sql:" + sql);
                final StringBuilder out = new StringBuilder();
                final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
                visitor.setParameterizedMergeInList(true);
                SQLStatementParser parser = new OracleStatementParser(sql);
                final SQLStatement parseStatement = parser.parseStatement();
                parseStatement.accept(visitor);
                final List<Object> plist = visitor.getParameters();
                sql = out.toString();
                System.out.println("after sql:" + sql);
                System.out.println("params: " + plist);
                assertEquals("[1]", JSON.toJSONString(plist));
                assertTrue(Pattern.compile("(?i)(^|\\s+)where(\\s+|$)").matcher(sql).find());
                if (!ok) {
                    fail();
                }
            } catch (ParserException ex) {
                if (ok) {
                    fail();
                } else {
                    ex.printStackTrace();
                }
            }
        }
    }
}
