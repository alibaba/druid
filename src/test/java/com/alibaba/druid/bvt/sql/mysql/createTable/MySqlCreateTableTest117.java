package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest117 extends MysqlTest {

    public void test() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (id int(10) auto_increment" +
                " ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10) AUTO_INCREMENT\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (id int(10) auto_increment by GROUP" +
                " ) auto_increment=12313 ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10) AUTO_INCREMENT BY GROUP\n" +
                ") AUTO_INCREMENT = 12313 ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (id int(10) auto_increment by simple" +
                " ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10) AUTO_INCREMENT BY SIMPLE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

    public void test_2() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (id int(10) auto_increment by simple with cache" +
                " ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10) AUTO_INCREMENT BY SIMPLE WITH CACHE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

    public void test_3() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (id int(10) auto_increment by time" +
                " ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10) AUTO_INCREMENT BY TIME\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

}