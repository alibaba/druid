package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlSelectTest_eq_null_parameterized
 * @description
 * @Author zzy
 * @Date 2019-07-16 17:28
 */
public class MySqlSelectTest_eq_null_parameterized extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from test_null_shard where id = null;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM test_null_shard\n" +
                "WHERE id = NULL;", stmt.toString());

        assertEquals("SELECT *\n" +
                "FROM test_null_shard\n" +
                "WHERE id = ?;", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

}
