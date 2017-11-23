package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
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
	     Assert.assertEquals("SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ DISTINCT e.id\nFROM ord_order_item e\nWHERE e.F1 = DATE '2011-10-01'", newSQL);
	     
	}
	
	public void test_hint2() throws Exception {
		String sql = "SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ distinct e.id from ord_order_item e where e.F1 = Date '2011-10-01'";
		String countSQL = PagerUtils.count(sql, JdbcUtils.ORACLE);		
		Assert.assertEquals("SELECT /*+leading(e) index(e ORD_ORDER_ITEM_GS_BS_DI_IND)*/ COUNT(DISTINCT e.id)\nFROM ord_order_item e\nWHERE e.F1 = DATE '2011-10-01'", countSQL);
	}
	
	public void test_hint3() throws Exception {
        String sql = "SELECT /*+index(a MTN_SMS_LOG_PK)*/ * from MTN_SMS_LOG a";
        String formattedSql = SQLUtils.formatOracle(sql);
        Assert.assertEquals("SELECT /*+index(a MTN_SMS_LOG_PK)*/ *" + "\nFROM MTN_SMS_LOG a", formattedSql);
    }

    public void test_hint4() throws Exception {
        String sql = "UPDATE /*+index(a MTN_SMS_LOG_PK)*/  MTN_SMS_LOG  a SET GMT_MODIFIED = sysdate WHERE id=1";
        String formattedSql = SQLUtils.formatOracle(sql);
        Assert.assertEquals("UPDATE /*+index(a MTN_SMS_LOG_PK)*/ MTN_SMS_LOG a"
                + "\nSET GMT_MODIFIED = SYSDATE"
                + "\nWHERE id = 1", formattedSql);
    }

    public void test_hint5() throws Exception {
        String sql = "SELECT /*+index(clk) use_nl(clk) */ distinct log.id log_id from t";
        String formattedSql = SQLUtils.formatOracle(sql);
        Assert.assertEquals("SELECT /*+index(clk) use_nl(clk) */ DISTINCT log.id AS log_id" + "\nFROM t", formattedSql);
    }
    
    public void test_hint6() throws Exception {
        String sql = "insert /*+APPEND*/ into emp_new select a.no, sysdate, a.name, b.service_duration from emp a, work b where a.no=b.no";
        String formattedSql = SQLUtils.formatOracle(sql);
        Assert.assertEquals("INSERT /*+APPEND*/ INTO emp_new" + "\nSELECT a.no, SYSDATE, a.name, b.service_duration"
                            + "\nFROM emp a, work b" + "\nWHERE a.no = b.no", formattedSql);
    }
    
    public void test_hint7() throws Exception {
        String sql = "delete /*+PARALLEL(semp, 5) */ from semp";
        String formattedSql = SQLUtils.formatOracle(sql);
        Assert.assertEquals("DELETE /*+PARALLEL(semp, 5) */ FROM semp", formattedSql);
    }
    
    
}
