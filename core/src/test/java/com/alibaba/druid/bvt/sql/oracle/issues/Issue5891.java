package com.alibaba.druid.bvt.sql.oracle.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5891>Issue来源</a>
 * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/Comparison-Conditions.html">Comparison Conditions</a>
 */
public class Issue5891 {

    @Test
    public void test_parse_any() {
        for (DbType dbType : new DbType[]{DbType.oracle}) {
            for (String sql : new String[]{
                //"SELECT * FROM tbl_name WHERE col > ANY (1, 5, 10);",
                "SELECT * FROM tbl_name WHERE col > SOME (1, 5, 10);",
                "SELECT * FROM tbl_name WHERE col = ANY ('1', '5', '10');",
                "SELECT * FROM tbl_name WHERE col = SOME ('1', '5', '10');",

                "SELECT * FROM employees\n"
                    + "  WHERE salary = ANY\n"
                    + "  (SELECT salary \n"
                    + "   FROM employees\n"
                    + "  WHERE department_id = 30)\n"
                    + "  ORDER BY employee_id;",
                "SELECT * FROM employees\n"
                    + "  WHERE salary >= ALL\n"
                    + "  (SELECT salary \n"
                    + "   FROM employees\n"
                    + "  WHERE department_id = 30)\n"
                    + "  ORDER BY employee_id;",
                "SELECT * FROM employees\n"
                    + "  WHERE salary >=\n"
                    + "  ALL (1400, 3000)\n"
                    + "  ORDER BY employee_id;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
