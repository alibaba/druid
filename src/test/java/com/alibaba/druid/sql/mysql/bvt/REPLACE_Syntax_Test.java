package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class REPLACE_Syntax_Test extends TestCase {
    public void test_0() throws Exception {
        String sql = "REPLACE INTO T SELECT * FROM T;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("REPLACE INTO T\n\tSELECT *\n\tFROM T;", text);
    }

    public void test_1() throws Exception {
        String sql =
                "REPLACE DELAYED INTO `online_users` SET `session_id`='3580cc4e61117c0785372c426eddd11c', `user_id` = 'XXX', `page` = '/', `lastview` = NOW();";
        
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("REPLACE DELAYED INTO `online_users`\nSET `session_id` = '3580cc4e61117c0785372c426eddd11c', `user_id` = 'XXX', `page` = '/', `lastview` = NOW();", text);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
