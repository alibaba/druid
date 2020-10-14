package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest141 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE sbtest1 (\n" +
                "id INTEGER UNSIGNED NOT NULL ,\n" +
                "k INTEGER UNSIGNED DEFAULT '0' NOT NULL,\n" +
                "c CHAR(120) DEFAULT '' NOT NULL,\n" +
                "pad CHAR(60) DEFAULT '' NOT NULL,\n" +
                "KEY xid (id)\n" +
                ") /*! ENGINE = innodb MAX_ROWS = 1000000 */  dbpartition by hash(id) tbpartition by hash(id) tbpartitions 2";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE sbtest1 (\n" +
                "\tid INTEGER UNSIGNED NOT NULL,\n" +
                "\tk INTEGER UNSIGNED NOT NULL DEFAULT '0',\n" +
                "\tc CHAR(120) NOT NULL DEFAULT '',\n" +
                "\tpad CHAR(60) NOT NULL DEFAULT '',\n" +
                "\tKEY xid (id)\n" +
                ")\n" +
                "DBPARTITION BY hash(id)\n" +
                "TBPARTITION BY hash(id) TBPARTITIONS 2", stmt.toString());

        assertEquals("create table sbtest1 (\n" +
                "\tid INTEGER unsigned not null,\n" +
                "\tk INTEGER unsigned not null default '0',\n" +
                "\tc CHAR(120) not null default '',\n" +
                "\tpad CHAR(60) not null default '',\n" +
                "\tkey xid (id)\n" +
                ")\n" +
                "dbpartition by hash(id)\n" +
                "tbpartition by hash(id) tbpartitions 2", stmt.toLowerCaseString());

    }





}