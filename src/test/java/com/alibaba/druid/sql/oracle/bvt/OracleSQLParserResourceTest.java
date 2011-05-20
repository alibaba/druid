package com.alibaba.druid.sql.oracle.bvt;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.util.JdbcUtils;

public class OracleSQLParserResourceTest extends TestCase {

    public void test_0() throws Exception {
        exec_test("bvt/parser/oracle-0.txt");
        exec_test("bvt/parser/oracle-1.txt");
        exec_test("bvt/parser/oracle-2.txt");
        exec_test("bvt/parser/oracle-3.txt");
        exec_test("bvt/parser/oracle-4.txt");
        exec_test("bvt/parser/oracle-5.txt");
        exec_test("bvt/parser/oracle-6.txt"); // PARTITION
        exec_test("bvt/parser/oracle-7.txt");
        exec_test("bvt/parser/oracle-8.txt");
        exec_test("bvt/parser/oracle-9.txt");
        exec_test("bvt/parser/oracle-10.txt");
        exec_test("bvt/parser/oracle-11.txt");
        exec_test("bvt/parser/oracle-12.txt");
        exec_test("bvt/parser/oracle-13.txt");
        exec_test("bvt/parser/oracle-14.txt");
        exec_test("bvt/parser/oracle-15.txt");
        exec_test("bvt/parser/oracle-16.txt");
        exec_test("bvt/parser/oracle-17.txt");
        exec_test("bvt/parser/oracle-18.txt");
        exec_test("bvt/parser/oracle-19.txt");
        exec_test("bvt/parser/oracle-20.txt");
        exec_test("bvt/parser/oracle-21.txt");
        exec_test("bvt/parser/oracle-22.txt");
        exec_test("bvt/parser/oracle-23.txt");
        exec_test("bvt/parser/oracle-24.txt");
        exec_test("bvt/parser/oracle-25.txt");
        exec_test("bvt/parser/oracle-26.txt");
        exec_test("bvt/parser/oracle-27.txt");
        exec_test("bvt/parser/oracle-28.txt");
    }
    
    
    public void exec_test(String resource) throws Exception {
        System.out.println(resource);
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is, "UTF-8");
        String input = JdbcUtils.read(reader);
        JdbcUtils.close(reader);
        String[] items = input.split("---------------------------");
        String sql = items[0].trim();
        String expect = items[1].trim();
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        
        Assert.assertEquals(1, statementList.size());
        
        String text = output(statementList);
        System.out.println(text);
        Assert.assertEquals(expect, text.trim());
       
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }
}
