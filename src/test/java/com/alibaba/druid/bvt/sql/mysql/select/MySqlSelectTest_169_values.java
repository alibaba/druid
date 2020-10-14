package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_169_values extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM (VALUES (89), (35), (77)) EXCEPT SELECT * FROM (VALUES (33), (35), (60))";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" + "FROM (\n" + "\tVALUES (89), \n" + "\t(35), \n" + "\t(77)\n" + ")\n"
                     + "EXCEPT\n" + "SELECT *\n" + "FROM (\n" + "\tVALUES (33), \n" + "\t(35), \n" + "\t(60)\n"
                     + ")", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT * FROM (VALUES 89, 35, 77) EXCEPT SELECT * FROM (VALUES 33, 35, 60)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" + "FROM (\n" + "\tVALUES (89), \n" + "\t(35), \n" + "\t(77)\n" + ")\n" + "EXCEPT\n"
                     + "SELECT *\n" + "FROM (\n" + "\tVALUES (33), \n" + "\t(35), \n" + "\t(60)\n" + ")", stmt.toString());
    }

}