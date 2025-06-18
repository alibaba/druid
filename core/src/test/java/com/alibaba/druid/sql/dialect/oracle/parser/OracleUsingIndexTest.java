package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

public class OracleUsingIndexTest {


    @Test
    public void testUsingIndex() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n" +
                "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n" +
                "\t\tUSING INDEX \"SC\".\"PK_XXX\"\n" +
                "\t\tENABLE";
        SQLStatement stat = SQLUtils.parseSingleStatement(sql, DbType.oracle,false);
        System.out.println(stat);
        Assert.assertEquals(sql, stat.toString());
        System.out.println("=============");
    }
}
