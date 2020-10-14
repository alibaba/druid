package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_135 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select (~(oct(mediumint_test  ))     ),(    ((  'b')AND (date_test  ))  in(smallint_test, bigint_test,tinyint_1bit_test,( WEIGHT_STRING( 0x007fff LEVEL 1 DESC  ))) )from select_base_two_multi_db_multi_tb";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT ~oct(mediumint_test), ('b'\n" +
                "\tAND date_test) IN (smallint_test, bigint_test, tinyint_1bit_test, WEIGHT_STRING(0x007fff LEVEL 1 DESC))\n" +
                "FROM select_base_two_multi_db_multi_tb", stmt.toString());

        assertEquals("SELECT ~oct(mediumint_test), (?\n" +
                "\tAND date_test) IN (smallint_test, bigint_test, tinyint_1bit_test, WEIGHT_STRING(? LEVEL 1 DESC))\n" +
                "FROM select_base_two_multi_db_multi_tb", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }


}