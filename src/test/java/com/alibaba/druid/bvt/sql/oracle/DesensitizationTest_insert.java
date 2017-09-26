package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 19/06/2017.
 */
public class DesensitizationTest_insert extends TestCase {
    public void test_for_desensitization() throws Exception {
        String sql = "INSERT INTO suppliers\n" +
                "(supplier_id, supplier_name)\n" +
                "SELECT account_no, name\n" +
                "FROM customers\n" +
                "WHERE customer_id > 5000;";

        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);

        String desens_Sql = SQLUtils.format(sql, JdbcConstants.ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);

        assertEquals("INSERT INTO T_2EBB0E6843F14AEE (supplier_id, supplier_name)\n" +
                "SELECT account_no, name\n" +
                "FROM T_0C9879D1E6FFA3BE\n" +
                "WHERE customer_id > ?;", desens_Sql);
    }
}
