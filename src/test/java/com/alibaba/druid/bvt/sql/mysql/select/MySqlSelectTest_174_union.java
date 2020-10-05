package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLUnionDataType;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_174_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select id from a where a.id < 10 union select id from b where a.id < 10 limit 10";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT id\n" +
                "FROM a\n" +
                "WHERE a.id < 10\n" +
                "UNION\n" +
                "SELECT id\n" +
                "FROM b\n" +
                "WHERE a.id < 10\n" +
                "LIMIT 10", stmt.toString());

        SQLUnionQuery union = (SQLUnionQuery) stmt.getSelect().getQuery();

        SQLLimit limit = union.getLimit();
        assertNotNull(limit);
    }

}