package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_175_hints extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT/!TDDL:t1.id=1 and t2.id=1*/ * FROM t1 INNER JOIN SELECT val FROM t2 WHERE id=1";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, SQLParserFeature.TDDLHint);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT /*TDDL:t1.id=1 and t2.id=1*/ *\n" +
                "FROM t1\n" +
                "\tINNER JOIN (\n" +
                "\t\tSELECT val\n" +
                "\t\tFROM t2\n" +
                "\t\tWHERE id = 1\n" +
                "\t)", stmt.toString());

        assertEquals("select /*TDDL:t1.id=1 and t2.id=1*/ *\n" +
                "from t1\n" +
                "\tinner join (\n" +
                "\t\tselect val\n" +
                "\t\tfrom t2\n" +
                "\t\twhere id = 1\n" +
                "\t)", stmt.toLowerCaseString());


        assertEquals("SELECT /*TDDL:t1.id=1 and t2.id=1*/ *\n" +
                "FROM t1\n" +
                "\tINNER JOIN (\n" +
                "\t\tSELECT val\n" +
                "\t\tFROM t2\n" +
                "\t\tWHERE id = ?\n" +
                "\t)", stmt.toParameterizedString());
    }

}