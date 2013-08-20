package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;

import org.junit.Assert;
import junit.framework.TestCase;

public class OracleHintTest extends TestCase {

	public void test_hint1() throws Exception {
		 String sql = "SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ distinct e.id from ord_order_item e where e.F1 = Date '2011-10-01'";
		
	     OracleStatementParser parser = new OracleStatementParser(sql);
	     List<SQLStatement> statementList = parser.parseStatementList();
	     SQLStatement stmt = statementList.get(0);
	     
	     StringBuilder out = new StringBuilder();
	     stmt.accept(new OracleOutputVisitor(out, true));

	     String newSQL = out.toString();	     
	     Assert.assertEquals("SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ DISTINCT e.id\nFROM ord_order_item e\nWHERE e.F1 = DATE '2011-10-01';\n", newSQL); 
	     
	}
	
	public void test_hint2() throws Exception {
		String sql = "SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ distinct e.id from ord_order_item e where e.F1 = Date '2011-10-01'";
		String countSQL = PagerUtils.count(sql, JdbcUtils.ORACLE);		
		Assert.assertEquals("SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ DISTINCT COUNT(*)\nFROM ord_order_item e\nWHERE e.F1 = DATE '2011-10-01'", countSQL);
	}
}
