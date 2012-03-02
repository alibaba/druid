package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class PGSelectIntoTest extends PGTest {
	public void test_0() throws Exception {
		String sql = "SELECT * INTO films_recent FROM films WHERE date_prod >= '2002-01-01';";
		
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
        
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films_recent")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films")));
        
        Assert.assertTrue(visitor.getColumns().contains(new Column("films", "*")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("films", "date_prod")));
	}
}
