package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbBuilderTest {

    @Test
    public void test_delete_builder() throws Exception {
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(JdbcConstants.YASHANDB);

        builder.from("employees")
                .whereAnd("salary < 3000");

        String sql = builder.toString();
        assertNotNull(sql);
        assertTrue(sql.contains("DELETE FROM employees"));
        assertTrue(sql.contains("WHERE salary < 3000"));
    }

    @Test
    public void test_update_builder() throws Exception {
        SQLUpdateBuilder builder = SQLBuilderFactory.createUpdateBuilder(JdbcConstants.YASHANDB);

        builder.from("employees")
                .whereAnd("id = 1")
                .set("salary = salary + 1000");

        String sql = builder.toString();
        assertNotNull(sql);
        assertTrue(sql.contains("UPDATE employees"));
        assertTrue(sql.contains("SET salary = salary + 1000"));
        assertTrue(sql.contains("WHERE id = 1"));
    }

    @Test
    public void test_delete_builder_returns_oracle_statement() throws Exception {
        // Verify that YashanDB creates Oracle-style delete statements
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(JdbcConstants.YASHANDB);
        builder.from("t1").whereAnd("c1 = 1");
        String sql = builder.toString();

        // Oracle DELETE uses same format
        SQLDeleteBuilder oracleBuilder = SQLBuilderFactory.createDeleteBuilder(JdbcConstants.ORACLE);
        oracleBuilder.from("t1").whereAnd("c1 = 1");
        assertEquals(sql, oracleBuilder.toString());
    }

    @Test
    public void test_update_builder_returns_oracle_statement() throws Exception {
        // Verify that YashanDB creates Oracle-style update statements
        SQLUpdateBuilder builder = SQLBuilderFactory.createUpdateBuilder(JdbcConstants.YASHANDB);
        builder.from("t1").whereAnd("c1 = 1").set("c2 = 'x'");
        String sql = builder.toString();

        SQLUpdateBuilder oracleBuilder = SQLBuilderFactory.createUpdateBuilder(JdbcConstants.ORACLE);
        oracleBuilder.from("t1").whereAnd("c1 = 1").set("c2 = 'x'");
        assertEquals(sql, oracleBuilder.toString());
    }
}
