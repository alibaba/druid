package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

/**
 * test issues 5708
 */
public class SQLCommentTest  extends TestCase {
    public void test1(){
        String sqlStr = "SELECT \n" +
                "test1, -- test1的注释\n" +
                "test2 -- test2的注释\n" +
                "FROM \n" +
                "S371_BSD_O_IDCS";
        SQLStatement sqlStatement1 = SQLUtils.parseSingleStatement(sqlStr, DbType.db2, true);
        System.out.println(sqlStatement1);
    }
}
