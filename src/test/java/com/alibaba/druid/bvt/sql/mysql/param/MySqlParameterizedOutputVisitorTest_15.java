package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_15 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        String sql = "INSERT INTO mycart.`member_cart_0172` (`cart_id`, `sku_id`, `item_id`, `quantity`, `user_id`\n" +
                ", `status`, `type`, `sub_type`, `gmt_create`, `gmt_modified`\n" +
                ", `attribute`, `attribute_cc`, `sync_version`, `ex1`, `ex2`\n" +
                ", `seller_id`, `ext_status`)\n" +
                "VALUES (?, ?, ?, ?, ?\n" +
                ", ?, ?, ?, ?, ?\n" +
                ", ?, ?, NULL, NULL, NULL\n" +
                ", ?, ?)\n";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("INSERT INTO mycart.member_cart (`cart_id`, `sku_id`, `item_id`, `quantity`, `user_id`\n" +
                "\t, `status`, `type`, `sub_type`, `gmt_create`, `gmt_modified`\n" +
                "\t, `attribute`, `attribute_cc`, `sync_version`, `ex1`, `ex2`\n" +
                "\t, `seller_id`, `ext_status`)\n" +
                "VALUES (?, ?, ?, ?, ?\n" +
                "\t\t, ?, ?, ?, ?, ?\n" +
                "\t\t, ?, ?, ?, ?, ?\n" +
                "\t\t, ?, ?)", psql);
    }
}
