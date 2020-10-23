package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlSelectTest_plus_ques
 * @description
 * @Author zzy
 * @Date 2019-07-17 10:30
 */
public class MySqlSelectTest_plus_ques extends MysqlTest {

    public void test_0() {
        String sql = "select +?;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("SELECT +?;", stmt.toString());

    }

}
