package com.alibaba.druid.bvt.sql.builder;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLSelectBuilder;
import com.alibaba.druid.util.JdbcConstants;

public class BuilderSelectTest extends TestCase {

    public void test_0() throws Exception {
        SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(JdbcConstants.MYSQL);

        builder.from("mytable");
        builder.select("f1", "f2", "f3 F3", "count(*) cnt");
        builder.groupBy("f1");
        builder.having("count(*) > 1");
        builder.orderBy("f1", "f2 desc");
        builder.whereAnd("f1 > 0");

        String sql = builder.toString();
        System.out.println(sql);
        Assert.assertEquals("SELECT f1, f2, f3 AS F3, COUNT(*) AS cnt\n" +
                "FROM mytable\n" +
                "WHERE f1 > 0\n" +
                "GROUP BY f1\n" +
                "HAVING COUNT(*) > 1\n" +
                "ORDER BY f1, f2 DESC", sql);
    }
}
