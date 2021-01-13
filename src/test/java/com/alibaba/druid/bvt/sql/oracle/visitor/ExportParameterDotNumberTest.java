package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import com.alibaba.druid.DbType;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

public class ExportParameterDotNumberTest extends TestCase {
	/**
	* Logger for this class
	*/
	private static final Logger LOG = LoggerFactory.getLogger(ExportParameterDotNumberTest.class);

	DbType dbType = JdbcConstants.MYSQL;

	public void test_exportParameter() throws Exception {
		String[] sqls = { "INSERT INTO test_tab1 (name) VALUES ( 2.0  )",
				 "INSERT INTO test_tab1 (name) VALUES ( 2  )",
		};
		for(String sql : sqls){
			final StringBuilder out = new StringBuilder();
			final ExportParameterVisitor visitor = new OracleExportParameterVisitor(out);
			SQLStatementParser parser = new OracleStatementParser(sql);
			final SQLStatement parseStatement = parser.parseStatement();
			parseStatement.accept(visitor);
			final List<Object> plist = visitor.getParameters();
			LOG.info("from_sql:{}",sql);
			sql = out.toString();
		
			LOG.info("to_sql:{} ==> plist:{}",sql,plist);
			
			Assert.assertTrue(plist.size()>0);
		}
	}

}
