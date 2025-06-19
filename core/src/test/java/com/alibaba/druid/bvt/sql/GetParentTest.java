package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import org.junit.Assert;
import org.junit.Test;

public class GetParentTest {
    @Test
    public void test() {
        SQLObject obj1 = new SQLIdentifierExpr("1");
        SQLObject obj2 = new SQLIdentifierExpr("2");
        obj2.setParent(obj1);
        SQLObject obj3 = new SQLIdentifierExpr("3");
        obj3.setParent(obj2);
        Assert.assertEquals(obj3, obj3.getParent(-1));
        Assert.assertEquals(obj3, obj3.getParent(0));
        Assert.assertEquals(obj2, obj3.getParent(1));
        Assert.assertEquals(obj1, obj3.getParent(2));
        Assert.assertNull(obj3.getParent(3));
        Assert.assertNull(obj3.getParent(4));
    }
}
