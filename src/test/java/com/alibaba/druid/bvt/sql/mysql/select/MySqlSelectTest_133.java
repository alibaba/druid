package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_133 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select (~(43)     ),(     (tinyint_1bit_test MOD integer_test MOD  bigint_test) not in (1,2,'a',(binary  'a'='a '))  )from select_base_two_one_db_multi_tb ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT ~43, (tinyint_1bit_test % integer_test % bigint_test) NOT IN (1, 2, 'a', BINARY 'a' = 'a ')\n" +
                "FROM select_base_two_one_db_multi_tb", stmt.toString());

        assertEquals("SELECT ~?, (tinyint_1bit_test % integer_test % bigint_test) NOT IN (?, ?, ?, BINARY ? = ?)\n" +
                "FROM select_base_two_one_db_multi_tb", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }


}