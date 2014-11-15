package com.alibaba.druid.bvt.sql.postgresql.datatypes;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;


public class BooleanTest extends PGTest {
    public void test_0() throws Exception {
        String sql = "INSERT INTO test1 VALUES (TRUE, 'sic est');";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("test1")));

        Assert.assertEquals(0, visitor.getColumns().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "kind")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "code")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "date_prod")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "title")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "did")));
    }
}
