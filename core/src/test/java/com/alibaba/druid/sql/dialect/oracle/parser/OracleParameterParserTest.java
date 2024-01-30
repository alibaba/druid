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
		String sql = """
					DECLARE
						TYPE Foursome IS TABLE OF VARCHAR2(15);
						team Foursome := Foursome('John', 'Mary', 'Alberto', 'Juanita');
					BEGIN
						DBMS_OUTPUT.PUT_LINE('2001 Team:');
						FOR i IN 1..4
						LOOP
							DBMS_OUTPUT.PUT_LINE(i || '.' || team(i));
					  	END LOOP;
					END;
				""";
		String expectedSql = """
					[DECLARE
						TYPE Foursome IS TABLE OF VARCHAR2(15);
						team Foursome := Foursome('John', 'Mary', 'Alberto', 'Juanita');
					BEGIN
						DBMS_OUTPUT.PUT_LINE('2001 Team:');
						FOR i IN 1..4
						LOOP
							DBMS_OUTPUT.PUT_LINE(i || '.' || team(i));
						END LOOP;
					END;]\
					""";
		List<SQLStatement> stat = SQLUtils.parseStatements(sql, DbType.oracle);
		System.out.println(stat.toString());
		Assert.assertEquals(expectedSql, stat.toString());
		System.out.println("=============");
	}
}
