package com.alibaba.druid.sql.mysql.bvt;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

public class MySqlParameterizedOutputVisitorTest extends TestCase {

    public void test_0() throws Exception {
        validate("SELECT * FROM T WHERE ID IN (?, ?, ?)", "SELECT * FROM T WHERE ID IN (?)");
        validate("SELECT * FROM T WHERE ID = 5", "SELECT * FROM T WHERE ID = ?");
        validate("SELECT * FROM T WHERE 1 = 0 AND ID = 5", "SELECT * FROM T WHERE 1 = 0 AND ID = ?");
        validate("SELECT * FROM T WHERE ID = ? OR ID = ?", "SELECT * FROM T WHERE ID = ?");
        validate("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT * FROM T WHERE A.ID = ?");
        validate("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ?", "SELECT * FROM T WHERE 1 = 0 OR a.id = ?");
        validateOracle("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT * FROM T WHERE A.ID = ?; ");
        validate("INSERT INTO T (F1, F2) VALUES(?, ?), (?, ?), (?, ?)", "INSERT INTO T (F1, F2) VALUES (?, ?)");
        
        //System.out.println("SELECT * FORM A WHERE ID = (##)".replaceAll("##", "?"));
    }

    void validate(String sql, String expect) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
        statemen.accept(visitor);

        Assert.assertEquals(expect, out.toString());
    }
    
    void validateOracle(String sql, String expect) {

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        OracleParameterizedOutputVisitor visitor = new OracleParameterizedOutputVisitor(out);
        statemen.accept(visitor);

        Assert.assertEquals(expect, out.toString());
    }
}
