package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest132_drds_primaryKey extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table gxw_test_87 (id int, name varchar(20),  primary key(id, name)) dbpartition by hash(id);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE gxw_test_87 (\n" +
                "\tid int,\n" +
                "\tname varchar(20),\n" +
                "\tPRIMARY KEY (id, name)\n" +
                ")\n" +
                "DBPARTITION BY hash(id);", stmt.toString());



    }




}