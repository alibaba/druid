package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

/**
 *
 * @author lijun.cailj 2018/1/3
 */
public class MysqlSelectTest_like extends MysqlTest {

    public void test_2() throws Exception {
        String sql = "select * from table1 where name like \"%lijun%\"";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        String s = SQLUtils.toMySqlString(statementList.get(0));
        assertEquals("SELECT *\n" + "FROM table1\n" + "WHERE name LIKE '%lijun%'", s);
    }
}
