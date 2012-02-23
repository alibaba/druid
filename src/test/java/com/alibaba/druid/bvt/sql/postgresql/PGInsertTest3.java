package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGInsertTest3 extends PGTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO films SELECT * FROM tmp_films WHERE date_prod < '2004-05-07';";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getFields());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("tmp_films")));

        Assert.assertEquals(2, visitor.getFields().size());

        Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("tmp_films", "date_prod")));
        Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("tmp_films", "*")));
    }

}
