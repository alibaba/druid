package com.alibaba.druid.bvt.sql.postgresql.ddl;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;


public class PGAlterTableAddColumnTest2 extends PGTest {
    public void test_0 () throws Exception {
        String sql = "ALTER TABLE t_user ADD column aaa VARCHAR(10)";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals("ALTER TABLE t_user\n" +
                "\tADD COLUMN aaa VARCHAR(10)", stmt.toString());

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_user")));
        
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("t_user")).getAlterCount() == 1);

        Assert.assertTrue(visitor.getColumns().size() == 1);
    }
}
