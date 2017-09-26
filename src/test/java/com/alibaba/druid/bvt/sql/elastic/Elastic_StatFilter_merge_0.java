package com.alibaba.druid.bvt.sql.elastic;

import com.alibaba.druid.filter.stat.MergeStatFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Elastic_StatFilter_merge_0 extends TestCase {
    public void test_for_stat_merge() throws Exception {
        String sql = "/* ///9fe39e72/ */SELECT fkc1.fkc_order_id, fkc1.out_order_id, fkc1.seller_nick, fkc1.buyer_nick, fkc1.seller_id, fkc1.buyer_id, fkc1.auction_id, fkc1.auction_title, fkc1.auction_price, fkc1.buy_amount, fkc1.fkc_type, fkc1.sub_fkc_type, fkc1.fail_reason, fkc1.pay_status, fkc1.logistics_status, fkc1.out_trade_status, fkc1.snap_path, fkc1.gmt_create, fkc1.status, ifnull(fkc1.buyer_rate_status, 4) AS buyer_rate_status, ifnull(fkc1.seller_rate_status, 4) AS seller_rate_status, fkc1.auction_pict_url, fkc1.seller_memo, fkc1.buyer_memo, fkc1.seller_flag, fkc1.buyer_flag, fkc1.buyer_message_path, fkc1.refund_status, fkc1.attributes, fkc1.attributes_cc, fkc1.gmt_modified, fkc1.ip, fkc1.end_time, fkc1.pay_time, fkc1.is_main, fkc1.is_detail, fkc1.point_rate, fkc1.parent_id, fkc1.adjust_fee, fkc1.discount_fee, fkc1.refund_fee, fkc1.confirm_paid_fee, fkc1.cod_status, fkc1.trade_tag, fkc1.shop_id, fkc1.options, fkc1.ignore_sold_quantity, fkc1.from_group, fkc1.gmt_create_tc FROM (SELECT fkc_order_id FROM tc_fkc_order AS tc_fkc_order IGNORE INDEX (ind_fkc_order_status, idx_sid_bt_st_im) WHERE is_main = 1 AND (fkc_type = 2700 OR fkc_type = 200 OR fkc_type = 10000 OR fkc_type = 610 OR fkc_type = 600 OR fkc_type = 300 OR fkc_type = 100 OR fkc_type = 500 OR fkc_type = 900 OR fkc_type = 620 OR fkc_type = 630 OR fkc_type = 650 OR fkc_type = 1000 OR fkc_type = 1001 OR fkc_type = 710 OR fkc_type = 1200 OR fkc_type = 1201 OR fkc_type = 1500 OR fkc_type = 1600 OR fkc_type = 2000 OR fkc_type = 2400) AND seller_id = 2169996291 AND (sub_fkc_type = 1 OR sub_fkc_type = 901 OR sub_fkc_type = 801 OR sub_fkc_type = 701 OR sub_fkc_type = 601 OR sub_fkc_type = 2) AND status = 0 AND gmt_create >= DATE_FORMAT('2017-04-22 00:00:00', '%Y-%m-%d %T') AND gmt_create <= DATE_FORMAT('2017-05-07 23:59:59', '%Y-%m-%d %T') AND from_group = 0 ORDER BY gmt_create DESC LIMIT 900, 100) AS fkc2, tc_fkc_order AS fkc1 WHERE fkc2.fkc_order_id = 1";
        StatFilter filter = new MergeStatFilter();
        filter.setDbType(JdbcConstants.ELASTIC_SEARCH);
        String mergedSql = filter.mergeSql(sql, JdbcConstants.ELASTIC_SEARCH);
        assertEquals("SELECT fkc1.fkc_order_id, fkc1.out_order_id, fkc1.seller_nick, fkc1.buyer_nick, fkc1.seller_id\n" +
                "\t, fkc1.buyer_id, fkc1.auction_id, fkc1.auction_title, fkc1.auction_price, fkc1.buy_amount\n" +
                "\t, fkc1.fkc_type, fkc1.sub_fkc_type, fkc1.fail_reason, fkc1.pay_status, fkc1.logistics_status\n" +
                "\t, fkc1.out_trade_status, fkc1.snap_path, fkc1.gmt_create, fkc1.status\n" +
                "\t, ifnull(fkc1.buyer_rate_status, ?) AS buyer_rate_status\n" +
                "\t, ifnull(fkc1.seller_rate_status, ?) AS seller_rate_status, fkc1.auction_pict_url\n" +
                "\t, fkc1.seller_memo, fkc1.buyer_memo, fkc1.seller_flag, fkc1.buyer_flag, fkc1.buyer_message_path\n" +
                "\t, fkc1.refund_status, fkc1.attributes, fkc1.attributes_cc, fkc1.gmt_modified, fkc1.ip\n" +
                "\t, fkc1.end_time, fkc1.pay_time, fkc1.is_main, fkc1.is_detail, fkc1.point_rate\n" +
                "\t, fkc1.parent_id, fkc1.adjust_fee, fkc1.discount_fee, fkc1.refund_fee, fkc1.confirm_paid_fee\n" +
                "\t, fkc1.cod_status, fkc1.trade_tag, fkc1.shop_id, fkc1.options, fkc1.ignore_sold_quantity\n" +
                "\t, fkc1.from_group, fkc1.gmt_create_tc\n" +
                "FROM (\n" +
                "\tSELECT fkc_order_id\n" +
                "\tFROM tc_fkc_order tc_fkc_order IGNORE INDEX (ind_fkc_order_status, idx_sid_bt_st_im)\n" +
                "\tWHERE is_main = ?\n" +
                "\t\tAND (fkc_type = ?)\n" +
                "\t\tAND seller_id = ?\n" +
                "\t\tAND (sub_fkc_type = ?)\n" +
                "\t\tAND status = ?\n" +
                "\t\tAND gmt_create >= DATE_FORMAT(?, '%Y-%m-%d %T')\n" +
                "\t\tAND gmt_create <= DATE_FORMAT(?, '%Y-%m-%d %T')\n" +
                "\t\tAND from_group = ?\n" +
                "\tORDER BY gmt_create DESC\n" +
                "\tLIMIT ?, ?\n" +
                ") fkc2, tc_fkc_order fkc1\n" +
                "WHERE fkc2.fkc_order_id = ?", mergedSql);
    }
}
