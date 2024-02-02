package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

/**
 * test
 */
public class SQLCommentTest  extends TestCase {
    // issues 5708
    public void test1(){
        String sqlStr = "SELECT \n" +
                "test1, -- test1的注释\n" +
                "test2 -- test2的注释\n" +
                "FROM \n" +
                "S371_BSD_O_IDCS";
        SQLStatement sqlStatement1 = SQLUtils.parseSingleStatement(sqlStr, DbType.db2, true);
        System.out.println(sqlStatement1);
    }
    // issues 5709
    public void test2(){
        String sqlStr = "INSERT INTO S371_BSD_O_IDCS(\n" +
                "AAAA  -- AAAA\n" +
                ",BBBB   -- BBBB\n" +
                ",CCCC   -- CCCC\n" +
                ",DDDD   -- DDDD\n" +
                ")\n" +
                "\n" +
                "\t(SELECT \n" +
                "\tAAAA,  -- AAAA\n" +
                "\tBBBB,-- BBBB\n" +
                "\tCCCC,-- CCCC\n" +
                "\tDDDD -- DDDD\n" +
                "\tFROM TABLE_1\n" +
                "\tUNION\n" +
                "\tSELECT AAAA,BBBB,CCCC,DDDD FROM TABLE_2\n" +
                "\tUNION\n" +
                "\tSELECT AAAA,BBBB,CCCC,DDDD FROM TABLE_3)";
        SQLStatement sqlStatement1 = SQLUtils.parseSingleStatement(sqlStr, DbType.db2, true);
        System.out.println(sqlStatement1);
    }
}
