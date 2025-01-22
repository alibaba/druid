package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceTest {
    @Test
    public void test_when() {
        SQLMergeStatement.WhenInsert when = new SQLMergeStatement.WhenInsert();
        when.addColumn(new SQLIdentifierExpr("id"));
        when.addColumn(new SQLIdentifierExpr("id2"));
        SQLIdentifierExpr current_timestamp_identifier1 = new SQLIdentifierExpr("current_timestamp");
        SQLIdentifierExpr current_timestamp_identifier2 = new SQLIdentifierExpr("current_timestamp");
        when.addValue(current_timestamp_identifier1);
        when.addValue(current_timestamp_identifier2);
        SQLMethodInvokeExpr current_timestamp_method1 = new SQLMethodInvokeExpr("current_timestamp");
        SQLMethodInvokeExpr current_timestamp_method2 = new SQLMethodInvokeExpr("current_timestamp");
        SQLUtils.replaceInParent(current_timestamp_identifier1, current_timestamp_method1);
        SQLUtils.replaceInParent(current_timestamp_identifier2, current_timestamp_method2);
        Assert.assertEquals(current_timestamp_method1, when.getValues().get(0));
        Assert.assertEquals(current_timestamp_method2, when.getValues().get(1));
    }
}
