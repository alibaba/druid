package com.alibaba.druid.demo.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 11/07/2017.
 */
public class Demo_for_issue_1815 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from t1;select * from t2;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.oracle);

        assertEquals("SELECT *\n" +
                "FROM t1;", stmtList.get(0).toString());

        SQLASTOutputVisitor.defaultPrintStatementAfterSemi = false;

        assertEquals("SELECT *\n" +
                "FROM t1", stmtList.get(0).toString());

        SQLASTOutputVisitor.defaultPrintStatementAfterSemi = null;
    }
}
