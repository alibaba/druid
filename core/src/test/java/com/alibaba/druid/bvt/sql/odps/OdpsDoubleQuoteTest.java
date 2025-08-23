package com.alibaba.druid.bvt.sql.odps;

import java.util.List;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class OdpsDoubleQuoteTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT my_udtf(1,2,3) as (a, b, c) from employee t1 WHERE t1.name = \"aaa\";";

        OdpsStatementParser parser = new OdpsStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        OdpsSchemaStatVisitor visitor = new OdpsSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("employee")));

        assertTrue(visitor.getColumns().contains(new Column("employee", "name")));
    }
}
