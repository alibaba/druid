package com.alibaba.druid.bvt.sql.postgresql.ddl;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;


public class PGAlterTableSetDefault extends PGTest {
    public void test_0 () throws Exception {
        String sql = "ALTER TABLE products ALTER COLUMN price SET DEFAULT 7.77;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);
        
        Assert.assertEquals("ALTER TABLE products"
                + "\n\tALTER COLUMN price SET DEFAULT 7.77;", stmt.toString());

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("products")));
        
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("products")).getDropCount() == 0);
        Assert.assertTrue(visitor.getTables().get(new TableStat.Name("products")).getAlterCount() == 1);

        Assert.assertTrue(visitor.getColumns().size() == 1);
    }
}
