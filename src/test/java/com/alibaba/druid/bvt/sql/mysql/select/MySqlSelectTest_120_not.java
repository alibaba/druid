package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_120_not extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "sELECT pk FROM corona_broadcast WHERE !(decimal_test BETWEEN double_test AND 62); ";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT pk\n" +
                "FROM corona_broadcast\n" +
                "WHERE !(decimal_test BETWEEN double_test AND 62);", stmt.toString());
    }
}