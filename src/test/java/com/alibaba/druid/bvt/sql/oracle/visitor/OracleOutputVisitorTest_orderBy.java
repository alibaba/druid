package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleOutputVisitorTest_orderBy extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT salary from employee order by name";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("employee"));

        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("employee", "salary")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("employee", "name")));

        StringBuilder buf = new StringBuilder();
        OracleOutputVisitor outputVisitor = new OracleOutputVisitor(buf);
        stmt.accept(outputVisitor);
        Assert.assertEquals("SELECT salary\nFROM employee\nORDER BY name;\n", buf.toString());

    }
}
