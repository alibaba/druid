package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 25/06/2017.
 */
public class Issue1737 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "select * from test_tab1 where name='name' and id in  ('A','B')";
        final StringBuilder out = new StringBuilder();
        final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
        visitor.setParameterizedMergeInList(true);
        SQLStatementParser parser = new OracleStatementParser(sql);
        final SQLStatement parseStatement = parser.parseStatement();
        parseStatement.accept(visitor);
        final List<Object> plist = visitor.getParameters();
        sql = out.toString();
        System.out.println("src:"+sql);
        System.out.println("sql:"+sql);
        System.out.println(" params: " + JSON.toJSONString(plist));

        assertEquals("[\"name\",[\"A\",\"B\"]]", JSON.toJSONString(plist));
    }
}
