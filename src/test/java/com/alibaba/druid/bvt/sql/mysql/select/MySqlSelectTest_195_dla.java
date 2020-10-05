package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_195_dla extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT $1, $2, $3 FROM\n" +
                "TABLE temp_1\n" +
                "LOCATION 'oss://xxx/xxx/xxx.csv'";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT $1, $2, $3\n" +
                "FROM TABLE temp_1\n" +
                "LOCATION 'oss://xxx/xxx/xxx.csv'", stmt.toString());

        assertEquals("select $1, $2, $3\n" +
                "from table temp_1\n" +
                "location 'oss://xxx/xxx/xxx.csv'", stmt.toLowerCaseString());
    }
}