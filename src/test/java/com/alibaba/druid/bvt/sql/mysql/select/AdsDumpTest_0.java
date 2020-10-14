package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDumpStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class AdsDumpTest_0 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+dump-merge=true*/DUMP DATA SELECT amp.buyer_add_cart_info.buyer_id,amp.buyer_add_cart_info.pre_score,amp.buyer_add_cart_info.cart_price FROM amp.buyer_add_cart_info  JOIN amp.crm_user_base_info ON amp.crm_user_base_info.user_id = amp.buyer_add_cart_info.buyer_id where (((amp.buyer_add_cart_info.seller_id=1921906956)) AND ((amp.buyer_add_cart_info.auction_id=562769960283)) AND ((amp.buyer_add_cart_info.show_price>=13300))) LIMIT 144800 ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLDumpStatement stmt = (SQLDumpStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+dump-merge=true*/\n" +
                "DUMP DATA SELECT amp.buyer_add_cart_info.buyer_id, amp.buyer_add_cart_info.pre_score, amp.buyer_add_cart_info.cart_price\n" +
                "FROM amp.buyer_add_cart_info\n" +
                "\tJOIN amp.crm_user_base_info ON amp.crm_user_base_info.user_id = amp.buyer_add_cart_info.buyer_id\n" +
                "WHERE amp.buyer_add_cart_info.seller_id = 1921906956\n" +
                "\tAND amp.buyer_add_cart_info.auction_id = 562769960283\n" +
                "\tAND amp.buyer_add_cart_info.show_price >= 13300\n" +
                "LIMIT 144800", stmt.toString());
    }


}