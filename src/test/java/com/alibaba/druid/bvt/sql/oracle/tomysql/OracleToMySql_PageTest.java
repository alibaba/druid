package com.alibaba.druid.bvt.sql.oracle.tomysql;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;

public class OracleToMySql_PageTest extends TestCase {

    public void test_page() throws Exception {
        String sql = "SELECT XX.*, ROWNUM AS RN" + //
                     "\nFROM (SELECT *" + //
                     "\n\tFROM t" + //
                     "\n\tORDER BY id" + //
                     "\n\t) XX" + //
                     "\nWHERE ROWNUM <= 10";

        String mysqlSql = SQLUtils.translateOracleToMySql(sql);
        Assert.assertEquals("SELECT *"//
                            + "\nFROM t"//
                            + "\nORDER BY id"//
                            + "\nLIMIT 10", mysqlSql);
        System.out.println(mysqlSql);
    }
    
    
    public void test_page1() throws Exception {
        String sql = "select * from t_xiaoxi where rowid in(select rid from (select rownum rn,rid from(select rowid rid,cid from t_xiaoxi  order by cid desc) where rownum<10000) where rn>9980) order by cid desc;";

        //sql = "SELECT rid  FROM ( SELECT rownum AS rn, rid FROM ( SELECT rowid AS rid, cid FROM t_xiaoxi ORDER BY cid DESC )  WHERE rownum < 10000 ) WHERE rn > 9980";
        String mysqlSql = SQLUtils.translateOracleToMySql(sql);
        
        System.out.println(mysqlSql);
    }
}
  