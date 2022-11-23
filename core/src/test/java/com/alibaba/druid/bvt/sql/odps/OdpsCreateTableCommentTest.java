package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

public class OdpsCreateTableCommentTest {
	@Test
	public void testNullComment(){
		String sqlContent = "CREATE TABLE IF NOT EXISTS test_table \n" +
				"(\n" +
				"  user_id STRING COMMENT\"userid\"\n" +
				"  ,user_features STRING COMMENT\"用户特征\"\n" +
				"  ,column_without_comment STRING\n" +
				")\n" +
				";";

		SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(
				sqlContent,
				DbType.odps,
				SQLParserFeature.KeepSourceLocation,
				SQLParserFeature.KeepComments
		);
		List<SQLStatement> sqlStatements = parser.parseStatementList();
		SQLStatement sqlStatement = sqlStatements.get(0);
		((SQLCreateTableStatement) sqlStatement).getColumnComments();
	}
}
