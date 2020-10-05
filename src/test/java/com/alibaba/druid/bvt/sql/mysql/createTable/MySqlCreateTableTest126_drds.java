package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest126_drds extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE xx\n" +
                "DBPARTITION BY hash(name1) TBPARTITION BY hash(name2) TBPARTITIONS 4\n" +
                "EXTPARTITION (\n" +
                "    DBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'),\n" +
                "    DBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'),\n" +
                "    DBPARTITION yyy BY KEY('gpk')\n" +
                ")";
//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE xx\n" +
                "DBPARTITION BY hash(name1)\n" +
                "TBPARTITION BY hash(name2) TBPARTITIONS 4\n" +
                "EXTPARTITION (\n" +
                "\tDBPARTITION xxx BY KEY('abc') TBPARTITION yyy BY KEY('abc'), \n" +
                "\tDBPARTITION yyy BY KEY('def') TBPARTITION yyy BY KEY('def'), \n" +
                "\tDBPARTITION yyy BY KEY('gpk')\n" +
                ")", stmt.toString());

    }



}