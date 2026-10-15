package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.junit.jupiter.api.Test;

public class Issue_685 {
    @Test
    public void test_for_issue() throws Exception {
        OracleStatementParser parser = new OracleStatementParser("select upper(*) from aa order by now()");
        SQLStatement st = parser.parseStatement();
        st.toString();
    }
}
