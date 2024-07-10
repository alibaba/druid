package com.alibaba.druid.bvt.sql.postgresql.pl;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class PGGetDiagnosticsStatementTest extends PGTest {
	public void test_0() throws Exception {
		String sql = "DO $$\n" +
					 "BEGIN\n" +
					 "  GET DIAGNOSTICS n = ROW_COUNT;\n" +
					 "END $$;";

		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
		assertEquals(1, statementList.size());

		String output = SQLUtils.toSQLString(statementList, JdbcConstants.POSTGRESQL);
		assertEquals("DO $$\n" +
					 "BEGIN\n" +
					 "\tGET DIAGNOSTICS n = ROW_COUNT;\n" +
					 "END;\n" +
					 "$$;",
					 output);
	}
}
