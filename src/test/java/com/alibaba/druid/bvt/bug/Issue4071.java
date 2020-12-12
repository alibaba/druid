package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import junit.framework.TestCase;

public class Issue4071 extends TestCase {
    public void test_for_issue() throws Exception {
        assertEquals("",new SQLSelectStatement().toString());
    }
}
