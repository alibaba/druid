package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import junit.framework.TestCase;

import java.io.StringWriter;

/**
 * Created by wenshao on 25/06/2017.
 */
public class Issue1769 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "SELECT id FROM test WHERE type = 9 AND name = ? AND orderId in (1, 2, 3);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        StringWriter out = new StringWriter();
        MySqlExportParameterVisitor v = new MySqlExportParameterVisitor(out);
        v.setParameterized(true);
        v.setShardingSupport(false);
        v.setPrettyFormat(false);
        stmt.accept(v);

        System.out.println(v.getParameters());
        System.out.println(v.getTables());
        assertEquals("SELECT id FROM test WHERE type = ? AND name = ? AND orderId IN (?)", out.toString());
    }
}
