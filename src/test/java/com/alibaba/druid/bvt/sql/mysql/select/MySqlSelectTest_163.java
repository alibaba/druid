package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_163 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "  val,\n" +
                "  ROW_NUMBER() OVER (ORDER BY val) AS 'row_number',\n" +
                "  RANK()       OVER (ORDER BY val) AS 'rank',\n" +
                "  DENSE_RANK() OVER (ORDER BY val) AS 'dense_rank'\n" +
                "FROM numbers;";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT val, ROW_NUMBER() OVER (ORDER BY val) AS \"row_number\", RANK() OVER (ORDER BY val) AS \"rank\"\n" +
                "\t, DENSE_RANK() OVER (ORDER BY val) AS \"dense_rank\"\n" +
                "FROM numbers;", stmt.toString());


    }

}