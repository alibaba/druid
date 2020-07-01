package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Issue1912 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "select a from t";
        SQLStatementParser parser = new SQLStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        final Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("a", "b");

        SQLASTVisitor visitor = new SQLASTVisitorAdapter() {
            public boolean visit(SQLIdentifierExpr x) {

                String destColumn = columnMapping.get(x.getName());
                if (destColumn != null) {
                    x.setName(destColumn);
                }

                return super.visit(x);
            }
        };
        stmt.accept(visitor);

        assertEquals("SELECT b\n" +
                "FROM t", SQLUtils.toSQLString(stmt));
    }
}
