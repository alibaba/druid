package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class CompatibleTest extends TestCase {
    public void test_for_issue_3986() throws Exception {
        String sql = "select 1 from dual;";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "mysql");
        assertEquals(1, stmts.size());
        assertEquals("select 1\n" +
                "from dual;", stmts.get(0).toLowerCaseString());
    }
}
