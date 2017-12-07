package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_55 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "/* ///9fe39e72/ */SELECT fck1.fck_order_id, fck1.out_order_id, fck1.seller_nick, fck1.buyer_nick, fck1.seller_id, fck1.buyer_id, fck1.auction_id, fck1.auction_title, fck1.auction_price, fck1.buy_amount, fck1.fck_type, fck1.sub_fck_type, fck1.fail_reason, fck1.pay_status, fck1.logistics_status, fck1.out_trade_status, fck1.snap_path, fck1.gmt_create, fck1.status, ifnull(fck1.buyer_rate_status, 4) AS buyer_rate_status, ifnull(fck1.seller_rate_status, 4) AS seller_rate_status, fck1.auction_pict_url, fck1.seller_memo, fck1.buyer_memo, fck1.seller_flag, fck1.buyer_flag, fck1.buyer_message_path, fck1.refund_status, fck1.attributes, fck1.attributes_cc, fck1.gmt_modified, fck1.ip, fck1.end_time, fck1.pay_time, fck1.is_main, fck1.is_detail, fck1.point_rate, fck1.parent_id, fck1.adjust_fee, fck1.discount_fee, fck1.refund_fee, fck1.confirm_paid_fee, fck1.cod_status, fck1.trade_tag, fck1.shop_id, fck1.options, fck1.ignore_sold_quantity, fck1.from_group, fck1.gmt_create_tc FROM (SELECT fck_order_id FROM tc_fck_order AS tc_fck_order IGNORE INDEX (ind_fck_order_status, idx_sid_bt_st_im) WHERE is_main = 1 AND (fck_type = 2700 OR fck_type = 200 OR fck_type = 10000 OR fck_type = 610 OR fck_type = 600 OR fck_type = 300 OR fck_type = 100 OR fck_type = 500 OR fck_type = 900 OR fck_type = 620 OR fck_type = 630 OR fck_type = 650 OR fck_type = 1000 OR fck_type = 1001 OR fck_type = 710 OR fck_type = 1200 OR fck_type = 1201 OR fck_type = 1500 OR fck_type = 1600 OR fck_type = 2000 OR fck_type = 2400) AND seller_id = 2169996291 AND (sub_fck_type = 1 OR sub_fck_type = 901 OR sub_fck_type = 801 OR sub_fck_type = 701 OR sub_fck_type = 601 OR sub_fck_type = 2) AND status = 0 AND gmt_create >= DATE_FORMAT('2017-04-22 00:00:00', '%Y-%m-%d %T') AND gmt_create <= DATE_FORMAT('2017-05-07 23:59:59', '%Y-%m-%d %T') AND from_group = 0 ORDER BY gmt_create DESC LIMIT 900, 100) AS fck2, tc_fck_order AS fck1 WHERE fck2.fck_order_id = fck1.fck_order_id";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        statement.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);


        assertEquals("SELECT fck1.fck_order_id, fck1.out_order_id, fck1.seller_nick, fck1.buyer_nick, fck1.seller_id\n" +
                "\t, fck1.buyer_id, fck1.auction_id, fck1.auction_title, fck1.auction_price, fck1.buy_amount\n" +
                "\t, fck1.fck_type, fck1.sub_fck_type, fck1.fail_reason, fck1.pay_status, fck1.logistics_status\n" +
                "\t, fck1.out_trade_status, fck1.snap_path, fck1.gmt_create, fck1.status\n" +
                "\t, ifnull(fck1.buyer_rate_status, ?) AS buyer_rate_status\n" +
                "\t, ifnull(fck1.seller_rate_status, ?) AS seller_rate_status, fck1.auction_pict_url\n" +
                "\t, fck1.seller_memo, fck1.buyer_memo, fck1.seller_flag, fck1.buyer_flag, fck1.buyer_message_path\n" +
                "\t, fck1.refund_status, fck1.attributes, fck1.attributes_cc, fck1.gmt_modified, fck1.ip\n" +
                "\t, fck1.end_time, fck1.pay_time, fck1.is_main, fck1.is_detail, fck1.point_rate\n" +
                "\t, fck1.parent_id, fck1.adjust_fee, fck1.discount_fee, fck1.refund_fee, fck1.confirm_paid_fee\n" +
                "\t, fck1.cod_status, fck1.trade_tag, fck1.shop_id, fck1.options, fck1.ignore_sold_quantity\n" +
                "\t, fck1.from_group, fck1.gmt_create_tc\n" +
                "FROM (\n" +
                "\tSELECT fck_order_id\n" +
                "\tFROM tc_fck_order tc_fck_order IGNORE INDEX (ind_fck_order_status, idx_sid_bt_st_im)\n" +
                "\tWHERE is_main = ?\n" +
                "\t\tAND (fck_type = ?)\n" +
                "\t\tAND seller_id = ?\n" +
                "\t\tAND (sub_fck_type = ?)\n" +
                "\t\tAND status = ?\n" +
                "\t\tAND gmt_create >= DATE_FORMAT(?, '%Y-%m-%d %T')\n" +
                "\t\tAND gmt_create <= DATE_FORMAT(?, '%Y-%m-%d %T')\n" +
                "\t\tAND from_group = ?\n" +
                "\tORDER BY gmt_create DESC\n" +
                "\tLIMIT ?, ?\n" +
                ") fck2, tc_fck_order fck1\n" +
                "WHERE fck2.fck_order_id = fck1.fck_order_id", psql);
    }
}
