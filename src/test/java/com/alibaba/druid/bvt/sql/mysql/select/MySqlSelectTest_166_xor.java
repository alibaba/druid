package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_166_xor extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select 1 from corona_select_multi_db_multi_tb where (1 XOR 2 ) between 3 and 4";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT 1\n" +
                "FROM corona_select_multi_db_multi_tb\n" +
                "WHERE (1 XOR 2) BETWEEN 3 AND 4", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select * from t where id != 4 or id = 4 xor (id = 4 or id < 4)";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id != 4\n" +
                "\tOR id = 4 XOR (id = 4\n" +
                "\t\tOR id < 4)", stmt.toString());
    }
    //

}