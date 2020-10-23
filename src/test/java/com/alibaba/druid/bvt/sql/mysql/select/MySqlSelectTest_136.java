package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_136 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT `tinyint_test` / `tinyint_1bit_test` = `mediumint_test` = `decimal_test` / `double_test`\n" +
                "FROM `corona_one_db_one_tb`";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT `tinyint_test` / `tinyint_1bit_test` = `mediumint_test` = `decimal_test` / `double_test`\n" +
                "FROM `corona_one_db_one_tb`", stmt.toString());

        assertEquals("SELECT `tinyint_test` / `tinyint_1bit_test` = `mediumint_test` = `decimal_test` / `double_test`\n" +
                "FROM `corona_one_db_one_tb`", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }


}