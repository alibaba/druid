package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGInsertTest0 extends PGTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO films VALUES ('UA502', 'Bananas', 105, '1971-07-13', 'Comedy', '82 minutes');";

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

        Assert.assertEquals(0, visitor.getFields().size());
        
//        Assert.assertTrue(visitor.getFields().contains(new TableStat.Column("films", "producer_id")));
    }

    
}
