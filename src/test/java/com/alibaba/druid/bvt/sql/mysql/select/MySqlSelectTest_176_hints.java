package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_176_hints extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT a.*,b.start_time,b.end_time,b.user_limit,b.member_limit,b.attributes,b.merchant_code,b.terminal FROM wdk_buygift_item a, wdk_online_activity b  WHERE a.act_id=b.act_id and a.status  =1 and b.status=1 and a.buy_item_id = '564779304647' /* ignore * 107074005 */ and a.shop_id=160039352 and b.start_time <=now() and b.end_time>now() order by act_id desc limit 1";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.EnableSQLBinaryOpExprGroup,
                SQLParserFeature.OptimizedForParameterized);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT a.*, b.start_time, b.end_time, b.user_limit, b.member_limit\n" +
                "\t, b.attributes, b.merchant_code, b.terminal\n" +
                "FROM wdk_buygift_item a, wdk_online_activity b\n" +
                "WHERE a.act_id = b.act_id\n" +
                "\tAND a.status = 1\n" +
                "\tAND b.status = 1\n" +
                "\tAND a.buy_item_id = '564779304647' /* ignore * 107074005 */\n" +
                "\tAND a.shop_id = 160039352\n" +
                "\tAND b.start_time <= now()\n" +
                "\tAND b.end_time > now()\n" +
                "ORDER BY act_id DESC\n" +
                "LIMIT 1", stmt.toString());

        assertEquals("select a.*, b.start_time, b.end_time, b.user_limit, b.member_limit\n" +
                "\t, b.attributes, b.merchant_code, b.terminal\n" +
                "from wdk_buygift_item a, wdk_online_activity b\n" +
                "where a.act_id = b.act_id\n" +
                "\tand a.status = 1\n" +
                "\tand b.status = 1\n" +
                "\tand a.buy_item_id = '564779304647' /* ignore * 107074005 */\n" +
                "\tand a.shop_id = 160039352\n" +
                "\tand b.start_time <= now()\n" +
                "\tand b.end_time > now()\n" +
                "order by act_id desc\n" +
                "limit 1", stmt.toLowerCaseString());


        assertEquals("SELECT a.*, b.start_time, b.end_time, b.user_limit, b.member_limit\n" +
                "\t, b.attributes, b.merchant_code, b.terminal\n" +
                "FROM wdk_buygift_item a, wdk_online_activity b\n" +
                "WHERE a.act_id = b.act_id\n" +
                "\tAND a.status = ?\n" +
                "\tAND b.status = ?\n" +
                "\tAND a.buy_item_id = ?\n" +
                "\tAND a.shop_id = ?\n" +
                "\tAND b.start_time <= now()\n" +
                "\tAND b.end_time > now()\n" +
                "ORDER BY act_id DESC\n" +
                "LIMIT ?", stmt.toParameterizedString());
    }


}