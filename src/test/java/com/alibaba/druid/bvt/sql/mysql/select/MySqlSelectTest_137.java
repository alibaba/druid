package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_137 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select  ((decimal_test =87 /  bigint_test =bigint_test) >(second(timestamp_test  )) )from select_base_two_multi_db_one_tb";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT decimal_test = 87 / bigint_test = bigint_test > second(timestamp_test)\n" +
                "FROM select_base_two_multi_db_one_tb", stmt.toString());

        assertEquals("SELECT decimal_test = ? / bigint_test = bigint_test > second(timestamp_test)\n" +
                "FROM select_base_two_multi_db_one_tb", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }


}