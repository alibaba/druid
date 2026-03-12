package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 16/9/25.
 */
public class MappingTest_insert {
    private String sql = "insert into user (id, name) values (123, 'abc')";
    Map<String, String> mapping = Collections.singletonMap("user", "user_01");

    @Test
    public void test_mapping() throws Exception {
        String result = SQLUtils.refactor(sql, null, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }

    @Test
    public void test_mapping_mysql() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.MYSQL, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }

    @Test
    public void test_mapping_pg() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.POSTGRESQL, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }

    @Test
    public void test_mapping_oracle() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.ORACLE, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }

    @Test
    public void test_mapping_sqlserver() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.SQL_SERVER, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }

    @Test
    public void test_mapping_db2() throws Exception {
        String result = SQLUtils.refactor(sql, JdbcConstants.DB2, mapping);
        assertEquals("INSERT INTO user_01 (id, name)\n" +
                "VALUES (123, 'abc')", result);
    }
}
