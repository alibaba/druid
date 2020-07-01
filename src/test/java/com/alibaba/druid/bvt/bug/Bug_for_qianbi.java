package com.alibaba.druid.bvt.bug;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsOutputVisitor;

public class Bug_for_qianbi extends TestCase {

    public void test_for_issue() throws Exception {
        String sql = "insert into table lol select detail(sellerid,id) as count1,sum(sellerid) as sum1 from ctu_trade_paid_done.time('natural','1d','1h') "
                     + "where match(auctionTitle,\"男鞋\\n中石化&加油卡\\n中石化&充值卡\\n中石化&冲值卡\\n中石化&代冲\\n中石化&代充\\n中国石化&加油卡\\n中国石化&充值卡\\n中国石化&冲值卡\\n中国石化&代冲\\n中国石化&代充\",\"\\n\")";

        String expected = "INSERT INTO TABLE lol\n" +
                "SELECT detail(sellerid, id) AS count1, sum(sellerid) AS sum1\n" +
                "FROM ctu_trade_paid_done:time('natural', '1d', '1h')\n" +
                "WHERE match(auctionTitle, '男鞋\\n中石化&加油卡\\n中石化&充值卡\\n中石化&冲值卡\\n中石化&代冲\\n中石化&代充\\n中国石化&加油卡\\n中国石化&充值卡\\n中国石化&冲值卡\\n中国石化&代冲\\n中国石化&代充', '\\n');\n";

        StringBuilder out = new StringBuilder();
        OdpsOutputVisitor visitor = new OdpsOutputVisitor(out);
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.print(";");
            visitor.println();
        }

        // System.out.println(out.toString());

        Assert.assertEquals(expected, out.toString());
    }
}
