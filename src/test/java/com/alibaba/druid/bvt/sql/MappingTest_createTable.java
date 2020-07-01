package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_createTable extends TestCase {
    String sql = "create table user (\n" +
            "source_key int,\n" +
            "source_value varchar(32),\n" +
            "primary key(source_key)\n" +
            ");";

    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    public void test_mapping_createTable() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }

    public void test_mapping_createTable_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.MYSQL, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }

    public void test_mapping_createTable_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.ORACLE, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }

    public void test_mapping_createTable_pg() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.POSTGRESQL, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }

    public void test_mapping_createTable_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.SQL_SERVER, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }

    public void test_mapping_createTable_db2() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.DB2, mapping);
        assertEquals("CREATE TABLE user_01 (\n" +
                "\tsource_key int,\n" +
                "\tsource_value varchar(32),\n" +
                "\tPRIMARY KEY (source_key)\n" +
                ");", result);
    }
}
