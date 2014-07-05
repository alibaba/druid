package com.alibaba.druid.bvt.sql.mysql;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

public class SQLUtilsTest extends TestCase {

    public void test_format() throws Exception {
        String formattedSql = SQLUtils.format("select * from t where id = ?", JdbcConstants.MYSQL,
                                              Arrays.<Object> asList("abc"));
        Assert.assertEquals("SELECT *" + //
                            "\nFROM t" + //
                            "\nWHERE id = 'abc'", formattedSql);
    }
    
    public void test_format_1() throws Exception {
        String sql = "select * from t where tname LIKE \"%\"'温'\"%\"";
        String formattedSql = SQLUtils.formatMySql(sql);
        Assert.assertEquals("SELECT *\nFROM t\nWHERE tname LIKE CONCAT('%', '温', '%')", formattedSql);
    }
    
    public void test_format_2() throws Exception {
        String sql = "begin\n" + " if (a=10) then\n" + " null;\n" + " else\n" + " null;\n" + " end if;\n" + "end;";
        System.out.println(SQLUtils.formatOracle(sql));
    }
}
