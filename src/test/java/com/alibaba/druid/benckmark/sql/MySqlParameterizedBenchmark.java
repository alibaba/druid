package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Lexer;
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

        for (int i = 0; i < 5; ++i) {
//            perf(sql); // 6740 6201 4752 4514 4391 4218 4127 4124
//            perf(sql2); // 2948 2928 2869 2780 2502
//            perf(sql3); // 15093 10392 10416 10154 10007 9126 8907
//            perf(sql4); // 4429 4190 4023 3747
//            perf(sql5); // 1917 1882
//            perf(sql6); // 4193
//            perf(sql8); // 601 585 314
//            perf(sql9); // 2403 2392 2188 2169
//            perf(sql10); // 3163 2866
            perf(sql13); //

//            perf_parse(sql); // 4643 4377 4345 3801 3627 3228 2961 2959
//            perf_parse(sql2); // 1918 1779 1666 1646
//            perf_parse(sql3); // 9174 5875 5805 5536 5717
//            perf_parse(sql4); // 2953 2502
//            perf_parse(sql5); // 1339
//            perf_parse(sql6);
//            perf_parse(sql7); // 9831 8581 8552
//            perf_parse(sql8); //
//            perf_parse(sql10); // 2291 2025 2024 2010
//            perf_parse(sql11); // 3209
//            perf_parse(sql12); // 4873 4767
//            perf_parse(sql13); //

//            perf_lexer(sql4); // 2051 1802
//            perf_lexer(sql5); // 1125 1054
//            perf_lexer(sql6); // millis : 2811

//            perfParameterized(sql6); // 4224 4083

//            perfFormat(sql7); // 14865 14132 13812 13714 12917

//            perf_hashCode64(sql5); // 181

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
}
