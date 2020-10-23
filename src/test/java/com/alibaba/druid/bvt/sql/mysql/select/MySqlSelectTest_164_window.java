package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_164_window extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "  val,\n" +
                "  ROW_NUMBER() OVER w AS 'row_number',\n" +
                "  RANK()       OVER w AS 'rank',\n" +
                "  DENSE_RANK() OVER w AS 'dense_rank'\n" +
                "FROM numbers\n" +
                "WINDOW w AS (ORDER BY val);";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT val, ROW_NUMBER() OVER w AS \"row_number\", RANK() OVER w AS \"rank\", DENSE_RANK() OVER w AS \"dense_rank\"\n" +
                "FROM numbers\n" +
                "WINDOW w AS (ORDER BY val);", stmt.toString());


    }

}