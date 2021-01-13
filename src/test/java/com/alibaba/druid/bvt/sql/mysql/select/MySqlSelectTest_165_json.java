package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_165_json extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select concat(l_shipdate,'10') from lineitem join orders on l_orderkey = o_orderkey where l_shipdate between '1997-01-27' and '1997-02-20' and json_extract(l_comment,'$.id') = json '1997-01-2810' limit 3";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT concat(l_shipdate, '10')\n" +
                "FROM lineitem\n" +
                "\tJOIN orders ON l_orderkey = o_orderkey\n" +
                "WHERE l_shipdate BETWEEN '1997-01-27' AND '1997-02-20'\n" +
                "\tAND json_extract(l_comment, '$.id') = JSON '1997-01-2810'\n" +
                "LIMIT 3", stmt.toString());


    }

}