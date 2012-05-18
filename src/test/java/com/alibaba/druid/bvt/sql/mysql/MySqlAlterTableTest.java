package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.Assert;
import junit.framework.TestCase;


public class MySqlAlterTableTest extends TestCase {
    public void test_alter_0() throws Exception {
        String sql = "ALTER TABLE `test`.`tb1` CHANGE COLUMN `fname` `fname1` VARCHAR(45) NULL DEFAULT NULL  ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE `test`.`tb1`\n\tCHANGE COLUMN `fname` `fname1` VARCHAR(45) NULL", output);
    }
    
    public void test_alter_1() throws Exception {
        String sql = "ALTER TABLE `test`.`tb1` CHARACTER SET = utf8 , COLLATE = utf8_general_ci ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE `test`.`tb1`\n\tCHARACTER SET = utf8, COLLATE = utf8_general_ci", output);
    }
    
}
