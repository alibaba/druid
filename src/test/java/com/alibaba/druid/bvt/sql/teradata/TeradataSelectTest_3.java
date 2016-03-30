package com.alibaba.druid.bvt.sql.teradata;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.TeradataTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.teradata.parser.TeradataStatementParser;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class TeradataSelectTest_3 extends TeradataTest {
	public void test_trim() throws Exception {
        String sql = "select trim(trailing from columnname) from dbc.columnsv;";
		
        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        Assert.assertTrue(stmt instanceof SQLSelectStatement);
        print(statementList);
        
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dbc.columnsv")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("dbc.columnsv", "columnname")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	
	}
}
