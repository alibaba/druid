package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest130_ads extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS hm_crm.crm_wdk_hm_store_poi_di\n" +
                "(\n" +
                "    shop_id    BIGINT COMMENT '店铺id',\n" +
                "    poi_type   BIGINT COMMENT '0：家 1： 工作',\n" +
                "    poi        VARCHAR COMMENT 'poi信息',\n" +
                "    user_count BIGINT COMMENT '用户量'\n" +
                ")\n" +
                "PARTITION BY HASH KEY (shop_id) PARTITION NUM 250\n" +
                "SUBPARTITION BY LIST KEY (biz_date long)\n" +
                "SUBPARTITION OPTIONS (available_partition_num = 1)\n" +
                "TABLEGROUP crm_platform_filter\n" +
                "OPTIONS (UPDATETYPE='batch')\n" +
                "COMMENT '店铺poi数据'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS hm_crm.crm_wdk_hm_store_poi_di (\n" +
                "\tshop_id BIGINT COMMENT '店铺id',\n" +
                "\tpoi_type BIGINT COMMENT '0：家 1： 工作',\n" +
                "\tpoi VARCHAR COMMENT 'poi信息',\n" +
                "\tuser_count BIGINT COMMENT '用户量'\n" +
                ")\n" +
                "OPTIONS (UPDATETYPE = 'batch') COMMENT '店铺poi数据'\n" +
                "PARTITION BY HASH KEY(shop_id) PARTITION NUM 250\n" +
                "SUBPARTITION BY LIST KEY (biz_date) \n" +
                "SUBPARTITION OPTIONS (available_partition_num = 1)\n" +
                "TABLEGROUP crm_platform_filter", stmt.toString());

    }




}