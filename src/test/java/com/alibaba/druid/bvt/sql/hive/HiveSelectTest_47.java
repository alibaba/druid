package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_47 extends TestCase {
    static String sql = "with temp1 AS ( \n" +
            "    SELECT datetrunc(gmv_time,'mm') as report_month\n" +
            "    ,seller_user_id\n" +
            "    ,count(1) as 卖家订单量\n" +
            "    from du_shucang.dm_ord_sub_order a \n" +
            "    where pt = max_pt('du_shucang.dm_ord_sub_order')\n" +
            "    and pay_status = 1 \n" +
            "    group by datetrunc(gmv_time,'mm'),seller_user_id\n" +
            ")\n" +
            ",temp2 AS ( \n" +
            "    -- SELECT gmv_time,a.sub_order_no,a.buyer_user_id\n" +
            "    -- ,ROW_NUMBER() OVER(PARTITION BY a.buyer_user_id ORDER BY gmv_time) AS order_rank \n" +
            "    -- from du_shucang.dm_ord_sub_order a \n" +
            "    -- where pt = max_pt('du_shucang.dm_ord_sub_order')\n" +
            "    -- and pay_status = 1 \n" +
            "    SELECT user_id,first_activat_time\n" +
            "    from du_shucang.dw_usr_user_order_extend \n" +
            "    where pt = max_pt('du_shucang.dw_usr_user_order_extend')\n" +
            ")\n" +
            "SELECT datetrunc(a.gmv_time,'mm') as report_month\n" +
            ",'' GMV\n" +
            "    ,sum(total_amount*0.01) as 支付gmv \n" +
            "    ,sum(buyer_discount_amount*0.01) as 买家优惠金额\n" +
            "    ,sum(buyer_discount_amount*0.01)/nullif(sum(total_amount*0.01),0) as 优惠占比 \n" +
            "    ------支付gmv-----\n" +
            "    ,sum(a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01) 支付gmv不含优惠\n" +
            "    ,sum(case when c.user_id is not null  then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01 end) 支付gmv不含优惠_新用户\n" +
            "    ,sum(case when c.user_id is null then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01 end) 支付gmv不含优惠_老用户\n" +
            "        ----未交付\n" +
            "    ,sum(case when sub_order_status_desc <> '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01 end) as 未交付gmv不含优惠\n" +
            "    ,sum(case when c.user_id is not null and sub_order_status_desc <> '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01 end) as 未交付gmv不含优惠_新用户\n" +
            "    ,sum(case when c.user_id is null and sub_order_status_desc <> '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01 end) as 未交付gmv不含优惠_老用户\n" +
            "    ----交付\n" +
            "    ,sum(case when sub_order_status_desc = '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01end) as 交付gmv不含优惠\n" +
            "    ,sum(case when c.user_id is not null and sub_order_status_desc = '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01end) as 交付gmv不含优惠_新用户\n" +
            "    ,sum(case when c.user_id is null  and sub_order_status_desc = '交易成功' then a.total_price*0.01 + buyer_freight_amount*0.01 - buyer_discount_amount*0.01end) as 交付gmv不含优惠_老用户\n" +
            "    ----未交付\n" +
            "    ,sum(case when sub_order_status_desc <> '交易成功' then total_amount*0.01 end) as 未交付gmv\n" +
            "    ,sum(case when c.user_id is not null and sub_order_status_desc <> '交易成功' then total_amount*0.01 end) as 未交付gmv_新用户\n" +
            "    ,sum(case when c.user_id is null and sub_order_status_desc <> '交易成功' then total_amount*0.01 end) as 未交付gmv_老用户\n" +
            "    ----交付\n" +
            "    ,sum(case when sub_order_status_desc = '交易成功' then total_amount*0.01 end) as 交付gmv\n" +
            "    ,sum(case when c.user_id is not null and sub_order_status_desc = '交易成功' then total_amount*0.01 end) as 交付gmv_新用户\n" +
            "    ,sum(case when c.user_id is null and sub_order_status_desc = '交易成功' then total_amount*0.01 end) as 交付gmv_老用户\n" +
            ",'' 订单量\n" +
            "    ,count(distinct a.sub_order_no) as 支付订单量\n" +
            "    ,count(distinct case when c.user_id is not null then a.sub_order_no end) as 支付订单量_新用户\n" +
            "    ,count(distinct case when c.user_id is null then a.sub_order_no end) as 支付订单量_老用户\n" +
            "\n" +
            "    ,count(distinct case when sub_order_status_desc <> '交易成功' then a.sub_order_no end) as 未交付订单量\n" +
            "    ,count(distinct case when c.user_id is not null and sub_order_status_desc <> '交易成功' then a.sub_order_no end) as 未交付订单量_新用户\n" +
            "    ,count(distinct case when c.user_id is null and sub_order_status_desc <> '交易成功' then a.sub_order_no end) as 未交付订单量_老用户\n" +
            "\n" +
            "    ,count(distinct case when sub_order_status_desc = '交易成功' then a.sub_order_no end) as 交付订单量\n" +
            "    ,count(distinct case when c.user_id is not null and sub_order_status_desc = '交易成功' then a.sub_order_no end) as 交付订单量_新用户\n" +
            "    ,count(distinct case when c.user_id is null and sub_order_status_desc = '交易成功' then a.sub_order_no end) as 交付订单量_老用户\n" +
            ",'' 价单价\n" +
            "    ,sum(total_amount*0.01)/nullif(count(distinct a.sub_order_no),0) as 支付件单价\n" +
            "    ,sum(case when c.user_id is not null then total_amount*0.01 end)/nullif(count(distinct case when c.user_id is not null then a.sub_order_no end),0) as 支付件单价_新用户\n" +
            "    ,sum(case when c.user_id is null then total_amount*0.01 end)/nullif(count(distinct case when c.user_id is null then a.sub_order_no end),0) as 支付件单价_老用户\n" +
            ",'' 下单人数\n" +
            "    ,count(distinct a.buyer_user_id) as 支付下单人数\n" +
            "    ,count(distinct case when c.user_id is not null then a.buyer_user_id end) as 支付下单人数_新用户\n" +
            "    ,count(distinct case when c.user_id is null then a.buyer_user_id end) as 支付下单人数_老用户\n" +
            "\n" +
            "    ,count(distinct case when sub_order_status_desc = '交易成功' then a.buyer_user_id end) as 交付下单人数\n" +
            "    ,count(distinct case when c.user_id is not null  and sub_order_status_desc = '交易成功' then a.buyer_user_id end) as 交付下单人数_新用户\n" +
            "    ,count(distinct case when c.user_id is null and sub_order_status_desc = '交易成功' then a.buyer_user_id end) as 交付下单人数_老用户\n" +
            "\n" +
            "    ,count(distinct a.sub_order_no)/nullif(count(distinct a.buyer_user_id),0) as 支付下单频次\n" +
            "    ,count(distinct case when c.user_id is not null then a.sub_order_no end)/nullif(count(distinct case when c.user_id is not null then a.buyer_user_id end),0) as 支付下单频次_新用户\n" +
            "    ,count(distinct case when c.user_id is null then a.sub_order_no end)/nullif(count(distinct case when c.user_id is null then a.buyer_user_id end),0) as 支付下单频次_老用户\n" +
            "\n" +
            "    ,count(distinct case when sub_order_status_desc = '交易成功' then a.sub_order_no end)/nullif(count(distinct case when sub_order_status_desc = '交易成功' then a.buyer_user_id end),0) as 交付下单频次\n" +
            "    ,count(distinct case when c.user_id is not null  and sub_order_status_desc = '交易成功' then a.sub_order_no end)/nullif(count(distinct case when c.user_id is not null  and sub_order_status_desc = '交易成功'then a.buyer_user_id end),0) as 交付下单频次_新用户\n" +
            "    ,count(distinct case when c.user_id is null and sub_order_status_desc = '交易成功' then a.sub_order_no end)/nullif(count(distinct case when c.user_id is null and sub_order_status_desc = '交易成功' then a.buyer_user_id end),0) as 交付下单频次_老用户\n" +
            "\n" +
            "    ,sum(total_amount*0.01)/nullif(count(distinct a.buyer_user_id),0) as ARPU\n" +
            "    ,sum(case when c.user_id is not null then total_amount*0.01 end)/nullif(count(distinct case when c.user_id is not null  then a.buyer_user_id end),0) as ARPU_新用户\n" +
            "    ,sum(case when c.user_id is null then total_amount*0.01 end)/nullif(count(distinct case when c.user_id is null then a.buyer_user_id end),0) as ARPU_老用户\n" +
            "    \n" +
            "    ,count(distinct a.seller_user_id) as 当月卖家人数\n" +
            "    ,count(distinct case when b.卖家订单量 > 50 then a.seller_user_id end) as 订单量每月50单以上卖家数\n" +
            "    ,count(distinct case when b.卖家订单量 >= 11 and b.卖家订单量 <= 50 then a.seller_user_id end) as 订单量每月11_5卖家数\n" +
            "    ,count(distinct case when b.卖家订单量 >= 6 and b.卖家订单量 <= 10 then a.seller_user_id end) as 订单量每月6_10卖家数\n" +
            "    ,count(distinct case when b.卖家订单量 >= 1 and b.卖家订单量 <= 5 then a.seller_user_id end) as 订单量每月1_5卖家数\n" +
            "    ,count(distinct case when b.卖家订单量 >= 1 and b.卖家订单量 <= 5 then a.seller_user_id end)/count(distinct a.seller_user_id) as 月1_5卖家数占比\n" +
            "\n" +
            "    ,count(distinct case when category_lv3_name = '篮球鞋' then a.sub_order_no end) as 篮球鞋支付订单量\n" +
            "    ,count(distinct case when category_lv3_name = '跑步鞋' then a.sub_order_no end) as 跑步鞋支付订单量\n" +
            "    ,count(distinct case when category_lv2_name = '休闲鞋' then a.sub_order_no end) as 休闲鞋支付订单量\n" +
            "    ,count(distinct case when category_lv1_name = '服装' then a.sub_order_no end) as 服装支付订单量\n" +
            "    ,count(distinct case when category_lv1_name <> '服装' and category_lv2_name not in ('休闲鞋') and category_lv3_name not in ('篮球鞋','跑步鞋') then a.sub_order_no end) as 其他支付订单量\n" +
            "\n" +
            "    ,count(case when category_lv3_name = '篮球鞋' then sku_id end) as 篮球鞋sku数量\n" +
            "    ,count(case when category_lv3_name = '跑步鞋' then sku_id end) as 跑步鞋sku数量\n" +
            "    ,count(case when category_lv2_name = '休闲鞋' then sku_id end) as 休闲鞋sku数量\n" +
            "    ,count(case when category_lv1_name = '服装' then sku_id end) as 服装支付sku数量\n" +
            "    ,count(case when category_lv1_name <> '服装' and category_lv2_name not in ('休闲鞋') and category_lv3_name not in ('篮球鞋','跑步鞋') then sku_id end) as 其他支付sku数量\n" +
            "\n" +
            "from du_shucang.dm_ord_sub_order a \n" +
            "left join temp2 c on a.buyer_user_id = c.user_id and datetrunc(a.gmv_time,'mm') = datetrunc(c.first_activat_time,'mm')\n" +
            "left join temp1 b on datetrunc(a.gmv_time,'mm') = b.report_month and a.seller_user_id = b.seller_user_id \n" +
            "where a.pt = max_pt('du_shucang.dm_ord_sub_order')\n" +
            "and a.pay_status = 1 and datetrunc(a.gmv_time,'dd') >= '2017-08-01 00:00:00'\n" +
            "group by datetrunc(a.gmv_time,'mm') \n" +
            ";";

    public void test_select() throws Exception {

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
          System.out.println("fields : " + visitor.getColumns());
          System.out.println("coditions : " + visitor.getConditions());
          System.out.println("orderBy : " + visitor.getOrderByColumns());
//
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(2, visitor.getColumns().size());
//        assertEquals(2, visitor.getConditions().size());

    }
}
