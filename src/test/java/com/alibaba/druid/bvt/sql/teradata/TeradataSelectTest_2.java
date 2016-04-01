package com.alibaba.druid.bvt.sql.teradata;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.TeradataTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.parser.TeradataStatementParser;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class TeradataSelectTest_2 extends TeradataTest{
	public void test_crossJoin() throws Exception {
        String sql = "SELECT * FROM t1 CROSS JOIN t2 ON t1.id = t2.id;";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());
        {
        	SQLSelectStatement selectStmt = (SQLSelectStatement) statement;
    		SQLSelect selectSQL = selectStmt.getSelect();
            TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) selectSQL.getQuery();
    	    
            Assert.assertTrue(queryBlock.getFrom() instanceof SQLJoinTableSource);
            
            SQLJoinTableSource joinSource = (SQLJoinTableSource) queryBlock.getFrom();
            
            Assert.assertEquals(joinSource.getJoinType().toString(), "CROSS_JOIN");
            Assert.assertEquals(joinSource.getLeft().toString(), "t1");
            Assert.assertEquals(joinSource.getRight().toString(), "t2");
            Assert.assertNotNull(joinSource.getCondition());
        }
        
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
    }
	
	public void test_leftJoin() throws Exception {
        String sql = "SELECT host.id as id" //
                + ",   host.item_id as itemId" //
                + ",   host.node_id as nodeId" //
                + ",   host.node_type as nodeType" //
                + ",   host.begin_time as beginTime" //
                + ",   host.end_time as endTime" //
                + ",   host.gmt_create as gmtCreate" //
                + ",   host.gmt_modify as gmtModify" //
                + ",   host.reason as reason" //
                + ",   host.creator_id as creatorId" //
                + ",   host.modifier_id as modifierId" //
                + ",   user.name as creator" //
                + ",   user.name as modifier" //
                + ",   user.nick_name as nickName   " //
                + " FROM notice_close_node host left join sys_user user on user.id = host.modifier_id";

        TeradataStatementParser parser = new TeradataStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        {
        	SQLSelectStatement selectStmt = (SQLSelectStatement) statemen;
    		SQLSelect selectSQL = selectStmt.getSelect();
            TeradataSelectQueryBlock queryBlock = (TeradataSelectQueryBlock) selectSQL.getQuery();
    	    
            Assert.assertTrue(queryBlock.getFrom() instanceof SQLJoinTableSource);
            
            SQLJoinTableSource joinSource = (SQLJoinTableSource) queryBlock.getFrom();
            
            Assert.assertEquals(joinSource.getJoinType().toString(), "LEFT_OUTER_JOIN");
            Assert.assertEquals(joinSource.getLeft().toString(), "notice_close_node");
            Assert.assertEquals(joinSource.getRight().toString(), "sys_user");
            Assert.assertNotNull(joinSource.getCondition());
        }
        
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(15, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getColumns().contains(new Column("sys_user", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("notice_close_node", "modifier_id")));
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	}
}
