package com.alibaba.druid.bvt.sql.postgresql.create_function;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.statement.SQLBlockStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprStatement;
import junit.framework.TestCase;

public class PGCreateFunctionTest extends TestCase {

	public void test_0() throws Exception {
		SQLCreateFunctionStatement sqlCreateFunctionStatement = new SQLCreateFunctionStatement();
		sqlCreateFunctionStatement.setDbType(DbType.postgresql);
		sqlCreateFunctionStatement.setCreate(true);
		sqlCreateFunctionStatement.setName(new SQLIdentifierExpr("func1"));
		SQLBlockStatement sqlBlockStatement = new SQLBlockStatement();
		sqlCreateFunctionStatement.setBlock(sqlBlockStatement);

		// Set sql block statement
		SQLExprStatement sqlExprStatement = new SQLExprStatement();
		sqlExprStatement.setExpr(new SQLNullExpr());
		sqlExprStatement.setAfterSemi(true);
		sqlBlockStatement.setHaveBeginEnd(true);
		sqlBlockStatement.setIsDollarQuoted(true);
		sqlBlockStatement.setLanguage("plpgsql");
		List<SQLStatement> sqlExprStatementList = new ArrayList<>();
		sqlExprStatementList.add(sqlExprStatement);
		sqlBlockStatement.setStatementList(sqlExprStatementList);

		String sql = sqlCreateFunctionStatement.toString();
		assertEquals("CREATE FUNCTION func1 ()\n" +
				"AS $$\n" +
				"BEGIN\n" +
				"\tNULL;\n" +
				"END;\n" +
				"$$ LANGUAGE plpgsql;", sql);
	}
}
