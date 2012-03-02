package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleExplainTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "EXPLAIN PLAN SET STATEMENT_ID='PLUS19628905' FOR select *   from( select row_.*, rownum rownum_   from( SELECT  h.id taskId, t.id tradeId, t.OUT_ORDER_ID orderId, t.SELLER_SEQ sellerSeq, t.BUYER_SEQ buyerSeq, h.RECORD_TYPE recordType, t.SELLER_LOGIN_ID sellerLoginId, t.SELLER_ADMIN_SEQ sellerAdminSeq, h.GMT_CREATE gmtTaskCreate, h.GMT_MODIFIED gmtTaskModified, h.GMT_FETCH_TASK gmtFetchTask, h.GMT_FINISH_TASK gmtFinishTask, h.STATUS status, h.OWNER owner   FROM HT_TASK_TRADE_HISTORY h, escrow_trade t  WHERE h.TRADE_ID= t.ID    and h.OWNER='zhoufei.zhangzf'    and h.STATUS in('running') ORDER BY h.TASK_FLOW_LEVEL, t.GMT_CREATE, h.GMT_MODIFIED DESC) row_ where rownum<= 100)  where rownum_>= 80";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(0, visitor.getTables().size());

        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("\"DUAL\"")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
