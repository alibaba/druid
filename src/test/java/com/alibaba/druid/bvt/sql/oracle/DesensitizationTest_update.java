package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 19/06/2017.
 */
public class DesensitizationTest_update extends TestCase {
    public void test_for_desensitization() throws Exception {
        String sql = "UPDATE customers\n" +
                "SET last_name = 'Anderson'\n" +
                "WHERE customer_id = 5000;";

        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);

        String desens_Sql = SQLUtils.format(sql, JdbcConstants.ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);

        assertEquals("UPDATE T_0C9879D1E6FFA3BE\n" +
                "SET last_name = ?\n" +
                "WHERE customer_id = ?;", desens_Sql);
    }
}
