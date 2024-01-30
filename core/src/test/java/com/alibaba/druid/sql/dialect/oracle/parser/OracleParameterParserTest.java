package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OracleParameterParserTest {
	@Test
	public void testTableOfParameter() {
		String sql = "DECLARE\n" +
			"	TYPE Foursome IS TABLE OF VARCHAR2(15);\n" +
			"	team Foursome := Foursome('John', 'Mary', 'Alberto', 'Juanita');\n" +
			"BEGIN\n" +
			"	DBMS_OUTPUT.PUT_LINE('2001 Team:');\n" +
			"	FOR i IN 1..4\n" +
			"	LOOP\n" +
			"		DBMS_OUTPUT.PUT_LINE(i || '.' || team(i));\n" +
			"	END LOOP;\n" +
			"END;\n";
		String expectedSql = "[DECLARE\n" +
			"	TYPE Foursome IS TABLE OF VARCHAR2(15);\n" +
			"	team Foursome := Foursome('John', 'Mary', 'Alberto', 'Juanita');\n" +
			"BEGIN\n" +
			"	DBMS_OUTPUT.PUT_LINE('2001 Team:');\n" +
			"	FOR i IN 1..4\n" +
			"	LOOP\n" +
			"		DBMS_OUTPUT.PUT_LINE(i || '.' || team(i));\n" +
			"	END LOOP;\n" +
			"END;]";
		List<SQLStatement> stat = SQLUtils.parseStatements(sql, DbType.oracle);
		System.out.println(stat);
		Assert.assertEquals(expectedSql, stat.toString());
		System.out.println("=============");
	}
}
