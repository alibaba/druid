package com.alibaba.druid.oracle;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleJSONTableExpr;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * Created by pku_liuqiang on 2025/5/7.
 * 类说明：
 */
public class OracleJsonTableTest extends TestCase {

    public void testLimit() {
        String sql = "select a1.* " +
                "from aaa a1 left join " +
                "JSON_TABLE(a1.jsondata, '$[*]' COLUMNS ( id NUMBER PATH '$.id', code VARCHAR2(100) path '$.code')) b1 on a1.id=b1.id";
        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        OracleASTVisitor visitor = new OracleASTVisitorAdapter() {
            @Override
            public boolean visit(OracleJSONTableExpr x) {
                Assert.assertNotNull(x.getExpr());
                Assert.assertNotNull(x.getPath());
                Assert.assertEquals(2, x.getColumns().size());
                return super.visit(x);
            }

            @Override
            public boolean visit(OracleJSONTableExpr.Column x) {
                Assert.assertNotNull(x.getName());
                Assert.assertNotNull(x.getDataType());
                Assert.assertNotNull(x.getPath());
                return super.visit(x);
            }
        };
        stmtList.get(0).accept(visitor);
    }
}
