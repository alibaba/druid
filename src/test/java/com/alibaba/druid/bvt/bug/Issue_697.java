package com.alibaba.druid.bvt.bug;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class Issue_697 extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "insert into tag_rule_detail(id, gmt_create, gmt_modified, group_id, priority, rule_condition, rule_action) values(1010102, now(), now(), 10101, 0, 'flow=''trustLogin''', 'be:login,dev:pc, env:web, type:trust_login, from:$loginfrom, result:true') ;\n"
                     + "insert into tag_rule_detail(id, gmt_create, gmt_modified, group_id, priority, rule_condition, rule_action) values(1010103, now(), now(), 10101, 0, 'flow=''Ctr''', 'be:login,dev:pc, env:web, type:ctr, from:$loginfrom, result:true') ;";

        String expected = "INSERT INTO tag_rule_detail (id, gmt_create, gmt_modified, group_id, priority"
                          + "\n\t, rule_condition, rule_action)"
                          + "\nVALUES (1010102, now(), now(), 10101, 0"
                          + "\n\t, 'flow=''trustLogin''', 'be:login,dev:pc, env:web, type:trust_login, from:$loginfrom, result:true');"
                          + "\nINSERT INTO tag_rule_detail (id, gmt_create, gmt_modified, group_id, priority"
                          + "\n\t, rule_condition, rule_action)"
                          + "\nVALUES (1010103, now(), now(), 10101, 0"
                          + "\n\t, 'flow=''Ctr''', 'be:login,dev:pc, env:web, type:ctr, from:$loginfrom, result:true');\n";

        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.print(";");
            visitor.println();
        }

        System.out.println(out.toString());

        Assert.assertEquals(expected, out.toString());
    }
}
