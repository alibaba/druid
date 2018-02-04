package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MySqlParameterizedBenchmark extends TestCase {
    protected void setUp() throws Exception {
        System.out.println("java.runtime.version : " + System.getProperty("java.runtime.version"));
    }

    public void test_perf() throws Exception {
        String sql = "select id as id,    gmt_create as gmtCreate,    gmt_modified as gmtModified,    name as name,    owner as owner,    type as type,    statement as statement,    datasource as datasource,    meta as meta,    param_file as paramFile,    sharable as sharable,    data_type as dataType,    status as status,    config as config,    project_id as projectId,    plugins as plugins,    field_compare as fieldCompare,    field_ext as fieldExt,    openx as openx   from quark_s_dataset     where id = 12569434";
        String sql2 = "/* ///6ea6f232/ */select count(*) FROM (SELECT  * FROM cluster_ins_mapping  WHERE `engine` = 'MySQL' and `status`='Activation'  GROUP BY ip,port ) a;";
        String sql3 = "/* 0be5256b15035048614234260e129b/0.1//722b9d1a/ */          select count(*) from (SELECT message_id,employee_name,employee_id FROM tunning_overview where         instance_info IS NOT NULL         and instance_info = '11.179.218.9:3306'         and gmt_modified >= '2017-08-23 00:00:00' and gmt_modified <= '2017-08-24 00:00:00'         ) o join         (SELECT message_id,index_advice_count,index_advice,gmt_created FROM tunning_task_detail         where gmt_modified >= '2017-08-23 00:00:00'         and gmt_modified <= '2017-08-24 00:00:00'         and index_advice_count >= 0 and error_code='0000'         ) t  on o.message_id=t.message_id         where o.employee_name!='system' or ( o.employee_name='system' and t.index_advice_count>0);";
        String sql4 = "UPDATE ROLLBACK_ON_FAIL TARGET_AFFECT_ROW 1 "
                + "`table_3966` AS `table_3966_11` SET `version` = `version` + 3, `gmt_modified` = NOW(), `optype` = ?, `feature` = ? "
                + "WHERE `sub_biz_order_id` = ? AND `biz_order_type` = ? AND `id` = ? AND `ti_id` = ? AND `optype` = ? AND `root_id` = ?";
        String sql5= "SELECT id, item_id, rule_id, tag_id, ext , gmt_create, gmt_modified FROM wukong_preview_item_tag WHERE item_id = ? AND rule_id = ?";
        String sql6 = "/* 0ab23d3915048393791723851d05b8/9.1.19.1.10176122173.14//8b4757d2/ */SELECT pay_order_id, total_fee, title, alipay_seller_id, alipay_buyer_id, pay_status, out_pay_id, pay_time, end_time, gmt_create, gmt_modified, coupon_fee, actual_total_fee, discount_fee, adjust_fee, closer, point_fee, real_point_fee, obtain_point, refund_fee, confirm_paid_fee, cod_fee, from_group, attributes, attribute_cc, buyer_id, seller_id FROM tc_pay_order_3134 AS tc_pay_order WHERE pay_order_id = 52708022289887078";
        String sql7 = "SELECT batch.warehouse_code AS '仓库CODE', cage.delivery_order_id AS '运单ID', batch.batch_id AS '批次ID', batch.batch_gmt_create AS '批次创建时间', waybill.biz_order_id AS '订单ID'\n" +
                "\t, package.box_id AS '包裹ID', cage.cage_id AS '笼车ID', batch.batch_name AS '批次名称', CASE WHEN batch.status = 1 THEN '初始化' ELSE '待揽收' END AS '状态', waybill.biz_order_remark AS '运单备注'\n" +
                "\t, CASE WHEN SUBSTRING(batch.batch_name, instr(batch.batch_name, ' ') + 1, 2) + 0 >= 18 THEN '晚上送' ELSE '白天送' END AS '备注'\n" +
                "FROM delivery_operator_batch batch, waybill, batch_box package, delivery_order_batch cage\n" +
                "WHERE batch.batch_id = waybill.batch_id\n" +
                "\tAND batch.batch_id = package.batch_id\n" +
                "\tAND package.batch_id = cage.batch_id\n" +
                "\tAND package.cage_id = cage.cage_id\n" +
                "\tAND batch.status IN (1, 2)\n" +
                "\tAND NOT (batch.batch_name IS NULL\n" +
                "\tOR batch.batch_name = '')\n" +
                "ORDER BY batch_gmt_create DESC\n" +
                "LIMIT 0, 50";
        String sql8 = "select @@session.tx_read_only";
        String sql9 = "insert into seller_item_sku_1967 ( item_id, gmt_modified, gmt_create, sku_id, seller_id, outer_id, status, sync_version, sku_feature) values ( 558323210106, '2017-09-08 10:55:39', '2017-09-08 10:55:39', 3634115182525, 3403773871, '', 1, 0, '{\"sku_order_num\":\"2\"}')";
        String sql10 = "update seller_spu_site_1976 set gmt_modified = NOW(), spu_id = 0, site_id = null, site_en = null, features = null, outer_id = null, auction_status = -2 where ((sellerid = 2027808696) AND (item_id = 552891941272))";
        String sql11 = "select seller_item_sku.item_id,seller_item_sku.gmt_modified,seller_item_sku.gmt_create,seller_item_sku.sku_id,seller_item_sku.seller_id,seller_item_sku.outer_id,seller_item_sku.status,seller_item_sku.sync_version,seller_item_sku.sku_feature from seller_item_sku_2047 seller_item_sku where ((seller_item_sku.item_id = 558182960563) AND (seller_item_sku.seller_id = 673619967) AND (seller_item_sku.sku_id = 3634115286014))";
        String sql12 = "/* 0ba9776a15048393743937000d1936/0//7d21f1f2/ */select `auction_relation`.`id`,`auction_relation`.`item_id`,`auction_relation`.`sku_id`,`auction_relation`.`user_id`,`auction_relation`.`target_id`,`auction_relation`.`extra_id`,`auction_relation`.`type`,`auction_relation`.`target_type`,`auction_relation`.`type_attr`,`auction_relation`.`status`,`auction_relation`.`target_user_id`,`auction_relation`.`options`,`auction_relation`.`features`,`auction_relation`.`version`,`auction_relation`.`sub_type`,`auction_relation`.`gmt_create`,`auction_relation`.`gmt_modified` from `auction_relation_1080` `auction_relation` where ((`auction_relation`.`item_id` = 556887226360) AND (`auction_relation`.`target_Type` IN (1,2,3,4)) AND (`auction_relation`.`status` = 0) AND (`auction_relation`.`type` IN (16,17,6,7,8,9,10,11,12,13,14,15)))";
        String sql13 = "/* 0b802d4e15048393680475983ea57a/0.1.5.24.1.1113363181//f7972bef/ */SELECT id, dispute_id, buyer_id, seller_id, total_fee, refund_fee, max_apply_goods_fee, apply_goods_fee, apply_carriage_fee, refund_goods_fee, refund_carriage_fee, refund_point, refund_coupon, refund_return_point, refund_cash, real_deduct_refund_point, real_refund_return_point, refund_return_commission, gmt_create, gmt_modified, attributes, attributes_cc FROM dispute_funds_0502 AS dispute_funds WHERE dispute_id = 3079439578090614";
        String sql14 = "SELECT biz_order_id, value_type, key_value, gmt_create, gmt_modified\n" +
                "\t, attribute_cc, buyer_id\n" +
                "FROM tc_biz_vertical_2533 t\n" +
                "WHERE t.biz_order_id = 54848191250268105\n" +
                "\tAND value_type = 0";
        String sql15 = "SELECT biz_order_id, value_type, key_value, gmt_create, gmt_modified\n" +
                "\t, attribute_cc, buyer_id\n" +
                "FROM tc_biz_vertical_2533 t\n" +
                "WHERE t.biz_order_id = 54848191250268105\n" +
                "\tAND value_type = 0";
        String sql16 = "SELECT sub_logistics_order_id, consign_time, attribute_cc, attributes, out_logistics_id\n" +
                "\t, parent_id, gmt_create, gmt_modified, detail_order_id, is_last\n" +
                "\t, ship_amount, buyer_id, seller_id, ship_status, step_order_id\n" +
                "FROM tc_sub_logistics_2531 tc_sub_logistics\n" +
                "WHERE parent_id = 13571580288486317\n" +
                "\tAND is_last = 1";
        String sql17 = "/* 0ba9776a15048393743937000d1936/0//7d21f1f2/ */select `auction_relation`.`id`,`auction_relation`.`item_id`,`auction_relation`.`sku_id`,`auction_relation`.`user_id`,`auction_relation`.`target_id`,`auction_relation`.`extra_id`,`auction_relation`.`type`,`auction_relation`.`target_type`,`auction_relation`.`type_attr`,`auction_relation`.`status`,`auction_relation`.`target_user_id`,`auction_relation`.`options`,`auction_relation`.`features`,`auction_relation`.`version`,`auction_relation`.`sub_type`,`auction_relation`.`gmt_create`,`auction_relation`.`gmt_modified` from `auction_relation_1080` `auction_relation` where ((`auction_relation`.`item_id` = 556887226360) AND (`auction_relation`.`target_Type` IN (1,2,3,4)) AND (`auction_relation`.`status` = 0) AND (`auction_relation`.`type` IN (16,17,6,7,8,9,10,11,12,13,14,15)))";
        String sql18 = "insert into SURF_WEA_CHN_HOR_ALL_TAB_INSERT (`D_DATETIME`, `V_ACODE`, `V01300`, `V01301`, `V05001`, `V06001`, `D_DATA_ID`, `D_IYMDHM`, `D_RYMDHM`, `D_UPDATE_TIME`, `V_BBB`,V04001, V04002, V04003, V04004, V07001, V07031, V07032_04, V07032_01, V07032_02, V02001, V02301, V08010, V02183, V10004, V10051, V10061, V10062, V10301, V10301_052, V10302, V10302_052, V12001, V12011, V12011_052, V12012, V12012_052, V12405, V12016, V12017, V12003, V13003, V13007, V13007_052, V13004, V13019, V13020, V13021, V13022, V13023, V04080_04, V13011, V13033, V11290, V11291, V11292, V11293, V11296, V11042, V11042_052, V11201, V11202, V11211, V11046, V11046_052, V11503_06, V11504_06, V11503_12, V11504_12, V12120, V12311, V12311_052, V12121, V12121_052, V12013, V12030_005, V12030_010, V12030_015, V12030_020, V12030_040, V12030_080, V12030_160, V12030_320, V12314, V12315, V12315_052, V12316, V12316_052, V20001_701_01, V20001_701_10, V20059, V20059_052, V20001, V20010, V20051, V20011, V20013, V20350_01, V20350_02, V20350_03, V20350_04, V20350_05, V20350_06, V20350_07, V20350_08, V20350_11, V20350_12, V20350_13, V20003, V04080_05, V20004, V20005, V20062, V13013, V13330, V20330_01, V20331_01, V20330_02, V20331_02, Q10004, Q10051, Q10061, Q10062, Q10301, Q10301_052, Q10302, Q10302_052, Q12001, Q12011, Q12011_052, Q12012, Q12012_052, Q12405, Q12016, Q12017, Q12003, Q13003, Q13007, Q13007_052, Q13004, Q13019, Q13020, Q13021, Q13022, Q13023, Q04080_04, Q13011, Q13033, Q11290, Q11291, Q11292, Q11293, Q11296, Q11042, Q11042_052, Q11201, Q11202, Q11211, Q11046, Q11046_052, Q11503_06, Q11504_06, Q11503_12, Q11504_12, Q12120, Q12311, Q12311_052, Q12121, Q12121_052, Q12013, Q12030_005, Q12030_010, Q12030_015, Q12030_020, Q12030_040, Q12030_080, Q12030_160, Q12030_320, Q12314, Q12315, Q12315_052, Q12316, Q12316_052, Q20001_701_01, Q20001_701_10, Q20059, Q20059_052, Q20001, Q20010, Q20051, Q20011, Q20013, Q20350_01, Q20350_02, Q20350_03, Q20350_04, Q20350_05, Q20350_06, Q20350_07, Q20350_08, Q20350_11, Q20350_12, Q20350_13, Q20003, Q04080_05, Q20004, Q20005, Q20062, Q13013, Q13330, Q20330_01, Q20331_01, Q20330_02, Q20331_02) values('2390-12-1', '190000', 140336, '140336', 74, 27,'A.0012.0001.S001', now(), now(), now(), 'abc', 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999, 9999 )";
        String sql19 = "insert into test(id, name, name2, name3) values(4, '1', '1', '1'), (5, '1', '1', '1'), (6, '1', '1', '1');";

        String sql20 = "SELECT biz_order_id, out_order_id, seller_nick, buyer_nick, seller_id\n" +
                "\t, buyer_id, auction_id, auction_title, auction_price, buy_amount\n" +
                "\t, biz_type, sub_biz_type, fail_reason, pay_status, logistics_status\n" +
                "\t, out_trade_status, snap_path, gmt_create, status\n" +
                "\t, ifnull(buyer_rate_status, 4) AS buyer_rate_status\n" +
                "\t, ifnull(seller_rate_status, 4) AS seller_rate_status, auction_pict_url\n" +
                "\t, seller_memo, buyer_memo, seller_flag, buyer_flag, buyer_message_path\n" +
                "\t, refund_status, attributes, attributes_cc, gmt_modified, ip\n" +
                "\t, end_time, pay_time, is_main, is_detail, point_rate\n" +
                "\t, parent_id, adjust_fee, discount_fee, refund_fee, confirm_paid_fee\n" +
                "\t, cod_status, trade_tag, shop_id, sync_version, options\n" +
                "\t, ignore_sold_quantity, from_group, attribute1, attribute2, attribute3\n" +
                "\t, attribute4, attribute11\n" +
                "FROM tc_biz_order_3212 tc_biz_order\n" +
                "WHERE parent_id = 9065896323824229284";

        String sql21 = "INSERT INTO __test_tc_biz_vertical_2052 (biz_order_id, value_type, key_value, buyer_id, seller_id\n" +
                "\t, gmt_create, gmt_modified, attribute_cc)\n" +
                "VALUES (9063685435998934059, 1, ';constract_service:DEFAULT#3B7d;promotionShare:shopbonus-9223370001508705690_9223370006796631977-9223370091419060389#3B300;service_tags:TB_CONSIGN_DATE$consignDate@48&;cartid:9223370002239375832;ppf:0;templateSnapshot:calcu_nomal@icKey_900000039254633516_0~price_4000~count_1~size_0~weight_0~tpid_1164540740~uid_9223370014720392256~ver_2~valu_0~freeReason_null~isFree_false~promo_;', 9223370015555935940, 9223370014720392256\n" +
                "\t, NOW(), NOW(), 0)";

        String sql22 = "/* 0b852b3c15094480140194289e3d24/0.1.1.2.1//2e3b9cf7/ */select `member_cart`.`CART_ID`,`member_cart`.`SKU_ID`,`member_cart`.`ITEM_ID`,`member_cart`.`QUANTITY`,`member_cart`.`USER_ID`,`member_cart`.`SELLER_ID`,`member_cart`.`STATUS`,`member_cart`.`EXT_STATUS`,`member_cart`.`TYPE`,`member_cart`.`SUB_TYPE`,`member_cart`.`GMT_CREATE`,`member_cart`.`GMT_MODIFIED`,`member_cart`.`ATTRIBUTE`,`member_cart`.`ATTRIBUTE_CC`,`member_cart`.`EX2` from `member_cart_0304` `member_cart` where ((`member_cart`.`USER_ID` = 2732851504) AND ((`member_cart`.`STATUS` = 1) AND (`member_cart`.`TYPE` IN (0,5,10)))) limit 0,200";
        String sql23 = "/* 0ba8360215094481936224182e74a5/0.1.1.2.28//eb2abb79/ */INSERT INTO tc_biz_order_3912 (biz_order_id, out_order_id, seller_nick, buyer_nick, seller_id, buyer_id, auction_id, auction_title, auction_price, buy_amount, biz_type, sub_biz_type, fail_reason, pay_status, logistics_status, out_trade_status, snap_path, gmt_create, status, buyer_rate_status, seller_rate_status, auction_pict_url, seller_memo, buyer_memo, seller_flag, buyer_flag, buyer_message_path, refund_status, attributes, attributes_cc, gmt_modified, ip, end_time, pay_time, is_main, is_detail, point_rate, parent_id, adjust_fee, discount_fee, refund_fee, confirm_paid_fee, cod_status, trade_tag, shop_id, sync_version, options, ignore_sold_quantity, from_group, attribute1, attribute2, attribute3, attribute4, attribute11) VALUES (81721703845156454, 'b_504714202740', 'suilu2009', 'lily_zys', 893525513, 90155464, 543024929653, '�����߲˺���ɽ���ز���ī����ع� ũ�������л�������ʳ5�����', 2900, 2, 200, 1, null, 7, 8, 0, 'm:81721703845156454_1', '2017-10-31 19:09:53', 0, 5, 5, 'i1/893525513/TB22oPHX4hmpuFjSZFyXXcLdFXa_!!893525513.jpg', null, null, 0, 0, 'm:81721703845156454_4', 9, ';bizCode:taobao.general.foods;shipping:2;newDO:1;productNoList:[7, 26];ctr:1;otf:5800;pro_free_de:1;p_sign:fe1714afc2738ec12aba5c9d0ec263f1;reduceInv:1;addBackInv:1;inv_result:#3A1#3B1#3A;itemTag:587,651,843,1035,1163,1611,2507,2635,4491,4550,4619,4811,5190,6603,7371,7947,8395,11083,11339,11467,16395,25282,36610,48578,165186;anony:1;ptid:8774749370;ttid:201200@taobao_iphone_7.1.0;ppayProt:1;isChecked:false;realRootCat:50050359;fusion:1;fromAdp:1;tmppTraceId:0ba8360215094481936224182e74a5;defaultAddr:1;tp3create:1;m_sign:8a79a1dd98a19a1e9d34f28bdc459e2b;lTime:0;unec:1;virtual:0;orderIdSelf:1;cosys:wap|buynow;buy2create:1;prepayCat:1;subUniqId:b_504714202740_1;joinId:b_504714202740_1;lgType:-4;cvb:1;pSubOutId:fe1714afc2738ec12aba5c9d0ec263f1;unity:1;rootCat:50050720;pOutId:b_504714202740;wkup:1;pxjkc:1;dapFlag:3|3372087912011355|consumerprotect^1^1^15;reduce_suc:1;wap:1;address_city:�Ϻ���;divisionCode:310112107;tf:5800;supDcc:Y#N;shopname:����ũ��Ʒ;unifyInv:1;buyrisk:1;shipname:���;stuff:5;', 0, '2017-10-31 19:09:53', 3031141102, null, null, 1, 1, 0, 81721703845156454, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '1|createOutPayOrder|^')";

//        System.out.println(System.getProperties());

//        System.out.println(sql19);
        for (int i = 0; i < 5; ++i) {
//            perf(sql); // 6740 6201 4752 4514 4391 4218 4127 4124
//            perf(sql2); // 2948 2928 2869 2780 2502
//            perf(sql3); // 15093 10392 10416 10154 10007 9126 8907
//            perf(sql4); // 4429 4190 4023 3747
//            perf(sql5); // 1917 1882
//            perf(sql6); // 4193
//            perf(sql8); // 601 585 314
//            perf(sql9); // 2403 2392 2188 2169 2083 1983 1956 1941
//            perf(sql10); // 3163 2866
//            perf(sql13); //
//            perf(sql15); // 1417
//            perf(sql16); // 3464 2518 1981 1625 1511 1490 1448
            //perf(sql21); // 2158 1816 1598
//            perf(sql20);
//            perf(sql22); // 3030 2980 2848 2955 2800 2582
//            perf(sql23);

//            perf_hash(sql22); // 2713
            perf_hash(sql23); // 4804 3858 3699

//            perf_hash2(sql22); // 3333

//            perf_direct(sql16); // 1531

//            perf_parse(sql); // 4643 4377 4345 3801 3627 3228 2961 2959
//            perf_parse(sql2); // 1918 1779 1666 1646
//            perf_parse(sql3); // 9174 5875 5805 5536 5717
//            perf_parse(sql4); // 2953 2502
//            perf_parse(sql5); // 1339
//            perf_parse(sql6);
//            perf_parse(sql7); // 9831 8581 8552
//            perf_parse(sql8); //
//            perf_parse(sql9); //
//            perf_parse(sql10); // 2291 2025 2024 2010
//            perf_parse(sql11); // 3209
//            perf_parse(sql12); // 4873 4767 3854
//            perf_parse(sql14); //
//            perf_parse(sql15); // 976 970 953 945 921
//            perf_parse(sql16); // 2419 1483 1045
//            perf_parse(sql17); // 3089 3041
//            perf_parse(sql18); // 23913 13408 millis : 13131
//            perf_parse(sql19); // 870

//            perf_lexer(sql4); // 2051 1802
//            perf_lexer(sql5); // 1125 1054
//            perf_lexer(sql6); // millis : 2811
//            perf_lexer(sql20);

//            perfParameterized(sql6); // 4224 4083

//            perfFormat(sql7); // 14865 14132 13812 13714 12917

//            perf_hashCode64(sql5); // 181
//            perf_hashCode64(sql20); // 1084

//            perf_stat(sql); // 15214 11793 13628 13561 13259 9946 7637 7444 7389 7326 7176 6687 5973 5660

//            perf_resolve(sql); // 4970 4586 3600 3595

//            perf_wall(sql); // 9695 5017
        }
    }
    public void perf(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            String psql = SqlHolder.of(sql).parameterize();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_parse(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder.of(sql).ensureParsed();
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_stat(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder holder = SqlHolder.of(sql);
            holder.ensureParsed();

            //new SchemaRepository(JdbcConstants.MYSQL).resolve(holder.ast);
            SQLASTVisitor visitor = new MySqlSchemaStatVisitor();
            holder.ast.accept(visitor);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_resolve(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SqlHolder holder = SqlHolder.of(sql);
            holder.ensureParsed();

            new SchemaRepository(JdbcConstants.MYSQL).resolve(holder.ast);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    MySqlWallProvider provider = new MySqlWallProvider();
    public void perf_wall(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            provider.checkValid(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_lexer(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            Lexer.parameterize(sql, JdbcConstants.MYSQL);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perfParameterized(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_hashCode64(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            FnvHash.fnv1a_64(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perfFormat(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            SQLUtils.format(sql, JdbcConstants.MYSQL);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    static SQLSelectListCache selectListCache = SqlHolder.selectListCache;

    public void perf_direct(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, selectListCache);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_hash(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            ParameterizedOutputVisitorUtils.parameterizeHash(sql, JdbcConstants.MYSQL, selectListCache, null);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }

    public void perf_hash2(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            FnvHash.fnv1a_64_lower(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, selectListCache));
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("millis : " + millis);
    }
//
//    static void x(String datas, int currItemOffset, int nextItemOffset) {
//long value = 0;
//for (int i = currItemOffset + 13; i < nextItemOffset; ++i) {
//    int digit = datas.charAt(i) - '0';
//    value = value * 10 + digit;
//    // power *= 10;
//}
//double doubleValue = ((double)value) * 0.000000000000000000000001D; // 如果定长，使用固定值，如果边长，在for循环中power然后除power
//    }
}
