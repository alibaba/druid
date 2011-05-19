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

public class OracleSQLParserResourceTest2 extends TestCase {

    public void f_test_1() throws Exception {
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bvt/parser/oracle-0.txt");
        Reader reader = new InputStreamReader(is);
        String sql = JdbcUtils.read(reader);
        JdbcUtils.close(reader);
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        String text = output(statementList);
        System.out.println(text);
    }
    
    public void test_2() throws Exception {
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bvt/parser/oracle-1.txt");
        Reader reader = new InputStreamReader(is);
        String sql = JdbcUtils.read(reader);
        JdbcUtils.close(reader);
        
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        String text = output(statementList);
        System.out.println(text);
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
