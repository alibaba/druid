package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import static org.junit.Assert.*;

import java.util.List;

public class OracleXmlelementTest extends OracleTest {
    public void testOracleXmlelement() {
        String sql = "SELECT XMLELEMENT(NAME foo).getstringval() from dual";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
    }

    public void testOracleXmlelement_WithoutNameKeyword() {
        String sql = "SELECT XMLELEMENT(foo).getstringval() from dual";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
    }
}
