package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlSelectTest_171_multi_error extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select 1 select 2";

        Exception error = null;
        try {
            SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        } catch (ParserException e) {
            error = e;
        }
        assertNotNull(error);
    }
}