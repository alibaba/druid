package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;

public class OdpsCreateViewTest extends TestCase {

    public void test_create() throws Exception {
        String sql = "CREATE view sale_detail as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE VIEW sale_detail" //
                            + "\nAS" //
                            + "\nSELECT *" //
                            + "\nFROM dual;", output);
    }
    
    public void test_create_or_replace() throws Exception {
        String sql = "CREATE or replace view sale_detail as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE OR REPLACE VIEW sale_detail" //
                            + "\nAS" //
                            + "\nSELECT *" //
                            + "\nFROM dual;", output);
    }
    
    
    public void test_create_if_not_exists() throws Exception {
        String sql = "CREATE view if not exists sale_detail as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE VIEW IF NOT EXISTS sale_detail" //
                            + "\nAS" //
                            + "\nSELECT *" //
                            + "\nFROM dual;", output);
    }
    
    public void test_create_comments() throws Exception {
        String sql = "CREATE view if not exists sale_detail comment 'aaaa' as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE VIEW IF NOT EXISTS sale_detail" //
                            + "\nCOMMENT 'aaaa'" //
                            + "\nAS" //
                            + "\nSELECT *" //
                            + "\nFROM dual;", output);
    }
    
    public void test_create_column_comments() throws Exception {
        String sql = "CREATE view if not exists sale_detail (f1 comment 'aaaa', f2 comment 'bbb') as select * from dual;";
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toOdpsString(stmt);
        Assert.assertEquals("CREATE VIEW IF NOT EXISTS sale_detail (\n" +
                "\tf1 COMMENT 'aaaa', \n" +
                "\tf2 COMMENT 'bbb'\n" +
                ")\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM dual;", output);
    }
}
