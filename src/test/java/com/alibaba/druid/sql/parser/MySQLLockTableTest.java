package com.alibaba.druid.sql.parser;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement.LockType;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySQLLockTableTest extends TestCase {
	
	public void testLockTables() {
		String stmt1 = "lock tables tt_table read";
		MySqlStatementParser parser1 = new MySqlStatementParser(stmt1);
		MySqlLockTableStatement statment1 = (MySqlLockTableStatement) parser1.parseStatement();
		Assert.assertTrue("tt_table".equalsIgnoreCase(statment1.getTableSource().toString()));
		Assert.assertTrue(LockType.READ == statment1.getLockType());
		
		String stmt2 = "lock tables tt_table write";
		MySqlStatementParser parser2 = new MySqlStatementParser(stmt2);
		MySqlLockTableStatement statment2 = (MySqlLockTableStatement) parser2.parseStatement();
		Assert.assertTrue("tt_table".equalsIgnoreCase(statment2.getTableSource().toString()));
		Assert.assertTrue(LockType.WRITE == statment2.getLockType());
		
		String stmt3 = "lock table tt_table read";
		MySqlStatementParser parser3 = new MySqlStatementParser(stmt3);
		MySqlLockTableStatement statment3 = (MySqlLockTableStatement) parser3.parseStatement();
		Assert.assertTrue("tt_table".equalsIgnoreCase(statment3.getTableSource().toString()));
		Assert.assertTrue(LockType.READ == statment3.getLockType());
		
		String stmt4 = "lock table tt_table write";
		MySqlStatementParser parser4 = new MySqlStatementParser(stmt4);
		MySqlLockTableStatement statment4 = (MySqlLockTableStatement) parser4.parseStatement();
		Assert.assertTrue("tt_table".equalsIgnoreCase(statment4.getTableSource().toString()));
		Assert.assertTrue(LockType.WRITE == statment4.getLockType());
	}
	
	public void testUnlockTables() {
		String stmt1 = "unlock tables";
		String stmt2 = "unlock table";
		MySqlStatementParser parser1 = new MySqlStatementParser(stmt1);
		MySqlStatementParser parser2 = new MySqlStatementParser(stmt2);
		MySqlUnlockTablesStatement statment1 = (MySqlUnlockTablesStatement)parser1.parseStatement();
		MySqlUnlockTablesStatement statment2 = (MySqlUnlockTablesStatement)parser2.parseStatement();
		Assert.assertTrue(true);
	}

}
