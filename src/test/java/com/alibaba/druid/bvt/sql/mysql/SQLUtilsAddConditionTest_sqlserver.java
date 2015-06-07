package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;


public class SQLUtilsAddConditionTest_sqlserver extends TestCase {
    public void test_select() throws Exception {
        Assert.assertEquals("SELECT *" //
                + "\nFROM t" //
                + "\nWHERE id = 0", SQLUtils.addCondition("select * from t", "id = 0", "sqlserver"));
    }
    
    public void test_select_1() throws Exception {
        Assert.assertEquals("SELECT *" //
                + "\nFROM t" //
                + "\nWHERE id = 0" //
                + "\n\tAND name = 'aaa'", SQLUtils.addCondition("select * from t where id = 0", "name = 'aaa'", "sqlserver"));
    }
    
    public void test_delete() throws Exception {
        Assert.assertEquals("DELETE FROM t" //
                + "\nWHERE id = 0", SQLUtils.addCondition("delete from t", "id = 0", "sqlserver"));
    }
    
    public void test_delete_1() throws Exception {
        Assert.assertEquals("DELETE FROM t" //
                + "\nWHERE id = 0" //
                + "\n\tAND name = 'aaa'", SQLUtils.addCondition("delete from t where id = 0", "name = 'aaa'", "sqlserver"));
    }
    
    
    public void test_update() throws Exception {
        Assert.assertEquals("UPDATE t"//
                + "\nSET f1 = ?" //
                + "\nWHERE id = 0", SQLUtils.addCondition("update t set f1 = ?", "id = 0", "sqlserver"));
    }
    
    public void test_update_1() throws Exception {
        Assert.assertEquals("UPDATE t"//
                + "\nSET f1 = ?" //
                + "\nWHERE id = 0"
                + "\n\tAND name = 'bb'", SQLUtils.addCondition("update t set f1 = ? where id = 0", "name = 'bb'", "sqlserver"));
    }
}
