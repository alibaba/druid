package com.alibaba.druid.bvt.bug;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

public class Issue_685 extends TestCase {

    public void test_for_issue() throws Exception {
        OracleStatementParser parser = new OracleStatementParser("select upper(*) from aa order by now()");
        SQLStatement st = parser.parseStatement();
        st.toString();
    }
}
