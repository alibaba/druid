package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGInsertTest2 extends PGTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO films (code, title, did, date_prod, kind) VALUES"
                     + "    ('B6717', 'Tampopo', 110, '1985-02-10', 'Comedy'), "
                     + "('HG120', 'The Dinner Game', 140, DEFAULT, 'Comedy');";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films")));

        Assert.assertEquals(5, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "kind")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "code")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "date_prod")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "title")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "did")));
    }

}
