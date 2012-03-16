package com.alibaba.druid.bvt.filter.wall;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.wall.spi.WallVisitorUtils;
import com.alibaba.druid.sql.SQLUtils;

public class WallVisitorUtilsTest extends TestCase {

    public void test_isTrue() throws Exception {
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("1 != 2")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("1 != 2 AND 2 = 2")));
        Assert.assertEquals(Boolean.FALSE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("1 != 2 AND 2 != 2")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("23 = 23")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("NOT 23 != 23")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("f1 like '%'")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("f1 like '%%'")));
        Assert.assertEquals(null, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("a1 = b1 AND f1 like '%%'")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("a1 = b1 OR f1 like '%%'")));

        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("1 < 2")));
        Assert.assertEquals(Boolean.FALSE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("2 < 2")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("2 <= 2")));

        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("2 > 1")));
        Assert.assertEquals(Boolean.FALSE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("2 > 2")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("2 >= 2")));

        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("len('44') > 0")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("len('44') >= 2")));

        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("(select count(*) from t) > 0")));
        Assert.assertEquals(Boolean.TRUE,
                            WallVisitorUtils.getValue(SQLUtils.toSQLExpr("(select count(*) from t) >= 0")));
        Assert.assertEquals(Boolean.FALSE,
                            WallVisitorUtils.getValue(SQLUtils.toSQLExpr("(select count(*) from t) < 0")));
        Assert.assertEquals(Boolean.TRUE,
                            WallVisitorUtils.getValue(SQLUtils.toSQLExpr("NOT (select count(*) from t) < 0")));

        //
    }

    public void test_chr() throws Exception {
        Assert.assertEquals("CAT", WallVisitorUtils.getValue(SQLUtils.toSQLExpr("CHR(67)||CHR(65)||CHR(84)")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("CHR(67)||CHR(65)||CHR(84) = 'CAT'")));
    }
}
