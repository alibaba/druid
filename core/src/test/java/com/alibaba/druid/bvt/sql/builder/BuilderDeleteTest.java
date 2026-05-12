package com.alibaba.druid.bvt.sql.builder;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderDeleteTest {
    @Test
    public void test_0() throws Exception {
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(JdbcConstants.MYSQL);

        builder.from("mytable")
                .whereAnd("f1 > 0");

        String sql = builder.toString();
        System.out.println(sql);
        assertEquals("DELETE FROM mytable"
                + "\nWHERE f1 > 0", sql);
    }
}
