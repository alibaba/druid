package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsShowStatisticTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "show statistic secods.xxx";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("SHOW STATISTIC secods.xxx", output);
    }
}
