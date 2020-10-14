package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.junit.Test;


public class MySqlCreateTableTest158_asselect extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table tmp_eric (pk int key, ia int unique) replace as select * from t;";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE tmp_eric (\n" +
                "\tpk int PRIMARY KEY,\n" +
                "\tia int UNIQUE\n" +
                ")\n" +
                "REPLACE \n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM t;", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "create table tmp_eric (pk int key, ia int unique) ignore as select * from t;";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE tmp_eric (\n" +
                "\tpk int PRIMARY KEY,\n" +
                "\tia int UNIQUE\n" +
                ")\n" +
                "IGNORE \n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM t;", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "create table tmp_eric (pk int key, ia int unique) replace ignore as select * from t;";

        try {
            MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
            fail();
        } catch (Exception e) {
        }
    }

    public void test_4() throws Exception {
        String sql = "CREATE TABLE `D` (\n"
                + " `id` bigint(20) NOT NULL comment 'xxx' AUTO_INCREMENT,\n"
                + " `c1` tinyint(1) DEFAULT NULL,\n"
                + " `c2` tinyint(4) DEFAULT NULL,\n"
                + " PRIMARY KEY (`id`)\n"
                + ") replace as select * from a";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE `D` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'xxx',\n" +
                "\t`c1` tinyint(1) DEFAULT NULL,\n" +
                "\t`c2` tinyint(4) DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "REPLACE \n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM a", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "CREATE TABLE artists_and_works\n" +
                "  SELECT artist.name, COUNT(work.artist_id) AS number_of_works\n" +
                "  FROM artist LEFT JOIN work ON artist.id = work.artist_id\n" +
                "  GROUP BY artist.id;";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE artists_and_works\n" +
                "AS\n" +
                "SELECT artist.name, COUNT(work.artist_id) AS number_of_works\n" +
                "FROM artist\n" +
                "\tLEFT JOIN work ON artist.id = work.artist_id\n" +
                "GROUP BY artist.id;", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "Create Table `aftersale_item_s3combine_1000` (\n" +
                " `order_status` varchar,\n" +
                " `is_cod` int,\n" +
                " `so_id` varchar,\n" +
                " `type` varchar,\n" +
                " `created` timestamp,\n" +
                " `creator_name` varchar,\n" +
                " `modifier_name` varchar,\n" +
                " `status` varchar,\n" +
                " `remark` varchar,\n" +
                " `question_type` varchar,\n" +
                " `refund` decimal(16, 4),\n" +
                " `payment` decimal(16, 4),\n" +
                " `shop_buyer_id` varchar,\n" +
                " `shop_id` int,\n" +
                " `logistics_company` varchar,\n" +
                " `l_id` varchar,\n" +
                " `good_status` varchar,\n" +
                " `order_type` varchar,\n" +
                " `drp_co_id` int,\n" +
                " `wms_co_id` int,\n" +
                " `tag` int,\n" +
                " `confirm_date` timestamp,\n" +
                " `asi_id` bigint,\n" +
                " `as_id` bigint,\n" +
                " `o_id` bigint,\n" +
                " `co_id` int,\n" +
                " `sku_id` varchar,\n" +
                " `outer_oi_id` varchar,\n" +
                " `sku_type` varchar,\n" +
                " `db_name` varchar,\n" +
                " `table_name` varchar,\n" +
                " `db_id` int,\n" +
                " `tb_id` int,\n" +
                " `cost_price` decimal(12, 4),\n" +
                " `base_amount` decimal(16, 4),\n" +
                " `bf_amount` decimal(16, 4),\n" +
                " `return_amount` decimal(16, 4),\n" +
                " `bf_qty` int,\n" +
                " `return_qty` int,\n" +
                " `base_qty` int,\n" +
                " `r_qty` int,\n" +
                " `src_combine_sku_id` varchar,\n" +
                " `item_type` varchar,\n" +
                " `return_amount_share` decimal(16, 4),\n" +
                " `bf_amount_share` decimal(16, 4),\n" +
                " `asid_rownumber` int,\n" +
                " `bf_asid_rownumber` int,\n" +
                " `sku_flag` int,\n" +
                " `sku_row_number` int,\n" +
                " `combinesku_row_number` int,\n" +
                " `buyer_message` varchar,\n" +
                " `order_remark` varchar,\n" +
                " `sent_flag` int,\n" +
                " `order_date` timestamp,\n" +
                " `pay_date` timestamp,\n" +
                " `send_date` timestamp,\n" +
                " `merge_so_id` varchar,\n" +
                " `receiver_state` varchar,\n" +
                " `receiver_city` varchar,\n" +
                " `receiver_district` varchar,\n" +
                " `receiver_name` varchar,\n" +
                " `order_from` varchar,\n" +
                " `presend_status` varchar,\n" +
                " `is_jst` int,\n" +
                " `seller_flag` int,\n" +
                " `shop_site` varchar,\n" +
                " `shop_buyer_id_en` varchar,\n" +
                " `receiver_name_en` varchar,\n" +
                " `dw_created` timestamp,\n" +
                " `properties_value` varchar,\n" +
                " `order_labels` varchar,\n" +
                " `order_l_id` varchar,\n" +
                " `outer_as_id` varchar,\n" +
                " `is_gift` int,\n" +
                " `end_time` timestamp,\n" +
                " `shop_sku_id` varchar,\n" +
                " `freight` decimal(16, 4),\n" +
                " `shop_i_id` varchar,\n" +
                " `as_item_so_id` varchar,\n" +
                " `entity_sku_flag` int,\n" +
                " `entity_combinesku_id` varchar,\n" +
                " `cdc_ts` timestamp,\n" +
                " `del_flag` int,\n" +
                " `sku_flag_new` int,\n" +
                " `drp_co_id_from` int,\n" +
                " `drp_co_id_to` int,\n" +
                " `supplier_name` varchar,\n" +
                " `wh_id` int,\n" +
                " `warehouse` varchar,\n" +
                " `combine_sku_id` varchar,\n" +
                " `creator` bigint,\n" +
                " `des` varchar,\n" +
                " `skuid_row_number` int,\n" +
                " `order_creator_name` varchar,\n" +
                " `order_creator` bigint,\n" +
                " primary key (co_id,o_id,as_id,asi_id,sku_id,sku_flag,entity_combinesku_id)\n" +
                ") DISTRIBUTE BY HASH(`o_id`) INDEX_ALL='Y' COMMENT='comments' replace as select * from t;";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        assertEquals("CREATE TABLE `aftersale_item_s3combine_1000` (\n" +
                "\t`order_status` varchar,\n" +
                "\t`is_cod` int,\n" +
                "\t`so_id` varchar,\n" +
                "\t`type` varchar,\n" +
                "\t`created` timestamp,\n" +
                "\t`creator_name` varchar,\n" +
                "\t`modifier_name` varchar,\n" +
                "\t`status` varchar,\n" +
                "\t`remark` varchar,\n" +
                "\t`question_type` varchar,\n" +
                "\t`refund` decimal(16, 4),\n" +
                "\t`payment` decimal(16, 4),\n" +
                "\t`shop_buyer_id` varchar,\n" +
                "\t`shop_id` int,\n" +
                "\t`logistics_company` varchar,\n" +
                "\t`l_id` varchar,\n" +
                "\t`good_status` varchar,\n" +
                "\t`order_type` varchar,\n" +
                "\t`drp_co_id` int,\n" +
                "\t`wms_co_id` int,\n" +
                "\t`tag` int,\n" +
                "\t`confirm_date` timestamp,\n" +
                "\t`asi_id` bigint,\n" +
                "\t`as_id` bigint,\n" +
                "\t`o_id` bigint,\n" +
                "\t`co_id` int,\n" +
                "\t`sku_id` varchar,\n" +
                "\t`outer_oi_id` varchar,\n" +
                "\t`sku_type` varchar,\n" +
                "\t`db_name` varchar,\n" +
                "\t`table_name` varchar,\n" +
                "\t`db_id` int,\n" +
                "\t`tb_id` int,\n" +
                "\t`cost_price` decimal(12, 4),\n" +
                "\t`base_amount` decimal(16, 4),\n" +
                "\t`bf_amount` decimal(16, 4),\n" +
                "\t`return_amount` decimal(16, 4),\n" +
                "\t`bf_qty` int,\n" +
                "\t`return_qty` int,\n" +
                "\t`base_qty` int,\n" +
                "\t`r_qty` int,\n" +
                "\t`src_combine_sku_id` varchar,\n" +
                "\t`item_type` varchar,\n" +
                "\t`return_amount_share` decimal(16, 4),\n" +
                "\t`bf_amount_share` decimal(16, 4),\n" +
                "\t`asid_rownumber` int,\n" +
                "\t`bf_asid_rownumber` int,\n" +
                "\t`sku_flag` int,\n" +
                "\t`sku_row_number` int,\n" +
                "\t`combinesku_row_number` int,\n" +
                "\t`buyer_message` varchar,\n" +
                "\t`order_remark` varchar,\n" +
                "\t`sent_flag` int,\n" +
                "\t`order_date` timestamp,\n" +
                "\t`pay_date` timestamp,\n" +
                "\t`send_date` timestamp,\n" +
                "\t`merge_so_id` varchar,\n" +
                "\t`receiver_state` varchar,\n" +
                "\t`receiver_city` varchar,\n" +
                "\t`receiver_district` varchar,\n" +
                "\t`receiver_name` varchar,\n" +
                "\t`order_from` varchar,\n" +
                "\t`presend_status` varchar,\n" +
                "\t`is_jst` int,\n" +
                "\t`seller_flag` int,\n" +
                "\t`shop_site` varchar,\n" +
                "\t`shop_buyer_id_en` varchar,\n" +
                "\t`receiver_name_en` varchar,\n" +
                "\t`dw_created` timestamp,\n" +
                "\t`properties_value` varchar,\n" +
                "\t`order_labels` varchar,\n" +
                "\t`order_l_id` varchar,\n" +
                "\t`outer_as_id` varchar,\n" +
                "\t`is_gift` int,\n" +
                "\t`end_time` timestamp,\n" +
                "\t`shop_sku_id` varchar,\n" +
                "\t`freight` decimal(16, 4),\n" +
                "\t`shop_i_id` varchar,\n" +
                "\t`as_item_so_id` varchar,\n" +
                "\t`entity_sku_flag` int,\n" +
                "\t`entity_combinesku_id` varchar,\n" +
                "\t`cdc_ts` timestamp,\n" +
                "\t`del_flag` int,\n" +
                "\t`sku_flag_new` int,\n" +
                "\t`drp_co_id_from` int,\n" +
                "\t`drp_co_id_to` int,\n" +
                "\t`supplier_name` varchar,\n" +
                "\t`wh_id` int,\n" +
                "\t`warehouse` varchar,\n" +
                "\t`combine_sku_id` varchar,\n" +
                "\t`creator` bigint,\n" +
                "\t`des` varchar,\n" +
                "\t`skuid_row_number` int,\n" +
                "\t`order_creator_name` varchar,\n" +
                "\t`order_creator` bigint,\n" +
                "\tPRIMARY KEY (co_id, o_id, as_id, asi_id, sku_id, sku_flag, entity_combinesku_id)\n" +
                ") INDEX_ALL = 'Y' COMMENT 'comments'\n" +
                "DISTRIBUTE BY HASH(`o_id`)\n" +
                "REPLACE \n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM t;", stmt.toString());
    }

    @Test
    public void test_6() {
        SQLUtils.parseSingleMysqlStatement("CREATE TABLE `aftersale_item_s3combine_1000` (\n" +
                "\t`order_status` varchar,\n" +
                "\t`is_cod` int,\n" +
                "\t`so_id` varchar,\n" +
                "\t`type` varchar,\n" +
                "\t`created` timestamp,\n" +
                "\t`creator_name` varchar,\n" +
                "\t`modifier_name` varchar,\n" +
                "\t`status` varchar,\n" +
                "\t`remark` varchar,\n" +
                "\t`question_type` varchar,\n" +
                "\t`refund` decimal(16, 4),\n" +
                "\t`payment` decimal(16, 4),\n" +
                "\t`shop_buyer_id` varchar,\n" +
                "\t`shop_id` int,\n" +
                "\t`logistics_company` varchar,\n" +
                "\t`l_id` varchar,\n" +
                "\t`good_status` varchar,\n" +
                "\t`order_type` varchar,\n" +
                "\t`drp_co_id` int,\n" +
                "\t`wms_co_id` int,\n" +
                "\t`tag` int,\n" +
                "\t`confirm_date` timestamp,\n" +
                "\t`asi_id` bigint,\n" +
                "\t`as_id` bigint,\n" +
                "\t`o_id` bigint,\n" +
                "\t`co_id` int,\n" +
                "\t`sku_id` varchar,\n" +
                "\t`outer_oi_id` varchar,\n" +
                "\t`sku_type` varchar,\n" +
                "\t`db_name` varchar,\n" +
                "\t`table_name` varchar,\n" +
                "\t`db_id` int,\n" +
                "\t`tb_id` int,\n" +
                "\t`cost_price` decimal(12, 4),\n" +
                "\t`base_amount` decimal(16, 4),\n" +
                "\t`bf_amount` decimal(16, 4),\n" +
                "\t`return_amount` decimal(16, 4),\n" +
                "\t`bf_qty` int,\n" +
                "\t`return_qty` int,\n" +
                "\t`base_qty` int,\n" +
                "\t`r_qty` int,\n" +
                "\t`src_combine_sku_id` varchar,\n" +
                "\t`item_type` varchar,\n" +
                "\t`return_amount_share` decimal(16, 4),\n" +
                "\t`bf_amount_share` decimal(16, 4),\n" +
                "\t`asid_rownumber` int,\n" +
                "\t`bf_asid_rownumber` int,\n" +
                "\t`sku_flag` int,\n" +
                "\t`sku_row_number` int,\n" +
                "\t`combinesku_row_number` int,\n" +
                "\t`buyer_message` varchar,\n" +
                "\t`order_remark` varchar,\n" +
                "\t`sent_flag` int,\n" +
                "\t`order_date` timestamp,\n" +
                "\t`pay_date` timestamp,\n" +
                "\t`send_date` timestamp,\n" +
                "\t`merge_so_id` varchar,\n" +
                "\t`receiver_state` varchar,\n" +
                "\t`receiver_city` varchar,\n" +
                "\t`receiver_district` varchar,\n" +
                "\t`receiver_name` varchar,\n" +
                "\t`order_from` varchar,\n" +
                "\t`presend_status` varchar,\n" +
                "\t`is_jst` int,\n" +
                "\t`seller_flag` int,\n" +
                "\t`shop_site` varchar,\n" +
                "\t`shop_buyer_id_en` varchar,\n" +
                "\t`receiver_name_en` varchar,\n" +
                "\t`dw_created` timestamp,\n" +
                "\t`properties_value` varchar,\n" +
                "\t`order_labels` varchar,\n" +
                "\t`order_l_id` varchar,\n" +
                "\t`outer_as_id` varchar,\n" +
                "\t`is_gift` int,\n" +
                "\t`end_time` timestamp,\n" +
                "\t`shop_sku_id` varchar,\n" +
                "\t`freight` decimal(16, 4),\n" +
                "\t`shop_i_id` varchar,\n" +
                "\t`as_item_so_id` varchar,\n" +
                "\t`entity_sku_flag` int,\n" +
                "\t`entity_combinesku_id` varchar,\n" +
                "\t`cdc_ts` timestamp,\n" +
                "\t`del_flag` int,\n" +
                "\t`sku_flag_new` int,\n" +
                "\t`drp_co_id_from` int,\n" +
                "\t`drp_co_id_to` int,\n" +
                "\t`supplier_name` varchar,\n" +
                "\t`wh_id` int,\n" +
                "\t`warehouse` varchar,\n" +
                "\t`combine_sku_id` varchar,\n" +
                "\t`creator` bigint,\n" +
                "\t`des` varchar,\n" +
                "\t`skuid_row_number` int,\n" +
                "\t`order_creator_name` varchar,\n" +
                "\t`order_creator` bigint,\n" +
                "\tPRIMARY KEY (co_id, o_id, as_id, asi_id, sku_id, sku_flag, entity_combinesku_id)\n" +
                ") INDEX_ALL = 'Y' COMMENT 'a'\n" +
                "DISTRIBUTE BY HASH(`o_id`)");
    }
}