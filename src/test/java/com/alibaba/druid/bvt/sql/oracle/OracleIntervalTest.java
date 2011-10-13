package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleIntervalTest extends TestCase {

    public void test_interval_literal() throws Exception {
        String sql = "SELECT INTERVAL '123-2' YEAR(3) TO MONTH FROM DUAL";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals("SELECT INTERVAL '123-2' YEAR(3) TO MONTH\nFROM DUAL;\n", text);

        System.out.println(text);
    }
    
    public void test_interval_literal_1() throws Exception {
        String sql = "SELECT INTERVAL '123' YEAR(3) FROM DUAL";
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        
        String text = TestUtils.output(stmt);
        
        Assert.assertEquals("SELECT INTERVAL '123' YEAR(3)\nFROM DUAL;\n", text);
        
        System.out.println(text);
    }
    
    public void test_interval_literal_2() throws Exception {
        String sql = "SELECT INTERVAL '5-3' YEAR TO MONTH + INTERVAL'20' MONTH FROM DUAL";
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        
        String text = TestUtils.output(stmt);
        
        Assert.assertEquals("SELECT INTERVAL '5-3' YEAR TO MONTH + INTERVAL '20' MONTH\nFROM DUAL;\n", text);
        
        System.out.println(text);
    }
    
    public void test_interval_literal_3() throws Exception {
        String sql = "SELECT INTERVAL '6-11' YEAR TO MONTH FROM DUAL";
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        
        String text = TestUtils.output(stmt);
        
        Assert.assertEquals("SELECT INTERVAL '6-11' YEAR TO MONTH\nFROM DUAL;\n", text);
        
        System.out.println(text);
    }
    
    public void test_interval_literal_4() throws Exception {
        String sql = "SELECT INTERVAL '4 5:12:10.222' DAY TO SECOND(3) FROM DUAL";
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        
        String text = TestUtils.output(stmt);
        
        Assert.assertEquals("SELECT INTERVAL '4 5:12:10.222' DAY TO SECOND(3)\nFROM DUAL;\n", text);
        
        System.out.println(text);
    }
    
    public void test_interval_literal_5() throws Exception {
        String sql = "SELECT INTERVAL '30.12345' SECOND(2,4) FROM DUAL";
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);
        
        String text = TestUtils.output(stmt);
        
        Assert.assertEquals("SELECT INTERVAL '30.12345' SECOND(2, 4)\nFROM DUAL;\n", text);
        
        System.out.println(text);
    }
    
    public void test_interval() throws Exception {
        String sql = "SELECT (SYSTIMESTAMP - order_date) DAY(9) TO SECOND from orders WHERE order_id = 2458;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals("SELECT (SYSTIMESTAMP - order_date) DAY(9) TO SECOND\n" + "FROM orders\n"
                            + "WHERE order_id = 2458;\n", text);

        System.out.println(text);
    }
}
