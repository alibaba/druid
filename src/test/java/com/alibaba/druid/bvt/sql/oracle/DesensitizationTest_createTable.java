package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 19/06/2017.
 */
public class DesensitizationTest_createTable extends TestCase {
    public void test_for_desensitization() throws Exception {
        String sql = "CREATE TABLE customers\n" +
                "( customer_id number(10) NOT NULL,\n" +
                "  customer_name varchar2(50) NOT NULL,\n" +
                "  city varchar2(50)\n" +
                ");";

        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);

        String desens_Sql = SQLUtils.format(sql, JdbcConstants.ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);

        assertEquals("CREATE TABLE T_0C9879D1E6FFA3BE (\n" +
                "\tcustomer_id number(10) NOT NULL,\n" +
                "\tcustomer_name varchar2(50) NOT NULL,\n" +
                "\tcity varchar2(50)\n" +
                ");", desens_Sql);
    }
}
