package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompatibleTest {
    @Test
    public void test_for_issue_3986() throws Exception {
        String sql = "select 1 from dual;";

        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, "mysql");
        assertEquals(1, stmts.size());
        assertEquals("select 1\n" +
                "from dual;", stmts.get(0).toLowerCaseString());
    }
}
