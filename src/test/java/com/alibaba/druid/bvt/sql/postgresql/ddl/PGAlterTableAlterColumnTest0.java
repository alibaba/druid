package com.alibaba.druid.bvt.sql.postgresql.ddl;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;


public class PGAlterTableAlterColumnTest0 extends PGTest {
    public void test_0 () throws Exception {
        String sql = "ALTER TABLE organizations ALTER COLUMN guarded TYPE BOOLEAN, ALTER COLUMN guarded DROP NOT NULL";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals("ALTER TABLE organizations\n" +
                "\tALTER COLUMN guarded BOOLEAN,\n" +
                "\tALTER COLUMN guarded DROP NOT NULL", stmt.toString());

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("organizations")));
        
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("organizations")).getAlterCount() == 1);

        Assert.assertTrue(visitor.getColumns().size() == 1);
    }
}
