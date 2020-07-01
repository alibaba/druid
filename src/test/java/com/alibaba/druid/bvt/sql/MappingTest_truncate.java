package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_truncate extends TestCase {
    private String sql = "truncate table user";
    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }

    public void test_mapping_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.MYSQL, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }

    public void test_mapping_pg() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.POSTGRESQL, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }

    public void test_mapping_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.ORACLE, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }

    public void test_mapping_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.SQL_SERVER, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }

    public void test_mapping_db2() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.DB2, mapping);
        assertEquals("TRUNCATE TABLE user_01", result);
    }
}
