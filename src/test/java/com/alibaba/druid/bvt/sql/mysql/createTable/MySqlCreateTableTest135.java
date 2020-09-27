package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

/**
 * @author Dagon0577
 * @date 2020/5/14 10:51
 */
public class MySqlCreateTableTest135 extends MysqlTest {
    public void test_0() throws Exception {
        String sql =
                "create table MQ_TOPIC_RECORD(\n"
                        + "   TOPIC_ID             bigint(11) not null,\n"
                        + "   BROKENNAME           national VARCHAR(50),\n"
                        + "   BROKENNAME2           national CHAR(50)\n" + ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE MQ_TOPIC_RECORD (\n" +
                "\tTOPIC_ID bigint(11) NOT NULL,\n" +
                "\tBROKENNAME national VARCHAR(50),\n" +
                "\tBROKENNAME2 national CHAR(50)\n" +
                ");", stmt.toString());

        assertEquals("create table MQ_TOPIC_RECORD (\n" +
                "\tTOPIC_ID bigint(11) not null,\n" +
                "\tBROKENNAME national VARCHAR(50),\n" +
                "\tBROKENNAME2 national CHAR(50)\n" +
                ");", stmt.toLowerCaseString());

    }
}
