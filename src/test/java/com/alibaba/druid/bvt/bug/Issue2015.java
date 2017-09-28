package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Issue2015 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "update t set a=1,b=2 where a > 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        StringBuffer buf = new StringBuffer();
        stmtList.get(0).output(buf);
        assertEquals("UPDATE t\n" +
                "SET a = 1, b = 2\n" +
                "WHERE a > 1", buf.toString());
    }
}
