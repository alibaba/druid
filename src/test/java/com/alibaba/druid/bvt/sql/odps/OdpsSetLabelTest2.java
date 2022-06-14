package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsSetLabelTest2 extends TestCase {
    public void test_odps() throws Exception {
        String sql = "SET LABEL S3 TO TABLE xx(f1,f2)";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SET LABEL S3 TO TABLE xx(f1, f2)", output);
    }

    public void test_odps_1() throws Exception {
        String sql = "set com.alibaba.security.airbus.udf.category.table=adl_tb_category_data,adl_cbu_category_data;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SET com.alibaba.security.airbus.udf.category.table = adl_tb_category_data,adl_cbu_category_data;", output);
    }
}
