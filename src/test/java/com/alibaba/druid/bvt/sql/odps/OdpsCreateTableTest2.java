package com.alibaba.druid.bvt.sql.odps;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsCreateTableTest2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE sale_detail as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE TABLE sale_detail"
                + "\nAS"
                + "\nSELECT *" //
                + "\nFROM dual;", output);
    }
}
