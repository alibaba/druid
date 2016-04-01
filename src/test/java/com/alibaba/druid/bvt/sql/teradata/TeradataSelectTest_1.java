package com.alibaba.druid.bvt.sql.teradata;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.TeradataTest;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.parser.TeradataStatementParser;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class TeradataSelectTest_1 extends TeradataTest{
	// case #1: general SELECT...FROM...WHERE
	public void test_select() throws Exception {
		String sql = "SELECT t1.name, t2.salary FROM employee t1, info t2  WHERE t1.name = t2.name;";
		TeradataStatementParser parser = new TeradataStatementParser(sql);
		List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        
        Assert.assertEquals(1, statementList.size());
        
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employee")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("info")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("employee", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("info", "name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("info", "salary")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	}
	
	// case #2: test ORDER BY...ASC/DESC
	public void test_orderBy() throws Exception {
		String sql = "select * from tb order by id asc,name desc";

		TeradataStatementParser parser = new TeradataStatementParser(sql);
		List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
               
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();

        Assert.assertNotNull(queryBlock.getOrderBy());
        System.out.println("select order by: " + queryBlock.getOrderBy().getItems().get(0).getExpr());

        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();

        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertEquals(2, visitor.getOrderByColumns().size());
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	}
	// case #3: test SELECT CONCAT...
	public void test_concat() throws Exception {
        String sql = "SELECT CONCAT(last_name,', ',first_name) AS full_name FROM mytable ORDER BY full_name;";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        print(statementList);
        Assert.assertEquals(1, statementList.size());
        
        SQLSelectStatement selectStmt = (SQLSelectStatement) statemen;
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();

        Assert.assertNotNull(queryBlock.getOrderBy());
        System.out.println("select order by: " + queryBlock.getOrderBy().getItems().get(0).getExpr());


        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }
	// case #4: test constant
	public void test_constant() throws Exception {
        String sql = "select 1";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());
        
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }
	// case #5: test sub-query
	public void test_subQuery() throws Exception {
        String sql = "SELECT COUNT(*) a FROM (select nickname,mobile,comment,createdate from ub_userdiscuss order by discuss_id desc) b  ";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());
        
        // test for sub-query
        {
        	Assert.assertTrue(queryBlock.getFrom() instanceof SQLSubqueryTableSource);
            TeradataSelectQueryBlock subBlock = (TeradataSelectQueryBlock)((SQLSubqueryTableSource)queryBlock.getFrom()).getSelect().getQuery();
            Assert.assertNotNull(subBlock.getOrderBy());	
        }
        
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertEquals(1, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ub_userdiscuss")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }

	public void test_comment() throws Exception {
        String sql = "select * from test /*!40101fff*/";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());

        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("test")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }

	public void test_not() throws Exception {
        String sql = "select a from t where not a<1 and not b>1";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());

        {
            SQLExpr where = queryBlock.getWhere();
            Assert.assertTrue(where instanceof SQLBinaryOpExpr);
            SQLBinaryOpExpr binaryWhere = (SQLBinaryOpExpr) where;
            Assert.assertEquals(binaryWhere.getOperator(), SQLBinaryOperator.BooleanAnd);

            Assert.assertTrue(binaryWhere.getLeft() instanceof SQLNotExpr);
            Assert.assertTrue(binaryWhere.getRight() instanceof SQLNotExpr);
            System.out.println("left expr: " + ((SQLNotExpr)binaryWhere.getLeft()).getExpr());
        }

        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }
	
	public void test_interval() throws Exception {
        String sql = "select bsvariety, max(bsh) as bsh, min(bsl) as bsl "
                + " from   exchange_market_info "
                + " where bsdate>(current_date - interval '1' DAY)"
                + " group by bsvariety;";
        
        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);
       
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsvariety")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsh")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsl")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("exchange_market_info", "bsdate")));
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");

    }
	
}
