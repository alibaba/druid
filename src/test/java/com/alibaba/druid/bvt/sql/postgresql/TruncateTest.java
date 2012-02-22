package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class TruncateTest extends PGTest {
	public void test_0() throws Exception {
		String sql = "TRUNCATE bigtable, fattable;";
		
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
        
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getFields());
        
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("bigtable")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("fattable")));
        
        Assert.assertTrue(visitor.getFields().size() == 0);
	}
}
