package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_restore_1 extends TestCase {
    public void test_for_parameterize() throws Exception {
        String sqlTemplate = "SELECT `buyer_resource`.`RESOURCE_ID`, `buyer_resource`.`RESOURCE_PROVIDER`, `buyer_resource`.`BUYER_ID`, `buyer_resource`.`RESOURCE_TYPE`, `buyer_resource`.`SUB_RESOURCE_TYPE`\n" +
                "\t, `buyer_resource`.`STATUS`, `buyer_resource`.`START_TIME`, `buyer_resource`.`END_TIME`, `buyer_resource`.`FEATURE`, `buyer_resource`.`GMT_CREATED`\n" +
                "\t, `buyer_resource`.`GMT_MODIFIED`, `buyer_resource`.`source`, `buyer_resource`.`seller_id`, `buyer_resource`.`original_Resource_Id`, `buyer_resource`.`business_unit`\n" +
                "\t, `buyer_resource`.`resource_code`, `buyer_resource`.`OPTIONS`, `buyer_resource`.`AVAILABLE_COUNT`, `buyer_resource`.`TOTAL_COUNT`, `buyer_resource`.`OUT_INSTANCE_ID`\n" +
                "\t, `buyer_resource`.`CONSUME_ID`, `buyer_resource`.`GROUP_ID`, `buyer_resource`.`BUSINESS_ID`, `buyer_resource`.`rule`, `buyer_resource`.`market_place`\n" +
                "\t, `buyer_resource`.`VERSION`\n" +
                "FROM buyer_resource `buyer_resource`\n" +
                "WHERE `buyer_resource`.`BUYER_ID` = ?\n" +
                "\tAND `buyer_resource`.`STATUS` = ?\n" +
                "\tAND `buyer_resource`.`START_TIME` <= ?\n" +
                "\tAND `buyer_resource`.`END_TIME` >= ?\n" +
                "\tAND `buyer_resource`.`seller_id` = ?\n" +
                "\tAND (`buyer_resource`.`AVAILABLE_COUNT` IS ?)\n" +
                "LIMIT ?, ?";
        String params = "[1957025290,1,\"2017-10-16 23:34:28.519\",\"2017-10-16 23:34:28.519\",2933220011L,[0,-1],0,20]";
        params = params.replaceAll("''", "'");
        sqlTemplate = SQLUtils.formatMySql(sqlTemplate);
        String formattedSql = ParseUtil.restore(sqlTemplate, null, params);
        assertEquals("SELECT `buyer_resource`.`RESOURCE_ID`, `buyer_resource`.`RESOURCE_PROVIDER`, `buyer_resource`.`BUYER_ID`, `buyer_resource`.`RESOURCE_TYPE`, `buyer_resource`.`SUB_RESOURCE_TYPE`\n" +
                "\t, `buyer_resource`.`STATUS`, `buyer_resource`.`START_TIME`, `buyer_resource`.`END_TIME`, `buyer_resource`.`FEATURE`, `buyer_resource`.`GMT_CREATED`\n" +
                "\t, `buyer_resource`.`GMT_MODIFIED`, `buyer_resource`.`source`, `buyer_resource`.`seller_id`, `buyer_resource`.`original_Resource_Id`, `buyer_resource`.`business_unit`\n" +
                "\t, `buyer_resource`.`resource_code`, `buyer_resource`.`OPTIONS`, `buyer_resource`.`AVAILABLE_COUNT`, `buyer_resource`.`TOTAL_COUNT`, `buyer_resource`.`OUT_INSTANCE_ID`\n" +
                "\t, `buyer_resource`.`CONSUME_ID`, `buyer_resource`.`GROUP_ID`, `buyer_resource`.`BUSINESS_ID`, `buyer_resource`.`rule`, `buyer_resource`.`market_place`\n" +
                "\t, `buyer_resource`.`VERSION`\n" +
                "FROM buyer_resource `buyer_resource`\n" +
                "WHERE `buyer_resource`.`BUYER_ID` = 1957025290\n" +
                "\tAND `buyer_resource`.`STATUS` = 1\n" +
                "\tAND `buyer_resource`.`START_TIME` <= '2017-10-16 23:34:28.519'\n" +
                "\tAND `buyer_resource`.`END_TIME` >= '2017-10-16 23:34:28.519'\n" +
                "\tAND `buyer_resource`.`seller_id` = 2933220011\n" +
                "\tAND (`buyer_resource`.`AVAILABLE_COUNT` = 0 OR `buyer_resource`.`AVAILABLE_COUNT` = -1)\n" +
                "LIMIT 0, 20", formattedSql);
    }
}
