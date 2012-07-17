package com.alibaba.druid.bvt.sql;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;


public class MybatisTest extends TestCase {
    private String sql = "select * from t where id = #{id}";
    
    public void test_mysql() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = #{id}", SQLUtils.format(sql, JdbcUtils.MYSQL));
    }
    
    public void test_oracle() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = #{id}", SQLUtils.format(sql, JdbcUtils.ORACLE));
    }
    
    public void test_postgres() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = #{id}", SQLUtils.format(sql, JdbcUtils.POSTGRESQL));
    }
    
    public void test_sql92() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = #{id}", SQLUtils.format(sql, null));
    }
}
