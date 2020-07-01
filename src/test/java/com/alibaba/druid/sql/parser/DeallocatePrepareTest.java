package com.alibaba.druid.sql.parser;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import junit.framework.TestCase;

public class DeallocatePrepareTest extends TestCase {

	public void test() {
		String sql = "DEALLOCATE PREPARE stmt1";
		MySqlStatementParser parser = new MySqlStatementParser(sql);
		SQLStatement stmt =parser.parseStatement();
		Assert.assertEquals(MysqlDeallocatePrepareStatement.class, stmt.getClass());
		MysqlDeallocatePrepareStatement dpStmt = (MysqlDeallocatePrepareStatement) stmt;
		Assert.assertEquals("stmt1", dpStmt.getStatementName().getSimpleName());
		Assert.assertEquals(sql, dpStmt.toString());
	}
	
}
