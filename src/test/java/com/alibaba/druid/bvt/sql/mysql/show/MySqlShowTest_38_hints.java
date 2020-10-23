package com.alibaba.druid.bvt.sql.mysql.show;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

/**
 * @author chenmo.cm
 * @date 2018/8/24 上午10:53
 */
public class MySqlShowTest_38_hints extends MysqlTest {

    private final static SQLParserFeature[] defaultFeatures = { SQLParserFeature.EnableSQLBinaryOpExprGroup,
        SQLParserFeature.UseInsertColumnsCache, SQLParserFeature.OptimizedForParameterized,
        SQLParserFeature.TDDLHint,                     };

    public void test_0() throws Exception {
        String sql = "/* +TDDL:scan()*/show table status from corona_qatest_0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.TDDLHint);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+TDDL:scan()*/\n" + "SHOW TABLE STATUS FROM corona_qatest_0;", stmt.toString());
        assertEquals("/*+TDDL:scan()*/\n" + "show table status from corona_qatest_0;", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "/* +TDDL:node(1)*/show table status from corona_qatest_0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql, defaultFeatures);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+TDDL:node(1)*/\n" + "SHOW TABLE STATUS FROM corona_qatest_0;", stmt.toString());
        assertEquals("/*+TDDL:node(1)*/\n" + "show table status from corona_qatest_0;", stmt.toLowerCaseString());
    }
}
