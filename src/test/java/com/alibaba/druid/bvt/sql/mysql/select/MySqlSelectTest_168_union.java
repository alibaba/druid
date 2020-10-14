package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_168_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from tb2 union all(select * from tb2 order by id1);";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT *\n" +
                "FROM tb2\n" +
                "UNION ALL\n" +
                "(SELECT *\n" +
                "FROM tb2\n" +
                "ORDER BY id1);", stmt.toString());
    }

}