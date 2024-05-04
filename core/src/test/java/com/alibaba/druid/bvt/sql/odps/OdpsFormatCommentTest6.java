package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import junit.framework.TestCase;

public class OdpsFormatCommentTest6 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select *" //
                + "\nfrom t "//
                + "\nwhere status = '20' -- comment xxx"
                + "\nand flag & 127 > 0 -- comment kkkkk"
                + "\n;";//
        SQLStatement stmt = SQLUtils
            .parseSingleStatement(sql, DbType.odps, SQLParserFeature.KeepComments,
                SQLParserFeature.EnableSQLBinaryOpExprGroup);
        System.out.println("第一次生成的sql==="+stmt.toString());
        SQLStatement stmt2 = SQLUtils
            .parseSingleStatement(stmt.toString(), DbType.odps,SQLParserFeature.KeepComments,
                SQLParserFeature.EnableSQLBinaryOpExprGroup);
        System.out.println("第二次生成的sql==="+stmt2.toString());
        Assert.assertEquals("SELECT *"
                + "\nFROM t"
                + "\nWHERE status = '20' -- comment xxx"
                + "\n\tAND flag & 127 > 0 -- comment kkkkk;", SQLUtils.formatOdps(sql));
    }

}
