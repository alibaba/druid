/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2011-7-18)
 */
package com.alibaba.druid.bvt.sql.cobar;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * @author <a href="mailto:danping.yudp@alibaba-inc.com">YU Danping</a>
 */
public class DDLParserTest extends TestCase {

    public void testTruncate() throws Exception {
        String sql = "Truncate table tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);
    }

    public void testTruncate_1() throws Exception {
        String sql = "Truncate tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("TRUNCATE TABLE tb1", output);
    }

    public void testAlterTable_0() throws Exception {
        String sql = "alTer ignore table tb_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER IGNORE TABLE tb_name", output);
    }

    public void testAlterTable_1() throws Exception {
        String sql = "alTer table tb_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE tb_name", output);
    }

    public void testAlterTable_2() throws Exception {
        String sql = "ALTER TABLE `test`.`tb1` ADD COLUMN `name` VARCHAR(45) NULL  AFTER `fid` ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE `test`.`tb1`" + //
                            "\n\tADD COLUMN `name` VARCHAR(45) NULL AFTER `fid`", output);
    }
    
    public void testAlterTable_3() throws Exception {
        String sql = "ALTER TABLE `test`.`tb1` DROP COLUMN `name` ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE `test`.`tb1`" + //
                "\n\tDROP COLUMN `name`", output);
    }
    
    public void test_createTable_0() throws Exception {
        String sql = "crEate temporary tabLe if not exists tb_name (fid int)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TEMPORARY TABLE IF NOT EXISTS tb_name (\n\tfid int\n)", output);
    }
    
    public void test_createTable_1() throws Exception {
        String sql = "crEate tabLe if not exists tb_name (fid int)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE IF NOT EXISTS tb_name (\n\tfid int\n)", output);
    }
    
    public void test_createIndex_0() throws Exception {
        String sql = "create index `idx1` ON tb1 (`fname` ASC) ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE INDEX `idx1` ON tb1 (`fname` ASC)", output);
    }
    
    public void test_createIndex_1() throws Exception {
        String sql = "create unique index `idx1` ON tb1 (`fname` desc) ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE UNIQUE INDEX `idx1` ON tb1 (`fname` DESC)", output);
    }
    
    public void test_createIndex_2() throws Exception {
        String sql = "CREATE INDEX id_index ON lookup (id) USING BTREE;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE INDEX id_index ON lookup (id) USING BTREE", output);
    }
    
    public void test_createIndex_3() throws Exception {
        String sql = "crEate index index_name using hash on tb(col(id))";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE INDEX index_name ON tb (col(id)) USING HASH", output);
    }
    
    public void test_createIndex_4() throws Exception {
        String sql = "crEate spatial index index_name on tb(col(id))";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE SPATIAL INDEX index_name ON tb (col(id))", output);
    }
    
    public void test_drop_index_0() throws Exception {
        String sql = "drop index index_name on tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP INDEX index_name ON tb1", output);
    }
    
    public void test_drop_index_1() throws Exception {
        String sql = "DROP INDEX `PRIMARY` ON t;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP INDEX `PRIMARY` ON t", output);
    }
    
    public void test_drop_table_0() throws Exception {
        String sql = "DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 RESTRICT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 RESTRICT", output);
    }
    
    public void test_drop_table_1() throws Exception {
        String sql = "DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 CASCADE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP TEMPORARY TABLE IF EXISTS tb1, tb2, tb3 CASCADE", output);
    }
    
    public void test_drop_table_2() throws Exception {
        String sql = "DROP TABLE t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP TABLE t1", output);
    }
    
    public void test_rename_0() throws Exception {
        String sql = "RENAME TABLE current_db.tbl_name TO other_db.tbl_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("RENAME TABLE current_db.tbl_name TO other_db.tbl_name", output);
    }
    
    public void test_rename_1() throws Exception {
        String sql = "rename table tb1 to ntb1,tb2 to ntb2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("RENAME TABLE tb1 TO ntb1, tb2 TO ntb2", output);
    }

    public void test_drop_view_1() throws Exception {
        String sql = "DROP VIEW IF EXISTS tb1, tb2, tb3 CASCADE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP VIEW IF EXISTS tb1, tb2, tb3 CASCADE", output);
    }
    
    public void test_drop_view_2() throws Exception {
        String sql = "DROP VIEW t1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP VIEW t1", output);
    }
    // public void testDDLStmt() throws Exception {
    //
    // sql = "rename table tb1 to ntb1,tb2 to ntb2";
    // lexer = new SQLLexer(sql);
    // parser = new DDLParser(lexer, new SQLExprParser(lexer));
    // dst = parser.ddlStmt();
    // output = output2MySQL(dst, sql);
    // Assert.assertEquals("RENAME TABLE tb1 TO ntb1, tb2 TO ntb2", output);
    //
    // sql = "rename table tb1 to ntb1";
    // lexer = new SQLLexer(sql);
    // parser = new DDLParser(lexer, new SQLExprParser(lexer));
    // dst = parser.ddlStmt();
    // output = output2MySQL(dst, sql);
    // Assert.assertEquals("RENAME TABLE tb1 TO ntb1", output);
    // }
}
