package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_alterTable extends TestCase {
    String sql = "ALTER TABLE user DROP INDEX pk_user;";

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping_createTable() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }

    public void test_mapping_createTable_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.MYSQL, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }

    public void test_mapping_createTable_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.ORACLE, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }

    public void test_mapping_createTable_pg() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.POSTGRESQL, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }

    public void test_mapping_createTable_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.SQL_SERVER, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }

    public void test_mapping_createTable_db2() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.DB2, mapping);
        assertEquals("ALTER TABLE user_01\n" +
                "\tDROP INDEX pk_user;", result);
    }
}
