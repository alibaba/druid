package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.fastjson.JSON;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_159 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT COUNT(*) AS COUNT \n" +
                "FROM ( \n" +
                "\tSELECT dcm.name AS 渠道, 大组, dcm1.name AS 城市, 供应商ID, 供应商名称  , 品牌ID, 品牌名称, round(成本汇总.订单实付 / 100, 6) AS 订单实付, round(成本汇总.理论成交 / 100, 6) AS 理论成交, round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 100, 6) AS 折前毛利  , concat(round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 成本汇总.理论成交 * 100, 6), '%') AS 折前毛利率, round(成本汇总.猫超承担店铺优惠券金额 / 100, 6) AS 猫超承担店铺优惠券金额, concat(round(成本汇总.猫超承担店铺优惠券金额 / 成本汇总.理论成交, 6), '%') AS 店铺优惠券占比, round(成本汇总.猫超承担店铺优惠券其他金额 / 100, 6) AS 猫超承担店铺优惠券其他金额, concat(round(成本汇总.猫超承担店铺优惠券其他金额 / 成本汇总.理论成交, 6), '%') AS 店铺优惠其他占比  , round(成本汇总.猫超承担跨店优惠非金本位 / 100, 6) AS 猫超承担跨店优惠非金本位, concat(round(成本汇总.猫超承担跨店优惠非金本位 / 成本汇总.理论成交 * 100, 6), '%') AS 跨店优惠非金本位占比, round(成本汇总.猫超承担跨店优惠金本位 / 100, 6) AS 猫超承担跨店优惠金本位, concat(round(成本汇总.猫超承担跨店优惠金本位 / 成本汇总.理论成交 * 100, 6), '%') AS 跨店优惠金本位占比, round(成本汇总.超级会员折扣 / 100, 6) AS 超级会员折扣  , concat(round(成本汇总.超级会员折扣 / 成本汇总.理论成交 * 100, 6), '%') AS 超级会员折扣占比, round(成本汇总.猫超承担补贴金额 / 100, 6) AS 猫超承担补贴金额, concat(round(成本汇总.猫超承担补贴金额 / 成本汇总.理论成交 * 100, 6), '%') AS 补贴占比, round(成本汇总.折后毛利 / 100, 6) AS 折后毛利, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * 100, 6), '%') AS 折后毛利率  , 物流成本, 物流成本占比, round(成本汇总.折后毛利 / 100, 6) AS 运营毛利, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * 100, 6), '%') AS 运营毛利率, round(成本汇总.物流收入 / 100, 6) AS 物流收入  , round(成本汇总.淘客收入 / 100, 6) AS 淘客收入, 免费货收入, round((成本汇总.折后毛利 + 成本汇总.物流收入) / 100, 6) AS 综合毛利, concat(round((成本汇总.折后毛利 + 成本汇总.物流收入) / 成本汇总.理论成交 * 100, 6), '%') AS 综合毛利率, round(成本汇总.正向分账金额 / 100, 6) AS 正向分账金额  , round(成本汇总.逆向分账金额 / 100, 6) AS 逆向分账金额, round(成本汇总.正向营销费用 / 100, 6) AS 正向营销费用, round(成本汇总.逆向营销费用 / 100, 6) AS 逆向营销费用 \n" +
                "FROM (\n" +
                "\tSELECT CASE WHEN '0' <> '0' THEN channel ELSE CAST(-1 AS bigint) END AS 渠道\n" +
                "\t\t, CASE WHEN '0' <> '0' THEN group_name ELSE '-' END AS 大组\n" +
                "\t\t, CASE WHEN '0' <> '0' THEN city ELSE '-1' END AS 城市\n" +
                "\t\t, CASE WHEN '0' <> '0' THEN supplier_code ELSE '-' END AS 供应商ID\n" +
                "\t\t, CASE WHEN '0' <> '0' THEN supplier_name ELSE '-' END AS 供应商名称   \n" +
                "\t\t, CASE WHEN '0' <> '0' THEN CAST(brand_id AS bigint) ELSE CAST(-1 AS bigint) END AS 品牌ID\n" +
                "\t\t, CASE WHEN '0' <> '0' THEN brand_name ELSE '-' END AS 品牌名称, SUM(abs(trade_paid_money) - abs(refund_paid_money)) AS 订单实付, SUM(abs(trade_paid_money) - abs(refund_paid_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + abs(trade_susidy_fee) - abs(refund_susidy_fee) + abs(trade_shop_coupon_supp_burden_fee) - abs(refund_shop_coupon_supp_burden_fee) + abs(trade_shop_coupon_other_supp_burden_fee) - abs(refund_shop_coupon_other_supp_burden_fee) + abs(trade_tmall_coupon_not_gold_supp_burden_fee) - abs(refund_tmall_coupon_not_gold_supp_burden_fee) + abs(trade_tmall_coupon_gold1_supp_burden_fee) - abs(refund_tmall_coupon_gold1_supp_burden_fee) + abs(trade_tmall_coupon_gold2_supp_burden_fee) - abs(refund_tmall_coupon_gold2_supp_burden_fee) + (abs(trade_tmall_vip_supp_burden_fee) - abs(refund_tmall_vip_supp_burden_fee))) AS 理论成交, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + (abs(trade_susidy_fee) - abs(refund_susidy_fee))) AS 折前毛利   , SUM(abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee)) AS 猫超承担店铺优惠券金额, SUM(abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee)) AS 猫超承担店铺优惠券其他金额, SUM(abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee)) AS 猫超承担跨店优惠非金本位, SUM(abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee)) AS 猫超承担跨店优惠金本位, SUM(abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee)) AS 超级会员折扣   , SUM(abs(trade_susidy_fee) - abs(refund_susidy_fee)) AS 猫超承担补贴金额, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money)) AS 折后毛利, '-' AS 物流成本, '-' AS 物流成本占比, SUM(abs(trade_postfee_share)) AS 物流收入   , SUM(abs(taoke_trade_money) - abs(taoke_refund_money)) AS 淘客收入, '-' AS 免费货收入, SUM(trade_payment_amount) AS 正向分账金额, SUM(refund_payment_amount) AS 逆向分账金额, SUM(trade_marketing_fee) AS 正向营销费用   , SUM(refund_marketing_fee) AS 逆向营销费用  \n" +
                "FROM dws_ascm_cost_di  WHERE 1 = 1   AND 1 = 1   AND 1 = 1   AND 1 = 1   AND 1 = 1   AND brand_id = '119079'   AND stat_date >= '20170401'   AND stat_date <= '20180228'  GROUP BY 1, CASE WHEN '0' <> '0' THEN channel ELSE CAST(-1 AS bigint) END, CASE WHEN '0' <> '0' THEN group_name ELSE '-' END, CASE WHEN '0' <> '0' THEN city ELSE '-1' END, CASE WHEN '0' <> '0' THEN supplier_code ELSE '-' END, CASE WHEN '0' <> '0' THEN supplier_name ELSE '-' END, CASE WHEN '0' <> '0' THEN CAST(brand_id AS bigint) ELSE CAST(-1 AS bigint) END, CASE WHEN '0' <> '0' THEN brand_name ELSE '-' END  ) 成本汇总  LEFT JOIN dim_channel_maochao dcm ON 成本汇总.渠道 = dcm.id  LEFT JOIN dim_city_maochao dcm1 ON CAST(成本汇总.城市 AS bigint) = dcm1.id ) quark_t1";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT COUNT(*) AS COUNT\n" +
                "FROM (\n" +
                "\tSELECT dcm.name AS 渠道, 大组, dcm1.name AS 城市, 供应商ID, 供应商名称\n" +
                "\t\t, 品牌ID, 品牌名称\n" +
                "\t\t, round(成本汇总.订单实付 / 100, 6) AS 订单实付\n" +
                "\t\t, round(成本汇总.理论成交 / 100, 6) AS 理论成交\n" +
                "\t\t, round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 100, 6) AS 折前毛利\n" +
                "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 成本汇总.理论成交 * 100, 6), '%') AS 折前毛利率\n" +
                "\t\t, round(成本汇总.猫超承担店铺优惠券金额 / 100, 6) AS 猫超承担店铺优惠券金额\n" +
                "\t\t, concat(round(成本汇总.猫超承担店铺优惠券金额 / 成本汇总.理论成交, 6), '%') AS 店铺优惠券占比\n" +
                "\t\t, round(成本汇总.猫超承担店铺优惠券其他金额 / 100, 6) AS 猫超承担店铺优惠券其他金额\n" +
                "\t\t, concat(round(成本汇总.猫超承担店铺优惠券其他金额 / 成本汇总.理论成交, 6), '%') AS 店铺优惠其他占比\n" +
                "\t\t, round(成本汇总.猫超承担跨店优惠非金本位 / 100, 6) AS 猫超承担跨店优惠非金本位\n" +
                "\t\t, concat(round(成本汇总.猫超承担跨店优惠非金本位 / 成本汇总.理论成交 * 100, 6), '%') AS 跨店优惠非金本位占比\n" +
                "\t\t, round(成本汇总.猫超承担跨店优惠金本位 / 100, 6) AS 猫超承担跨店优惠金本位\n" +
                "\t\t, concat(round(成本汇总.猫超承担跨店优惠金本位 / 成本汇总.理论成交 * 100, 6), '%') AS 跨店优惠金本位占比\n" +
                "\t\t, round(成本汇总.超级会员折扣 / 100, 6) AS 超级会员折扣\n" +
                "\t\t, concat(round(成本汇总.超级会员折扣 / 成本汇总.理论成交 * 100, 6), '%') AS 超级会员折扣占比\n" +
                "\t\t, round(成本汇总.猫超承担补贴金额 / 100, 6) AS 猫超承担补贴金额\n" +
                "\t\t, concat(round(成本汇总.猫超承担补贴金额 / 成本汇总.理论成交 * 100, 6), '%') AS 补贴占比\n" +
                "\t\t, round(成本汇总.折后毛利 / 100, 6) AS 折后毛利\n" +
                "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * 100, 6), '%') AS 折后毛利率\n" +
                "\t\t, 物流成本, 物流成本占比\n" +
                "\t\t, round(成本汇总.折后毛利 / 100, 6) AS 运营毛利\n" +
                "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * 100, 6), '%') AS 运营毛利率\n" +
                "\t\t, round(成本汇总.物流收入 / 100, 6) AS 物流收入\n" +
                "\t\t, round(成本汇总.淘客收入 / 100, 6) AS 淘客收入\n" +
                "\t\t, 免费货收入\n" +
                "\t\t, round((成本汇总.折后毛利 + 成本汇总.物流收入) / 100, 6) AS 综合毛利\n" +
                "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.物流收入) / 成本汇总.理论成交 * 100, 6), '%') AS 综合毛利率\n" +
                "\t\t, round(成本汇总.正向分账金额 / 100, 6) AS 正向分账金额\n" +
                "\t\t, round(成本汇总.逆向分账金额 / 100, 6) AS 逆向分账金额\n" +
                "\t\t, round(成本汇总.正向营销费用 / 100, 6) AS 正向营销费用\n" +
                "\t\t, round(成本汇总.逆向营销费用 / 100, 6) AS 逆向营销费用\n" +
                "\tFROM (\n" +
                "\t\tSELECT CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN channel\n" +
                "\t\t\t\tELSE CAST(-1 AS bigint)\n" +
                "\t\t\tEND AS 渠道\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN group_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND AS 大组\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN city\n" +
                "\t\t\t\tELSE '-1'\n" +
                "\t\t\tEND AS 城市\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN supplier_code\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND AS 供应商ID\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN supplier_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND AS 供应商名称\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN CAST(brand_id AS bigint)\n" +
                "\t\t\t\tELSE CAST(-1 AS bigint)\n" +
                "\t\t\tEND AS 品牌ID\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN brand_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND AS 品牌名称\n" +
                "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money)) AS 订单实付\n" +
                "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + abs(trade_susidy_fee) - abs(refund_susidy_fee) + abs(trade_shop_coupon_supp_burden_fee) - abs(refund_shop_coupon_supp_burden_fee) + abs(trade_shop_coupon_other_supp_burden_fee) - abs(refund_shop_coupon_other_supp_burden_fee) + abs(trade_tmall_coupon_not_gold_supp_burden_fee) - abs(refund_tmall_coupon_not_gold_supp_burden_fee) + abs(trade_tmall_coupon_gold1_supp_burden_fee) - abs(refund_tmall_coupon_gold1_supp_burden_fee) + abs(trade_tmall_coupon_gold2_supp_burden_fee) - abs(refund_tmall_coupon_gold2_supp_burden_fee) + (abs(trade_tmall_vip_supp_burden_fee) - abs(refund_tmall_vip_supp_burden_fee))) AS 理论成交\n" +
                "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + (abs(trade_susidy_fee) - abs(refund_susidy_fee))) AS 折前毛利\n" +
                "\t\t\t, SUM(abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee)) AS 猫超承担店铺优惠券金额\n" +
                "\t\t\t, SUM(abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee)) AS 猫超承担店铺优惠券其他金额\n" +
                "\t\t\t, SUM(abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee)) AS 猫超承担跨店优惠非金本位\n" +
                "\t\t\t, SUM(abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee)) AS 猫超承担跨店优惠金本位\n" +
                "\t\t\t, SUM(abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee)) AS 超级会员折扣\n" +
                "\t\t\t, SUM(abs(trade_susidy_fee) - abs(refund_susidy_fee)) AS 猫超承担补贴金额\n" +
                "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money)) AS 折后毛利\n" +
                "\t\t\t, '-' AS 物流成本, '-' AS 物流成本占比, SUM(abs(trade_postfee_share)) AS 物流收入\n" +
                "\t\t\t, SUM(abs(taoke_trade_money) - abs(taoke_refund_money)) AS 淘客收入\n" +
                "\t\t\t, '-' AS 免费货收入, SUM(trade_payment_amount) AS 正向分账金额, SUM(refund_payment_amount) AS 逆向分账金额\n" +
                "\t\t\t, SUM(trade_marketing_fee) AS 正向营销费用, SUM(refund_marketing_fee) AS 逆向营销费用\n" +
                "\t\tFROM dws_ascm_cost_di\n" +
                "\t\tWHERE 1 = 1\n" +
                "\t\t\tAND 1 = 1\n" +
                "\t\t\tAND 1 = 1\n" +
                "\t\t\tAND 1 = 1\n" +
                "\t\t\tAND 1 = 1\n" +
                "\t\t\tAND brand_id = '119079'\n" +
                "\t\t\tAND stat_date >= '20170401'\n" +
                "\t\t\tAND stat_date <= '20180228'\n" +
                "\t\tGROUP BY 1, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN channel\n" +
                "\t\t\t\tELSE CAST(-1 AS bigint)\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN group_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN city\n" +
                "\t\t\t\tELSE '-1'\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN supplier_code\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN supplier_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN CAST(brand_id AS bigint)\n" +
                "\t\t\t\tELSE CAST(-1 AS bigint)\n" +
                "\t\t\tEND, CASE \n" +
                "\t\t\t\tWHEN '0' <> '0' THEN brand_name\n" +
                "\t\t\t\tELSE '-'\n" +
                "\t\t\tEND\n" +
                "\t) 成本汇总\n" +
                "\t\tLEFT JOIN dim_channel_maochao dcm ON 成本汇总.渠道 = dcm.id\n" +
                "\t\tLEFT JOIN dim_city_maochao dcm1 ON CAST(成本汇总.城市 AS bigint) = dcm1.id\n" +
                ") quark_t1", stmt.toString());

        assertEquals("SELECT COUNT(*) AS COUNT\n" +
                        "FROM (\n" +
                        "\tSELECT dcm.name AS 渠道, 大组, dcm1.name AS 城市, 供应商ID, 供应商名称\n" +
                        "\t\t, 品牌ID, 品牌名称\n" +
                        "\t\t, round(成本汇总.订单实付 / ?, ?) AS 订单实付\n" +
                        "\t\t, round(成本汇总.理论成交 / ?, ?) AS 理论成交\n" +
                        "\t\t, round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / ?, ?) AS 折前毛利\n" +
                        "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 成本汇总.理论成交 * ?, ?), ?) AS 折前毛利率\n" +
                        "\t\t, round(成本汇总.猫超承担店铺优惠券金额 / ?, ?) AS 猫超承担店铺优惠券金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担店铺优惠券金额 / 成本汇总.理论成交, ?), ?) AS 店铺优惠券占比\n" +
                        "\t\t, round(成本汇总.猫超承担店铺优惠券其他金额 / ?, ?) AS 猫超承担店铺优惠券其他金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担店铺优惠券其他金额 / 成本汇总.理论成交, ?), ?) AS 店铺优惠其他占比\n" +
                        "\t\t, round(成本汇总.猫超承担跨店优惠非金本位 / ?, ?) AS 猫超承担跨店优惠非金本位\n" +
                        "\t\t, concat(round(成本汇总.猫超承担跨店优惠非金本位 / 成本汇总.理论成交 * ?, ?), ?) AS 跨店优惠非金本位占比\n" +
                        "\t\t, round(成本汇总.猫超承担跨店优惠金本位 / ?, ?) AS 猫超承担跨店优惠金本位\n" +
                        "\t\t, concat(round(成本汇总.猫超承担跨店优惠金本位 / 成本汇总.理论成交 * ?, ?), ?) AS 跨店优惠金本位占比\n" +
                        "\t\t, round(成本汇总.超级会员折扣 / ?, ?) AS 超级会员折扣\n" +
                        "\t\t, concat(round(成本汇总.超级会员折扣 / 成本汇总.理论成交 * ?, ?), ?) AS 超级会员折扣占比\n" +
                        "\t\t, round(成本汇总.猫超承担补贴金额 / ?, ?) AS 猫超承担补贴金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担补贴金额 / 成本汇总.理论成交 * ?, ?), ?) AS 补贴占比\n" +
                        "\t\t, round(成本汇总.折后毛利 / ?, ?) AS 折后毛利\n" +
                        "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * ?, ?), ?) AS 折后毛利率\n" +
                        "\t\t, 物流成本, 物流成本占比\n" +
                        "\t\t, round(成本汇总.折后毛利 / ?, ?) AS 运营毛利\n" +
                        "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * ?, ?), ?) AS 运营毛利率\n" +
                        "\t\t, round(成本汇总.物流收入 / ?, ?) AS 物流收入\n" +
                        "\t\t, round(成本汇总.淘客收入 / ?, ?) AS 淘客收入\n" +
                        "\t\t, 免费货收入\n" +
                        "\t\t, round((成本汇总.折后毛利 + 成本汇总.物流收入) / ?, ?) AS 综合毛利\n" +
                        "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.物流收入) / 成本汇总.理论成交 * ?, ?), ?) AS 综合毛利率\n" +
                        "\t\t, round(成本汇总.正向分账金额 / ?, ?) AS 正向分账金额\n" +
                        "\t\t, round(成本汇总.逆向分账金额 / ?, ?) AS 逆向分账金额\n" +
                        "\t\t, round(成本汇总.正向营销费用 / ?, ?) AS 正向营销费用\n" +
                        "\t\t, round(成本汇总.逆向营销费用 / ?, ?) AS 逆向营销费用\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN channel\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND AS 渠道\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN group_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 大组\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN city\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 城市\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_code\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 供应商ID\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 供应商名称\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN CAST(brand_id AS bigint)\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND AS 品牌ID\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN brand_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 品牌名称\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money)) AS 订单实付\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + abs(trade_susidy_fee) - abs(refund_susidy_fee) + abs(trade_shop_coupon_supp_burden_fee) - abs(refund_shop_coupon_supp_burden_fee) + abs(trade_shop_coupon_other_supp_burden_fee) - abs(refund_shop_coupon_other_supp_burden_fee) + abs(trade_tmall_coupon_not_gold_supp_burden_fee) - abs(refund_tmall_coupon_not_gold_supp_burden_fee) + abs(trade_tmall_coupon_gold1_supp_burden_fee) - abs(refund_tmall_coupon_gold1_supp_burden_fee) + abs(trade_tmall_coupon_gold2_supp_burden_fee) - abs(refund_tmall_coupon_gold2_supp_burden_fee) + (abs(trade_tmall_vip_supp_burden_fee) - abs(refund_tmall_vip_supp_burden_fee))) AS 理论成交\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + (abs(trade_susidy_fee) - abs(refund_susidy_fee))) AS 折前毛利\n" +
                        "\t\t\t, SUM(abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee)) AS 猫超承担店铺优惠券金额\n" +
                        "\t\t\t, SUM(abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee)) AS 猫超承担店铺优惠券其他金额\n" +
                        "\t\t\t, SUM(abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee)) AS 猫超承担跨店优惠非金本位\n" +
                        "\t\t\t, SUM(abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee)) AS 猫超承担跨店优惠金本位\n" +
                        "\t\t\t, SUM(abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee)) AS 超级会员折扣\n" +
                        "\t\t\t, SUM(abs(trade_susidy_fee) - abs(refund_susidy_fee)) AS 猫超承担补贴金额\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money)) AS 折后毛利\n" +
                        "\t\t\t, ? AS 物流成本, ? AS 物流成本占比, SUM(abs(trade_postfee_share)) AS 物流收入\n" +
                        "\t\t\t, SUM(abs(taoke_trade_money) - abs(taoke_refund_money)) AS 淘客收入\n" +
                        "\t\t\t, ? AS 免费货收入, SUM(trade_payment_amount) AS 正向分账金额, SUM(refund_payment_amount) AS 逆向分账金额\n" +
                        "\t\t\t, SUM(trade_marketing_fee) AS 正向营销费用, SUM(refund_marketing_fee) AS 逆向营销费用\n" +
                        "\t\tFROM dws_ascm_cost_di\n" +
                        "\t\tWHERE 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND brand_id = ?\n" +
                        "\t\t\tAND stat_date >= ?\n" +
                        "\t\t\tAND stat_date <= ?\n" +
                        "\t\tGROUP BY 1, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN channel\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN group_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN city\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_code\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN CAST(brand_id AS bigint)\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN brand_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND\n" +
                        "\t) 成本汇总\n" +
                        "\t\tLEFT JOIN dim_channel_maochao dcm ON 成本汇总.渠道 = dcm.id\n" +
                        "\t\tLEFT JOIN dim_city_maochao dcm1 ON CAST(成本汇总.城市 AS bigint) = dcm1.id\n" +
                        ") quark_t1"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        List<Object> params = new ArrayList<Object>();
        assertEquals("SELECT COUNT(*) AS COUNT\n" +
                        "FROM (\n" +
                        "\tSELECT dcm.name AS 渠道, 大组, dcm1.name AS 城市, 供应商ID, 供应商名称\n" +
                        "\t\t, 品牌ID, 品牌名称\n" +
                        "\t\t, round(成本汇总.订单实付 / ?, ?) AS 订单实付\n" +
                        "\t\t, round(成本汇总.理论成交 / ?, ?) AS 理论成交\n" +
                        "\t\t, round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / ?, ?) AS 折前毛利\n" +
                        "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.猫超承担店铺优惠券金额 + 成本汇总.猫超承担店铺优惠券其他金额 + 成本汇总.猫超承担跨店优惠非金本位 + 成本汇总.猫超承担跨店优惠金本位 + 成本汇总.超级会员折扣 + 成本汇总.猫超承担补贴金额) / 成本汇总.理论成交 * ?, ?), ?) AS 折前毛利率\n" +
                        "\t\t, round(成本汇总.猫超承担店铺优惠券金额 / ?, ?) AS 猫超承担店铺优惠券金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担店铺优惠券金额 / 成本汇总.理论成交, ?), ?) AS 店铺优惠券占比\n" +
                        "\t\t, round(成本汇总.猫超承担店铺优惠券其他金额 / ?, ?) AS 猫超承担店铺优惠券其他金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担店铺优惠券其他金额 / 成本汇总.理论成交, ?), ?) AS 店铺优惠其他占比\n" +
                        "\t\t, round(成本汇总.猫超承担跨店优惠非金本位 / ?, ?) AS 猫超承担跨店优惠非金本位\n" +
                        "\t\t, concat(round(成本汇总.猫超承担跨店优惠非金本位 / 成本汇总.理论成交 * ?, ?), ?) AS 跨店优惠非金本位占比\n" +
                        "\t\t, round(成本汇总.猫超承担跨店优惠金本位 / ?, ?) AS 猫超承担跨店优惠金本位\n" +
                        "\t\t, concat(round(成本汇总.猫超承担跨店优惠金本位 / 成本汇总.理论成交 * ?, ?), ?) AS 跨店优惠金本位占比\n" +
                        "\t\t, round(成本汇总.超级会员折扣 / ?, ?) AS 超级会员折扣\n" +
                        "\t\t, concat(round(成本汇总.超级会员折扣 / 成本汇总.理论成交 * ?, ?), ?) AS 超级会员折扣占比\n" +
                        "\t\t, round(成本汇总.猫超承担补贴金额 / ?, ?) AS 猫超承担补贴金额\n" +
                        "\t\t, concat(round(成本汇总.猫超承担补贴金额 / 成本汇总.理论成交 * ?, ?), ?) AS 补贴占比\n" +
                        "\t\t, round(成本汇总.折后毛利 / ?, ?) AS 折后毛利\n" +
                        "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * ?, ?), ?) AS 折后毛利率\n" +
                        "\t\t, 物流成本, 物流成本占比\n" +
                        "\t\t, round(成本汇总.折后毛利 / ?, ?) AS 运营毛利\n" +
                        "\t\t, concat(round(成本汇总.折后毛利 / 成本汇总.理论成交 * ?, ?), ?) AS 运营毛利率\n" +
                        "\t\t, round(成本汇总.物流收入 / ?, ?) AS 物流收入\n" +
                        "\t\t, round(成本汇总.淘客收入 / ?, ?) AS 淘客收入\n" +
                        "\t\t, 免费货收入\n" +
                        "\t\t, round((成本汇总.折后毛利 + 成本汇总.物流收入) / ?, ?) AS 综合毛利\n" +
                        "\t\t, concat(round((成本汇总.折后毛利 + 成本汇总.物流收入) / 成本汇总.理论成交 * ?, ?), ?) AS 综合毛利率\n" +
                        "\t\t, round(成本汇总.正向分账金额 / ?, ?) AS 正向分账金额\n" +
                        "\t\t, round(成本汇总.逆向分账金额 / ?, ?) AS 逆向分账金额\n" +
                        "\t\t, round(成本汇总.正向营销费用 / ?, ?) AS 正向营销费用\n" +
                        "\t\t, round(成本汇总.逆向营销费用 / ?, ?) AS 逆向营销费用\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN channel\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND AS 渠道\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN group_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 大组\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN city\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 城市\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_code\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 供应商ID\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 供应商名称\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN CAST(brand_id AS bigint)\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND AS 品牌ID\n" +
                        "\t\t\t, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN brand_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND AS 品牌名称\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money)) AS 订单实付\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + abs(trade_susidy_fee) - abs(refund_susidy_fee) + abs(trade_shop_coupon_supp_burden_fee) - abs(refund_shop_coupon_supp_burden_fee) + abs(trade_shop_coupon_other_supp_burden_fee) - abs(refund_shop_coupon_other_supp_burden_fee) + abs(trade_tmall_coupon_not_gold_supp_burden_fee) - abs(refund_tmall_coupon_not_gold_supp_burden_fee) + abs(trade_tmall_coupon_gold1_supp_burden_fee) - abs(refund_tmall_coupon_gold1_supp_burden_fee) + abs(trade_tmall_coupon_gold2_supp_burden_fee) - abs(refund_tmall_coupon_gold2_supp_burden_fee) + (abs(trade_tmall_vip_supp_burden_fee) - abs(refund_tmall_vip_supp_burden_fee))) AS 理论成交\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money) + abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee) + abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee) + abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee) + abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee) + abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee) + (abs(trade_susidy_fee) - abs(refund_susidy_fee))) AS 折前毛利\n" +
                        "\t\t\t, SUM(abs(trade_shop_coupon_biz_burden_fee) - abs(refund_shop_coupon_biz_burden_fee)) AS 猫超承担店铺优惠券金额\n" +
                        "\t\t\t, SUM(abs(trade_shop_coupon_other_biz_burden_fee) - abs(refund_shop_coupon_other_biz_burden_fee)) AS 猫超承担店铺优惠券其他金额\n" +
                        "\t\t\t, SUM(abs(trade_tmall_coupon_not_gold_biz_burden_fee) - abs(refund_tmall_coupon_not_gold_biz_burden_fee)) AS 猫超承担跨店优惠非金本位\n" +
                        "\t\t\t, SUM(abs(trade_tmall_coupon_gold1_biz_burden_fee) - abs(refund_tmall_coupon_gold1_biz_burden_fee) + abs(trade_tmall_coupon_gold2_biz_burden_fee) - abs(refund_tmall_coupon_gold2_biz_burden_fee)) AS 猫超承担跨店优惠金本位\n" +
                        "\t\t\t, SUM(abs(trade_tmall_vip_biz_burden_fee) - abs(refund_tmall_vip_biz_burden_fee)) AS 超级会员折扣\n" +
                        "\t\t\t, SUM(abs(trade_susidy_fee) - abs(refund_susidy_fee)) AS 猫超承担补贴金额\n" +
                        "\t\t\t, SUM(abs(trade_paid_money) - abs(refund_paid_money) - abs(trade_payment_amount) + abs(trade_marketing_fee) + abs(taoke_trade_money) + abs(refund_payment_amount) - abs(refund_marketing_fee) - abs(taoke_refund_money)) AS 折后毛利\n" +
                        "\t\t\t, ? AS 物流成本, ? AS 物流成本占比, SUM(abs(trade_postfee_share)) AS 物流收入\n" +
                        "\t\t\t, SUM(abs(taoke_trade_money) - abs(taoke_refund_money)) AS 淘客收入\n" +
                        "\t\t\t, ? AS 免费货收入, SUM(trade_payment_amount) AS 正向分账金额, SUM(refund_payment_amount) AS 逆向分账金额\n" +
                        "\t\t\t, SUM(trade_marketing_fee) AS 正向营销费用, SUM(refund_marketing_fee) AS 逆向营销费用\n" +
                        "\t\tFROM dws_ascm_cost_di\n" +
                        "\t\tWHERE 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND 1 = 1\n" +
                        "\t\t\tAND brand_id = ?\n" +
                        "\t\t\tAND stat_date >= ?\n" +
                        "\t\t\tAND stat_date <= ?\n" +
                        "\t\tGROUP BY 1, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN channel\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN group_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN city\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_code\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN supplier_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN CAST(brand_id AS bigint)\n" +
                        "\t\t\t\tELSE CAST(? AS bigint)\n" +
                        "\t\t\tEND, CASE \n" +
                        "\t\t\t\tWHEN ? <> ? THEN brand_name\n" +
                        "\t\t\t\tELSE ?\n" +
                        "\t\t\tEND\n" +
                        "\t) 成本汇总\n" +
                        "\t\tLEFT JOIN dim_channel_maochao dcm ON 成本汇总.渠道 = dcm.id\n" +
                        "\t\tLEFT JOIN dim_city_maochao dcm1 ON CAST(成本汇总.城市 AS bigint) = dcm1.id\n" +
                        ") quark_t1"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));

        assertEquals(112, params.size());
        assertEquals("[100,6,100,6,100,6,100,6,\"%\",100,6,6,\"%\",100,6,6,\"%\",100,6,100,6,\"%\",100,6,100,6,\"%\",100,6,100,6,\"%\",100,6,100,6,\"%\",100,6,100,6,\"%\",100,6,100,6,\"%\",100,6,100,6,100,6,100,6,\"%\",100,6,100,6,100,6,100,6,\"0\",\"0\",-1,\"0\",\"0\",\"-\",\"0\",\"0\",\"-1\",\"0\",\"0\",\"-\",\"0\",\"0\",\"-\",\"0\",\"0\",-1,\"0\",\"0\",\"-\",\"-\",\"-\",\"-\",\"119079\",\"20170401\",\"20180228\",\"0\",\"0\",-1,\"0\",\"0\",\"-\",\"0\",\"0\",\"-1\",\"0\",\"0\",\"-\",\"0\",\"0\",\"-\",\"0\",\"0\",-1,\"0\",\"0\",\"-\"]", JSON.toJSONString(params));


    }

}