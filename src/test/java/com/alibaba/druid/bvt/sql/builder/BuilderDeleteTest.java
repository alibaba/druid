package com.alibaba.druid.bvt.sql.builder;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.util.JdbcConstants;

public class BuilderDeleteTest extends TestCase {

    public void test_0() throws Exception {
        SQLDeleteBuilder builder = SQLBuilderFactory.createDeleteBuilder(JdbcConstants.MYSQL);

        builder //
        .from("mytable") //
        .whereAnd("f1 > 0") //
        ;

        String sql = builder.toString();
        System.out.println(sql);
        Assert.assertEquals("DELETE FROM mytable" //
                            + "\nWHERE f1 > 0", sql);
    }
}
