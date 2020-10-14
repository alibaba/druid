package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest129_ads extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE cache_table_1 OPTIONS(cache=true) AS /*+ engine=MPP */ SELECT * FROM test_realtime1 LIMIT 200;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE cache_table_1\n" +
                "OPTIONS (cache = true)\n" +
                "AS\n" +
                "/*+ engine=MPP */\n" +
                "SELECT *\n" +
                "FROM test_realtime1\n" +
                "LIMIT 200;", stmt.toString());

    }


    public void test_1() throws Exception {
        String sql = "CREATE TABLE employees (\n" +
                "    id INT NOT NULL,\n" +
                "    fname VARCHAR(30),\n" +
                "    lname VARCHAR(30),\n" +
                "    hired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "    separated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "    job_code INT,\n" +
                "    store_id INT\n" +
                ")\n" +
                "PARTITION BY HASH KEY (id)\n" +
                "tablegroup group0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE employees (\n" +
                "\tid INT NOT NULL,\n" +
                "\tfname VARCHAR(30),\n" +
                "\tlname VARCHAR(30),\n" +
                "\thired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "\tseparated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "\tjob_code INT,\n" +
                "\tstore_id INT\n" +
                ")\n" +
                "PARTITION BY HASH KEY(id)\n" +
                "TABLEGROUP group0;", stmt.toString());

    }

    public void test_2() throws Exception {
        String sql = "CREATE TABLE employees (\n" +
                "    id INT NOT NULL,\n" +
                "    fname VARCHAR(30),\n" +
                "    lname VARCHAR(30),\n" +
                "    hired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "    separated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "    job_code INT,\n" +
                "    store_id INT,\n" +
                "    primary key(id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY (id)\n" +
                "SUBPARTITION BY RANGE (YEAR(separated)) \n" +
                "(\n" +
                "    PARTITION p0 VALUES LESS THAN (1991),\n" +
                "    PARTITION p1 VALUES LESS THAN (1996),\n" +
                "    PARTITION p2 VALUES LESS THAN (2001),\n" +
                "    PARTITION p3 VALUES LESS THAN MAXVALUE\n" +
                ")\n" +
                "tablegroup group0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE employees (\n" +
                "\tid INT NOT NULL,\n" +
                "\tfname VARCHAR(30),\n" +
                "\tlname VARCHAR(30),\n" +
                "\thired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "\tseparated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "\tjob_code INT,\n" +
                "\tstore_id INT,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY(id)\n" +
                "SUBPARTITION BY RANGE  (\n" +
                "\tPARTITION p0 VALUES LESS THAN (1991), \n" +
                "\tPARTITION p1 VALUES LESS THAN (1996), \n" +
                "\tPARTITION p2 VALUES LESS THAN (2001), \n" +
                "\tPARTITION p3 VALUES LESS THAN MAXVALUE\n" +
                ")\n" +
                "TABLEGROUP group0;", stmt.toString());

    }


    public void test_3() throws Exception {
        String sql = "CREATE TABLE employees (\n" +
                "    id INT NOT NULL,\n" +
                "    fname VARCHAR(30),\n" +
                "    lname VARCHAR(30),\n" +
                "    hired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "    separated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "    job_code INT,\n" +
                "    store_id INT,\n" +
                "    primary key(id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY (id)\n" +
                "SUBPARTITION BY LIST(store_id) (\n" +
                "    PARTITION pNorth VALUES IN (3,5,6,9,17),\n" +
                "    PARTITION pEast VALUES IN (1,2,10,11,19,20),\n" +
                "    PARTITION pWest VALUES IN (4,12,13,14,18),\n" +
                "    PARTITION pCentral VALUES IN (7,8,15,16)\n" +
                ")\n" +
                "tablegroup group0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE employees (\n" +
                "\tid INT NOT NULL,\n" +
                "\tfname VARCHAR(30),\n" +
                "\tlname VARCHAR(30),\n" +
                "\thired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "\tseparated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "\tjob_code INT,\n" +
                "\tstore_id INT,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY(id)\n" +
                "SUBPARTITION BY LIST (store_id)  (\n" +
                "\tPARTITION pNorth VALUES IN (3, 5, 6, 9, 17), \n" +
                "\tPARTITION pEast VALUES IN (1, 2, 10, 11, 19, 20), \n" +
                "\tPARTITION pWest VALUES IN (4, 12, 13, 14, 18), \n" +
                "\tPARTITION pCentral VALUES IN (7, 8, 15, 16)\n" +
                ")\n" +
                "TABLEGROUP group0;", stmt.toString());

    }

    public void test_4() throws Exception {
        String sql = "CREATE TABLE employees (\n" +
                "    id INT NOT NULL,\n" +
                "    fname VARCHAR(30),\n" +
                "    lname VARCHAR(30),\n" +
                "    hired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "    separated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "    job_code INT,\n" +
                "    store_id INT,\n" +
                "    close INT,\n" +
                "    primary key(id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY (id)\n" +
                "SUBPARTITION BY LIST (store_id) (\n" +
                "    SUBPARTITION pNorth VALUES IN (3,5,6,9,17),\n" +
                "    SUBPARTITION pEast VALUES IN (1,2,10,11,19,20),\n" +
                "    SUBPARTITION pWest VALUES IN (4,12,13,14,18),\n" +
                "    SUBPARTITION pCentral VALUES IN (7,8,15,16)\n" +
                ")\n" +
                "tablegroup group0;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE employees (\n" +
                "\tid INT NOT NULL,\n" +
                "\tfname VARCHAR(30),\n" +
                "\tlname VARCHAR(30),\n" +
                "\thired DATE NOT NULL DEFAULT '1970-01-01',\n" +
                "\tseparated DATE NOT NULL DEFAULT '9999-12-31',\n" +
                "\tjob_code INT,\n" +
                "\tstore_id INT,\n" +
                "\tclose INT,\n" +
                "\tPRIMARY KEY (id)\n" +
                ")\n" +
                "PARTITION BY HASH KEY(id)\n" +
                "SUBPARTITION BY LIST (store_id)  (\n" +
                "\tPARTITION pNorth VALUES IN (3, 5, 6, 9, 17), \n" +
                "\tPARTITION pEast VALUES IN (1, 2, 10, 11, 19, 20), \n" +
                "\tPARTITION pWest VALUES IN (4, 12, 13, 14, 18), \n" +
                "\tPARTITION pCentral VALUES IN (7, 8, 15, 16)\n" +
                ")\n" +
                "TABLEGROUP group0;", stmt.toString());

    }

    public void test_create_table_as() {
        String sql = "CREATE TABLE triangle (\n" +
                "  sidea DOUBLE,\n" +
                "  sideb DOUBLE,\n" +
                "  sidec DOUBLE AS (SQRT(sidea * sidea + sideb * sideb))\n" +
                ");";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("CREATE TABLE triangle (\n" +
                "\tsidea DOUBLE,\n" +
                "\tsideb DOUBLE,\n" +
                "\tsidec DOUBLE AS (SQRT(sidea * sidea + sideb * sideb))\n" +
                ");", stmt.toString());
    }


    public void test_create_table_new() {
        String sql = "select label from t";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        System.out.println(statement.toString());
    }



}