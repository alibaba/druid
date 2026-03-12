package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 19/06/2017.
 */
public class DesensitizationTest {
    @Test
    public void test_for_desensitization() throws Exception {
        String sql = "select id, name from mytable where id = 3";

        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);

        String desens_Sql = SQLUtils.format(sql, JdbcConstants.ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);

        assertEquals("SELECT id, name\n" +
                "FROM T_6635BB1415F4C1B5\n" +
                "WHERE id = ?", desens_Sql);
    }

    @Test
    public void test_for_desensitization_1() throws Exception {
        String sql = "select id, name from \"mytable\" where id = 3";

        SQLUtils.FormatOption option = new SQLUtils.FormatOption();
        option.setDesensitize(true);
        option.setParameterized(true);

        String desens_Sql = SQLUtils.format(sql, JdbcConstants.ORACLE, option);
        System.out.println(sql);
        System.out.println("-------------------");
        System.out.println(desens_Sql);

        assertEquals("SELECT id, name\n" +
                "FROM T_6635BB1415F4C1B5\n" +
                "WHERE id = ?", desens_Sql);
    }
}
