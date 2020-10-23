package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_132_not_in extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select abc.* from abc where id not in (1,2,3)";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT abc.*\n" +
                "FROM abc\n" +
                "WHERE id NOT IN (1, 2, 3)", stmt.toString());

        assertEquals("SELECT abc.*\n" +
                "FROM abc\n" +
                "WHERE id NOT IN (?)", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }


}