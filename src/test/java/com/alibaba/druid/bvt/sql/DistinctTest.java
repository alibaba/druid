package com.alibaba.druid.bvt.sql;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;


public class DistinctTest extends TestCase {
    private String sql = "select count(distinct *) from t";
    
    public void test_mysql() throws Exception {
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\nFROM t", SQLUtils.format(sql, JdbcUtils.MYSQL));
    }
    
    public void test_oracle() throws Exception {
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\nFROM t", SQLUtils.format(sql, JdbcUtils.ORACLE));
    }
    
    public void test_postgres() throws Exception {
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\nFROM t", SQLUtils.format(sql, JdbcUtils.POSTGRESQL));
    }
    
    public void test_sql92() throws Exception {
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\nFROM t", SQLUtils.format(sql, null));
    }
}
