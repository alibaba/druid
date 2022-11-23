package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_update extends TestCase {
    private String sql = "update user set f1 = 1 where id = 3";
    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }

    public void test_mapping_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.MYSQL, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }

    public void test_mapping_pg() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.POSTGRESQL, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }

    public void test_mapping_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.ORACLE, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }

    public void test_mapping_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.SQL_SERVER, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }

    public void test_mapping_db2() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.DB2, mapping);
        assertEquals("UPDATE user_01\n" +
                "SET f1 = 1\n" +
                "WHERE id = 3", result);
    }
}
