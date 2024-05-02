package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import junit.framework.TestCase;

public class OdpsFormatCommentTest13 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from t where f0 > 0 -- comment_0"
                + "\n and -- comment_1"
                + "\n f1 > 1 -- comment_2"
                + "\n and -- comment_3"
                + "\n f2 > 2 -- comment_4";
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
                + "\nWHERE f0 > 0 -- comment_0"
                + "\n\tAND -- comment_1"
                + "\n\tf1 > 1 -- comment_2"
                + "\n\tAND -- comment_3"
                + "\n\tf2 > 2 -- comment_4", SQLUtils.formatOdps(sql));
    }

}
