package com.alibaba.druid.bvt.bug;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class Issue_697 extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "insert into tag_rule_detail(id, gmt_create, gmt_modified, group_id, priority, rule_condition, rule_action) values(1010102, now(), now(), 10101, 0, 'flow=''trustLogin''', 'be:login,dev:pc, env:web, type:trust_login, from:$loginfrom, result:true') ;\n"
                     + "insert into tag_rule_detail(id, gmt_create, gmt_modified, group_id, priority, rule_condition, rule_action) values(1010103, now(), now(), 10101, 0, 'flow=''Ctr''', 'be:login,dev:pc, env:web, type:ctr, from:$loginfrom, result:true') ;";

        String expected = "INSERT INTO tag_rule_detail (id, gmt_create, gmt_modified, group_id, priority\n" +
                "\t, rule_condition, rule_action)\n" +
                "VALUES (1010102, now(), now(), 10101, 0\n" +
                "\t, 'flow=''trustLogin''', 'be:login,dev:pc, env:web, type:trust_login, from:$loginfrom, result:true');\n" +
                "\n" +
                "INSERT INTO tag_rule_detail (id, gmt_create, gmt_modified, group_id, priority\n" +
                "\t, rule_condition, rule_action)\n" +
                "VALUES (1010103, now(), now(), 10101, 0\n" +
                "\t, 'flow=''Ctr''', 'be:login,dev:pc, env:web, type:ctr, from:$loginfrom, result:true');";


        Assert.assertEquals(expected, SQLUtils.formatMySql(sql));
    }
}
