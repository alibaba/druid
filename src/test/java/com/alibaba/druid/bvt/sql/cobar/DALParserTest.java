/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
 * (created at 2011-5-20)
 */
package com.alibaba.druid.bvt.sql.cobar;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;


public class DALParserTest extends TestCase {

    public void testdesc() throws Exception {
        String sql = "desc tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement desc = parser.parseDescribe();
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(desc);
        Assert.assertEquals("DESC tb1", output);
    }

    public void testdesc_1() throws Exception {
        String sql = "desc db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement desc = parser.parseDescribe();
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(desc);
        Assert.assertEquals("DESC db.tb1", output);
    }

    public void testdesc_2() throws Exception {
        String sql = "describe db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement desc = parser.parseDescribe();
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(desc);
        Assert.assertEquals("DESC db.tb1", output);
    }

    public void testSet_1() throws Exception {
        String sql = "seT sysVar1 = ? ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSetStatement set = (SQLSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET sysVar1 = ?", output);
    }

    public void testSet_2() throws Exception {
        String sql = "SET `sysVar1` = ?, @@gloBal . `var2` :=1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSetStatement set = (SQLSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET `sysVar1` = ?, @@global.`var2` = 1", output);
    }

    public void testSet_3() throws Exception {
        String sql = "SET @usrVar1 := ?, @@`var2` =1, @@var3:=?, @'var\\'3'=?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSetStatement set = (SQLSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET @usrVar1 = ?, @@`var2` = 1, @@var3 = ?, @'var\\'3' = ?", output);
    }

    public void testSet_4() throws Exception {
        String sql = "SET GLOBAL var1=1, SESSION var2:=2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSetStatement set = (SQLSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET @@global.var1 = 1, @@session.var2 = 2", output);
    }

    public void testSet_5() throws Exception {
        String sql = "SET @@GLOBAL. var1=1, SESSION var2:=2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSetStatement set = (SQLSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET @@global.var1 = 1, @@session.var2 = 2", output);
    }

    public void testSetTxn_0() throws Exception {
        String sql = "SET transaction ISOLATION LEVEL READ UNCOMMITTED";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlSetTransactionStatement set = (MySqlSetTransactionStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED", output);
    }

    public void testSetTxn_1() throws Exception {
        String sql = "SET global transaction ISOLATION LEVEL READ COMMITTED";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlSetTransactionStatement set = (MySqlSetTransactionStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED", output);
    }

    public void testSetTxn_2() throws Exception {
        String sql = "SET transaction ISOLATION LEVEL REPEATABLE READ ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlSetTransactionStatement set = (MySqlSetTransactionStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET TRANSACTION ISOLATION LEVEL REPEATABLE READ", output);
    }

    public void testSetTxn_3() throws Exception {
        String sql = "SET session transaction ISOLATION LEVEL SERIALIZABLE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlSetTransactionStatement set = (MySqlSetTransactionStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE", output);
    }

    public void test_setNames() throws Exception {
        String sql = "SET names default ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement set = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET NAMES DEFAULT", output);
    }

    public void test_setNames_1() throws Exception {
        String sql = "SET NAMEs 'utf8' collatE \"latin1_danish_ci\" ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement set = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET NAMES 'utf8' COLLATE \"latin1_danish_ci\"", output);
    }

    public void test_setNames_2() throws Exception {
        String sql = "SET NAMEs utf8 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement set = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET NAMES utf8", output);
    }

    public void test_setCharSet() throws Exception {
        String sql = "SET CHARACTEr SEt 'utf8'  ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement set = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET CHARACTER SET 'utf8'", output);
    }

    public void test_setCharSet_1() throws Exception {
        String sql = "SET CHARACTEr SEt DEFaULT  ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement set = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(set);
        Assert.assertEquals("SET CHARACTER SET DEFAULT", output);
    }

    public void test_show_authors() throws Exception {
        String sql = "shoW authors ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowAuthorsStatement show = (MySqlShowAuthorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW AUTHORS", output);
    }

    public void test_show_binaryLogs() throws Exception {
        String sql = "SHOW BINARY LOGS ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowBinaryLogsStatement show = (MySqlShowBinaryLogsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW BINARY LOGS", output);
    }

    public void test_show_masterLogs() throws Exception {
        String sql = "SHOW MASTER LOGS ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowMasterLogsStatement show = (MySqlShowMasterLogsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW MASTER LOGS", output);
    }

    public void test_show_collation() throws Exception {
        String sql = "SHOW collation";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCollationStatement show = (MySqlShowCollationStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLLATION", output);
    }

    public void test_show_collation_1() throws Exception {
        String sql = "SHOW Collation like 'var1'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCollationStatement show = (MySqlShowCollationStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLLATION LIKE 'var1'", output);
    }

    public void test_show_collation_2() throws Exception {
        String sql = "SHOW COLLATION WHERE `Default` = 'Yes'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCollationStatement show = (MySqlShowCollationStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLLATION WHERE `Default` = 'Yes'", output);
    }

    public void test_show_collation_3() throws Exception {
        String sql = "SHOW collation where Collation like 'big5%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCollationStatement show = (MySqlShowCollationStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLLATION WHERE Collation LIKE 'big5%'", output);
    }

    public void test_binaryLog() throws Exception {
        String sql = "SHOW binlog events in 'a' from 1 limit 1,2  ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowBinLogEventsStatement show = (MySqlShowBinLogEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW BINLOG EVENTS IN 'a' FROM 1 LIMIT 1, 2", output);
    }

    public void test_binaryLog_1() throws Exception {
        String sql = "SHOW binlog events from 1 limit 1,2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowBinLogEventsStatement show = (MySqlShowBinLogEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW BINLOG EVENTS FROM 1 LIMIT 1, 2", output);
    }

    public void test_binaryLog_2() throws Exception {
        String sql = "SHOW binlog events ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowBinLogEventsStatement show = (MySqlShowBinLogEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW BINLOG EVENTS", output);
    }

    public void test_show_character_set() throws Exception {
        String sql = "SHOW CHARACTER SET like 'var' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCharacterSetStatement show = (MySqlShowCharacterSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CHARACTER SET LIKE 'var'", output);
    }

    public void test_show_character_set2() throws Exception {
        String sql = "SHOW CHARSET";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCharacterSetStatement show = (MySqlShowCharacterSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CHARACTER SET", output);
    }

    public void test_show_character_set3() throws Exception {
        String sql = "SHOW CHARSET like 'utf8'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCharacterSetStatement show = (MySqlShowCharacterSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CHARACTER SET LIKE 'utf8'", output);
    }


    public void test_show_character_set_1() throws Exception {
        String sql = "SHOW CHARACTER SET where Charset = 'big5'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCharacterSetStatement show = (MySqlShowCharacterSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CHARACTER SET WHERE Charset = 'big5'", output);
    }


    public void test_show_character_set_2() throws Exception {
        String sql = "SHOW CHARACTER SET";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCharacterSetStatement show = (MySqlShowCharacterSetStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CHARACTER SET", output);
    }

    public void test_show_columns() throws Exception {
        String sql = "SHOW full columns from tb1 from db1 like 'var' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowColumnsStatement show = (SQLShowColumnsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL COLUMNS FROM db1.tb1 LIKE 'var'", output);
    }

    public void test_show_columns_1() throws Exception {
        String sql = "show columns from events where Field = 'name'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowColumnsStatement show = (SQLShowColumnsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLUMNS FROM events WHERE Field = 'name'", output);
    }

    public void test_show_columns_2() throws Exception {
        String sql = "SHOW COLUMNS FROM City";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowColumnsStatement show = (SQLShowColumnsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COLUMNS FROM City", output);
    }

    public void test_show_columns_3() throws Exception {
        String sql = "SHOW full columns from db1.tb1 like 'var' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowColumnsStatement show = (SQLShowColumnsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL COLUMNS FROM db1.tb1 LIKE 'var'", output);
    }

    public void test_show_columns_4() throws Exception {
        String sql = "SHOW full columns from tb1 from db1 where count(col)>10 ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowColumnsStatement show = (SQLShowColumnsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL COLUMNS FROM db1.tb1 WHERE count(col) > 10", output);
    }

    public void test_show_contributors() throws Exception {
        String sql = "SHOW contributors";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowContributorsStatement show = (MySqlShowContributorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CONTRIBUTORS", output);
    }

    public void test_show_create_database() throws Exception {
        String sql = "SHOW CREATE DATABASE db_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateDatabaseStatement show = (MySqlShowCreateDatabaseStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE DATABASE db_name", output);
    }

    public void test_show_create_database_2() throws Exception {
        String sql = "SHOW CREATE SCHEMA IF NOT EXISTS db1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateDatabaseStatement show = (MySqlShowCreateDatabaseStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE DATABASE IF NOT EXISTS db1", output);
    }

    public void test_show_create_event() throws Exception {
        String sql = "SHOW CREATE event db_name";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateEventStatement show = (MySqlShowCreateEventStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE EVENT db_name", output);
    }

    public void test_show_create_function() throws Exception {
        String sql = "SHOW CREATE function x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateFunctionStatement show = (MySqlShowCreateFunctionStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE FUNCTION x", output);
    }

    public void test_show_create_PROCEDURE() throws Exception {
        String sql = "SHOW CREATE PROCEDURE x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateProcedureStatement show = (MySqlShowCreateProcedureStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE PROCEDURE x", output);
    }

    public void test_show_create_table() throws Exception {
        String sql = "SHOW CREATE table x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowCreateTableStatement show = (SQLShowCreateTableStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE TABLE x", output);
    }

    public void test_show_create_table_2() throws Exception {
        String sql = "SHOW CREATE table db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowCreateTableStatement show = (SQLShowCreateTableStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE TABLE db.tb1", output);
    }

    public void test_show_create_table_3() throws Exception {
        String sql = "SHOW ALL CREATE table catalog.db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowCreateTableStatement show = (SQLShowCreateTableStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ALL CREATE TABLE catalog.db.tb1", output);
    }

    public void test_show_create_trigger() throws Exception {
        String sql = "SHOW CREATE trigger x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowCreateTriggerStatement show = (MySqlShowCreateTriggerStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE TRIGGER x", output);
    }

    public void test_show_create_view() throws Exception {
        String sql = "SHOW CREATE VIEW x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowCreateViewStatement show = (SQLShowCreateViewStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW CREATE VIEW x", output);
    }

    public void test_show_databases() throws Exception {
        String sql = "SHOW DATABASES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowDatabasesStatement show = (SQLShowDatabasesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DATABASES", output);
    }

    public void test_show_databases_1() throws Exception {
        String sql = "SHOW DATABASES LIKE 'a%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowDatabasesStatement show = (SQLShowDatabasesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DATABASES LIKE 'a%'", output);
    }

    public void test_show_databases_2() throws Exception {
        String sql = "SHOW DATABASES WHERE `Database` = 'mysql'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowDatabasesStatement show = (SQLShowDatabasesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DATABASES WHERE `Database` = 'mysql'", output);
    }

    public void test_show_engine() throws Exception {
        String sql = "SHOW ENGINE INNODB STATUS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEngineStatement show = (MySqlShowEngineStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ENGINE INNODB STATUS", output);
    }

    public void test_show_engine_1() throws Exception {
        String sql = "SHOW ENGINE PERFORMANCE_SCHEMA STATUS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEngineStatement show = (MySqlShowEngineStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ENGINE PERFORMANCE_SCHEMA STATUS", output);
    }

    public void test_show_engine_2() throws Exception {
        String sql = "SHOW ENGINE INNODB mutex";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEngineStatement show = (MySqlShowEngineStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ENGINE INNODB MUTEX", output);
    }

    public void test_show_engines() throws Exception {
        String sql = "SHOW ENGINES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEnginesStatement show = (MySqlShowEnginesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ENGINES", output);
    }

    public void test_show_engines_1() throws Exception {
        String sql = "SHOW STORAGE ENGINES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEnginesStatement show = (MySqlShowEnginesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW STORAGE ENGINES", output);
    }

    public void test_show_errors() throws Exception {
        String sql = "SHOW COUNT(*) ERRORS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowErrorsStatement show = (MySqlShowErrorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW COUNT(*) ERRORS", output);
    }

    public void test_show_errors_1() throws Exception {
        String sql = "SHOW ERRORS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowErrorsStatement show = (MySqlShowErrorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ERRORS", output);
    }

    public void test_show_errors_2() throws Exception {
        String sql = "SHOW ERRORS limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowErrorsStatement show = (MySqlShowErrorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ERRORS LIMIT 1", output);
    }

    public void test_show_errors_3() throws Exception {
        String sql = "SHOW ERRORS limit 1, 2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowErrorsStatement show = (MySqlShowErrorsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW ERRORS LIMIT 1, 2", output);
    }

    public void test_show_events() throws Exception {
        String sql = "SHOW EVENTS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEventsStatement show = (MySqlShowEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW EVENTS", output);
    }

    public void test_show_events_1() throws Exception {
        String sql = "SHOW EVENTS from x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEventsStatement show = (MySqlShowEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW EVENTS FROM x", output);
    }

    public void test_show_events_2() throws Exception {
        String sql = "SHOW EVENTS in x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEventsStatement show = (MySqlShowEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW EVENTS FROM x", output);
    }

    public void test_show_events_3() throws Exception {
        String sql = "SHOW EVENTS in x like '%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEventsStatement show = (MySqlShowEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW EVENTS FROM x LIKE '%'", output);
    }

    public void test_show_events_4() throws Exception {
        String sql = "SHOW EVENTS in x where 1 = 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowEventsStatement show = (MySqlShowEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW EVENTS FROM x WHERE 1 = 1", output);
    }

    public void test_show_function_code() throws Exception {
        String sql = "SHOW function code x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowFunctionCodeStatement show = (MySqlShowFunctionCodeStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FUNCTION CODE x", output);
    }

    public void test_show_function_status() throws Exception {
        String sql = "SHOW function status like '%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowFunctionStatusStatement show = (MySqlShowFunctionStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FUNCTION STATUS LIKE '%'", output);
    }

    public void test_show_function_status_1() throws Exception {
        String sql = "SHOW function status where 1 = 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowFunctionStatusStatement show = (MySqlShowFunctionStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FUNCTION STATUS WHERE 1 = 1", output);
    }

    public void test_show_function_status_2() throws Exception {
        String sql = "SHOW function status ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowFunctionStatusStatement show = (MySqlShowFunctionStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FUNCTION STATUS", output);
    }

    public void test_show_grants() throws Exception {
        String sql = "SHOW GRANTS FOR 'root'@'localhost';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowGrantsStatement show = (MySqlShowGrantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        assertEquals("SHOW GRANTS FOR 'root'@'localhost';", output);
    }

    public void test_show_grants_1() throws Exception {
        String sql = "SHOW GRANTS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowGrantsStatement show = (MySqlShowGrantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW GRANTS", output);
    }

    public void test_show_grants_2() throws Exception {
        String sql = "SHOW GRANTS FOR CURRENT_USER";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowGrantsStatement show = (MySqlShowGrantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW GRANTS FOR CURRENT_USER", output);
    }

    public void test_show_grants_3() throws Exception {
        String sql = "SHOW GRANTS FOR CURRENT_USER()";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowGrantsStatement show = (MySqlShowGrantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW GRANTS FOR CURRENT_USER()", output);
    }

    public void test_show_index() throws Exception {
        String sql = "SHOW index from tb1 from db";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW INDEX FROM db.tb1", output);
    }

    public void test_show_index_1() throws Exception {
        String sql = "SHOW index in tb1 in db";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW INDEX FROM db.tb1", output);
    }

    public void test_show_index_2() throws Exception {
        String sql = "SHOW index in db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW INDEX FROM db.tb1", output);
    }

    public void test_show_key() throws Exception {
        String sql = "SHOW keys from tb1 from db";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW KEYS FROM db.tb1", output);
    }

    public void test_show_key_1() throws Exception {
        String sql = "SHOW keys in tb1 in db";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW KEYS FROM db.tb1", output);
    }

    public void test_show_key_2() throws Exception {
        String sql = "SHOW keys in db.tb1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW KEYS FROM db.tb1", output);
    }

    public void test_show_key_3() throws Exception {
        String sql = "SHOW KEYS FROM tb1 IN db1 where maxlength = 2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowIndexesStatement show = (SQLShowIndexesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW KEYS FROM db1.tb1 WHERE maxlength = 2", output);
    }

    public void test_master_status() throws Exception {
        String sql = "SHOW MASTER STATUS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowMasterStatusStatement show = (MySqlShowMasterStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW MASTER STATUS", output);
    }

    public void test_open_tables() throws Exception {
        String sql = "SHOW OPEN TABLES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowOpenTablesStatement show = (MySqlShowOpenTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW OPEN TABLES", output);
    }

    public void test_open_tables_1() throws Exception {
        String sql = "SHOW OPEN TABLES FROM mysql";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowOpenTablesStatement show = (MySqlShowOpenTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW OPEN TABLES FROM mysql", output);
    }

    public void test_open_tables_2() throws Exception {
        String sql = "SHOW OPEN TABLES in mysql";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowOpenTablesStatement show = (MySqlShowOpenTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW OPEN TABLES FROM mysql", output);
    }

    public void test_open_tables_3() throws Exception {
        String sql = "SHOW OPEN TABLES in mysql like '%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowOpenTablesStatement show = (MySqlShowOpenTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW OPEN TABLES FROM mysql LIKE '%'", output);
    }

    public void test_open_tables_4() throws Exception {
        String sql = "SHOW OPEN TABLES in mysql where 1 = 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowOpenTablesStatement show = (MySqlShowOpenTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW OPEN TABLES FROM mysql WHERE 1 = 1", output);
    }

    public void test_show_open_plugins() throws Exception {
        String sql = "SHOW plugins";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowPluginsStatement show = (MySqlShowPluginsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PLUGINS", output);
    }

    public void test_show_PRIVILEGES() throws Exception {
        String sql = "SHOW PRIVILEGES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowPrivilegesStatement show = (MySqlShowPrivilegesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PRIVILEGES", output);
    }

    public void test_show_dblock() throws Exception {
        String sql = "SHOW dblock";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MysqlShowDbLockStatement show = (MysqlShowDbLockStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DBLOCK", output);
    }

    public void test_show_htc() throws Exception {
        String sql = "SHOW htc";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MysqlShowHtcStatement show = (MysqlShowHtcStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW HTC", output);
    }

    public void test_show_Stc() throws Exception {
        String sql = "SHOW Stc";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MysqlShowStcStatement show = (MysqlShowStcStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW STC", output);
    }

    public void test_show_Stc_1() throws Exception {
        String sql = "SHOW Stc his";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MysqlShowStcStatement show = (MysqlShowStcStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW STC HIS", output);
    }


    public void test_show_procedure_code() throws Exception {
        String sql = "SHOW procedure code x";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcedureCodeStatement show = (MySqlShowProcedureCodeStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROCEDURE CODE x", output);
    }

    public void test_show_procedure_status() throws Exception {
        String sql = "SHOW procedure status like '%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcedureStatusStatement show = (MySqlShowProcedureStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROCEDURE STATUS LIKE '%'", output);
    }

    public void test_show_procedure_status_1() throws Exception {
        String sql = "SHOW procedure status where 1 = 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcedureStatusStatement show = (MySqlShowProcedureStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROCEDURE STATUS WHERE 1 = 1", output);
    }

    public void test_show_procedure_status_2() throws Exception {
        String sql = "SHOW procedure status ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcedureStatusStatement show = (MySqlShowProcedureStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROCEDURE STATUS", output);
    }

    public void test_show_processList() throws Exception {
        String sql = "SHOW processlist ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcessListStatement show = (MySqlShowProcessListStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROCESSLIST", output);
    }

    public void test_show_processList_1() throws Exception {
        String sql = "SHOW full processlist ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcessListStatement show = (MySqlShowProcessListStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL PROCESSLIST", output);
    }

    public void test_show_processList_2() throws Exception {
        String sql = "SHOW processlist where id = 3";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcessListStatement show = (MySqlShowProcessListStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        assertEquals("SHOW PROCESSLIST WHERE id = 3", show.toString());
        assertEquals("show processlist where id = 3", show.toLowerCaseString());
    }

    public void test_show_processList_3() throws Exception {
        String sql = "SHOW processlist order by id desc";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcessListStatement show = (MySqlShowProcessListStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        Assert.assertEquals("SHOW PROCESSLIST ORDER BY id DESC", show.toString());
        Assert.assertEquals("show processlist order by id desc", show.toLowerCaseString());
    }

    public void test_show_processList_4() throws Exception {
        String sql = "SHOW processlist limit 3, 4";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProcessListStatement show = (MySqlShowProcessListStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        Assert.assertEquals("SHOW PROCESSLIST LIMIT 3, 4", show.toString());
        Assert.assertEquals("show processlist limit 3, 4", show.toLowerCaseString());
    }

    public void test_show_profiles() throws Exception {
        String sql = "SHOW profiles";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProfilesStatement show = (MySqlShowProfilesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROFILES", output);
    }


    public void test_show_profile() throws Exception {
        String sql = "SHOW profile";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProfileStatement show = (MySqlShowProfileStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROFILE", output);
    }

    public void test_show_profile_1() throws Exception {
        String sql = "SHOW profile all,block io,context switches,cpu,ipc,memory, page faults,source,swaps for query 2 limit 1 offset 2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowProfileStatement show = (MySqlShowProfileStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PROFILE ALL, BLOCK IO, CONTEXT SWITCHES, CPU, IPC, MEMORY, PAGE FAULTS, SOURCE, SWAPS FOR QUERY 2 LIMIT 2, 1", output);
    }

    public void test_show_relayLogEvents() throws Exception {
        String sql = "SHOW RELAYLOG EVENTS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowRelayLogEventsStatement show = (MySqlShowRelayLogEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW RELAYLOG EVENTS", output);
    }

    public void test_show_relayLogEvents_1() throws Exception {
        String sql = "SHOW RELAYLOG EVENTS IN 'x' from 3 limit 5,6";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowRelayLogEventsStatement show = (MySqlShowRelayLogEventsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW RELAYLOG EVENTS IN 'x' FROM 3 LIMIT 5, 6", output);
    }

    public void test_show_slaveHosts() throws Exception {
        String sql = "SHOW SLAVE HOSTS";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowSlaveHostsStatement show = (MySqlShowSlaveHostsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW SLAVE HOSTS", output);
    }

    public void test_show_slaveStatus() throws Exception {
        String sql = "SHOW SLAVE Status";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowSlaveStatusStatement show = (MySqlShowSlaveStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW SLAVE STATUS", output);
    }

    public void test_show_status() throws Exception {
        String sql = "SHOW STATUS LIKE 'Key%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowStatusStatement show = (MySqlShowStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW STATUS LIKE 'Key%'", output);
    }

    public void test_show_slow() throws Exception {
        String sql = "SHOW SLOW WHERE 1=1 ORDER BY A LIMIT 10";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowSlowStatement show = (MySqlShowSlowStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW SLOW WHERE 1 = 1 ORDER BY A LIMIT 10", output);
    }

    public void test_show_slow_1() throws Exception {
        String sql = "SHOW PHYSICAL_SLOW WHERE 1=1 ORDER BY A LIMIT 10";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowSlowStatement show = (MySqlShowSlowStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PHYSICAL_SLOW WHERE 1 = 1 ORDER BY A LIMIT 10", output);
    }

    public void test_show_sequence() throws Exception {
        String sql = "SHOW SEQUENCES WHERE 1=1 ORDER BY A LIMIT 10";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowSequencesStatement show = (MySqlShowSequencesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW SEQUENCES WHERE 1 = 1 ORDER BY A LIMIT 10", output);
    }

    public void test_show_rule() throws Exception {
        String sql = "SHOW RULE FROM A WHERE 1=1 ORDER BY A LIMIT 10";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowRuleStatement show = (MySqlShowRuleStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW RULE FROM A WHERE 1 = 1 ORDER BY A LIMIT 10", output);
    }


    public void test_show_rule_1() throws Exception {
        String sql = "SHOW FULL RULE FROM A WHERE 1=1 ORDER BY A LIMIT 10";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowRuleStatement show = (MySqlShowRuleStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL RULE FROM A WHERE 1 = 1 ORDER BY A LIMIT 10", output);
    }

    public void test_show_table_status() throws Exception {
        String sql = "SHOW TABLE STATUS FROM mysql";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTableStatusStatement show = (MySqlShowTableStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TABLE STATUS FROM mysql", output);
    }
    public void test_show_table_status_1() throws Exception {
        String sql = "SHOW TABLE STATUS LIKE 'test%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTableStatusStatement show = (MySqlShowTableStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TABLE STATUS LIKE 'test%'", output);
    }
    public void test_show_table_status_2() throws Exception {
        String sql = "SHOW TABLE STATUS FROM testdb.test_group LIKE 'test%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTableStatusStatement show = (MySqlShowTableStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TABLE STATUS FROM testdb.test_group LIKE 'test%'", output);
    }
    public void test_show_table_status_3() throws Exception {
        String sql = "SHOW TABLE STATUS FROM testdb.test_group WHERE schema_name = 'ss' or schema_name = 'ss1'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTableStatusStatement show = (MySqlShowTableStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TABLE STATUS FROM testdb.test_group WHERE schema_name = 'ss'\n"
                + "OR schema_name = 'ss1'", output);
    }
    public void test_show_table_status_4() throws Exception {
        String sql = "SHOW TABLE STATUS IN testdb.test_group WHERE schema_name = 'ss' or schema_name = 'ss1'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTableStatusStatement show = (MySqlShowTableStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TABLE STATUS FROM testdb.test_group WHERE schema_name = 'ss'\n"
                + "OR schema_name = 'ss1'", output);
    }

    public void test_show_database_status() throws Exception {
        String sql = "SHOW DATABASE STATUS LIKE foo";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowDatabaseStatusStatement show = (MySqlShowDatabaseStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DATABASE STATUS LIKE foo", output);
    }

    public void test_show_database_status_1() throws Exception {
        String sql = "SHOW DATABASE STATUS WHERE 1 = 1 ORDER BY a LIMIT 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowDatabaseStatusStatement show = (MySqlShowDatabaseStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DATABASE STATUS WHERE 1 = 1 ORDER BY a LIMIT 1", output);
    }

    public void test_show_triggers() throws Exception {
        String sql = "SHOW TRIGGERS LIKE 'acc%'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTriggersStatement show = (MySqlShowTriggersStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TRIGGERS LIKE 'acc%'", output);
    }

    public void test_show_partitions() throws Exception {
        String sql = "SHOW PARTITIONS from a";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement show = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PARTITIONS FROM a", output);
    }

    public void test_show_partitions_2() throws Exception {
        String sql = "SHOW PARTITIONS a";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement show = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW PARTITIONS FROM a", output);
    }

    public void test_show_tables_1() throws Exception {
        String sql = "SHOW FULL TABLES from a";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowTablesStatement show = (SQLShowTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL TABLES FROM a", output);
    }


    public void test_show_tables_2() throws Exception {
        String sql = "SHOW FULL TABLES LIKE a";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLShowTablesStatement show = (SQLShowTablesStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW FULL TABLES LIKE a", output);
    }

    public void test_show_trace_01() throws Exception {
        String sql = "SHOW TRACE WHERE 1=1 order by a limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTraceStatement show = (MySqlShowTraceStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TRACE WHERE 1 = 1 ORDER BY a LIMIT 1", output);
    }

    public void test_show_trace_02() throws Exception {
        String sql = "SHOW TRACE WHERE 1=1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTraceStatement show = (MySqlShowTraceStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TRACE WHERE 1 = 1", output);
    }

    public void test_show_trace_03() throws Exception {
        String sql = "SHOW TRACE WHERE 1=1 limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTraceStatement show = (MySqlShowTraceStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TRACE WHERE 1 = 1 LIMIT 1", output);
    }

    public void test_show_topology_01() throws Exception {
        String sql = "SHOW TOPOLOGY T1 WHERE 1=1 limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTopologyStatement show = (MySqlShowTopologyStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TOPOLOGY FROM T1 WHERE 1 = 1 LIMIT 1", output);
    }

    public void test_show_triggers_1() throws Exception {
        String sql = "SHOW triggers in db where strcmp('test1', 'test2')";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowTriggersStatement show = (MySqlShowTriggersStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW TRIGGERS FROM db WHERE strcmp('test1', 'test2')", output);
    }

    public void test_show_broadcast_1() throws Exception {
        String sql = "SHOW broadcasts WHERE 1=1 order by a limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowBroadcastsStatement show = (MySqlShowBroadcastsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW BROADCASTS WHERE 1 = 1 ORDER BY a LIMIT 1", output);
    }

    public void test_show_ds_1() throws Exception {
        String sql = "SHOW DS WHERE 1=1 order by a limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowDsStatement show = (MySqlShowDsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DS WHERE 1 = 1 ORDER BY a LIMIT 1", output);
    }

    public void test_show_dbstatus_1() throws Exception {
        String sql = "SHOW ddl status WHERE 1=1 order by a limit 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowDdlStatusStatement show = (MySqlShowDdlStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        Assert.assertEquals("SHOW DDL STATUS WHERE 1 = 1 ORDER BY a LIMIT 1", output);
    }

    public void test_show_variants() throws Exception {
        String sql = "SHOW VARIABLES LIKE '%size%';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowVariantsStatement show = (MySqlShowVariantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        assertEquals("SHOW VARIABLES LIKE '%size%';", output);
    }

    public void test_show_variants_1() throws Exception {
        String sql = "SHOW GLOBAL VARIABLES LIKE '%size%';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowVariantsStatement show = (MySqlShowVariantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        assertEquals("SHOW GLOBAL VARIABLES LIKE '%size%';", output);
    }

    public void test_show_variants_2() throws Exception {
        String sql = "SHOW SESSION VARIABLES LIKE '%size%';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowVariantsStatement show = (MySqlShowVariantsStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(show);
        assertEquals("SHOW SESSION VARIABLES LIKE '%size%';", output);
    }

    public void test_plancache_1() throws Exception {
        String sql = "SHOW PLANCACHE status";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlShowPlanCacheStatusStatement stmt = (MySqlShowPlanCacheStatusStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW PLANCACHE STATUS", output);
    }

    public void test_plancache_2() throws Exception {
        String sql = "DISABLED PLANCACHE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlDisabledPlanCacheStatement stmt = (MySqlDisabledPlanCacheStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("DISABLED PLANCACHE", output);
    }

    public void test_plancache_3() throws Exception {
        String sql = "CLEAR PLANCACHE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlClearPlanCacheStatement stmt = (MySqlClearPlanCacheStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CLEAR PLANCACHE", output);
    }

    public void test_plancache_4() throws Exception {
        String sql = "UPDATE PLANCACHE SELECT A FROM T1 where t1.a = 1 TO SELECT B2 FROM T2 where t2.a = 2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlUpdatePlanCacheStatement stmt = (MySqlUpdatePlanCacheStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("UPDATE PLANCACHE SELECT A\n"
                + "FROM T1\n"
                + "WHERE t1.a = 1\n"
                + " TO \n"
                + "SELECT B2\n"
                + "FROM T2\n"
                + "WHERE t2.a = 2", output);
    }

    public void test_plancache_5() throws Exception {
        String sql = "EXPLAIN PLANCACHE";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        MySqlExplainPlanCacheStatement stmt = (MySqlExplainPlanCacheStatement) parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("EXPLAIN PLANCACHE", output);
    }


//
//    public void testShow() throws Exception {
//
//        sql =
//                "SHOW profile all,block io,context switches,cpu,ipc,memory,"
//                        + "page faults,source,swaps for query 2 limit 1 offset 2";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW PROFILE ALL, BLOCK IO, CONTEXT SWITCHES, CPU, IPC, MEMORY, "
//                            + "PAGE FAULTS, SOURCE, SWAPS FOR QUERY 2 LIMIT 2, 1", output);
//
//        sql = "SHOW profile";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW PROFILE", output);
//
//        sql = "SHOW profile all,block io,context switches,cpu,ipc," + "memory,page faults,source,swaps for query 2";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW PROFILE ALL, BLOCK IO, CONTEXT SWITCHES, CPU, IPC, "
//                            + "MEMORY, PAGE FAULTS, SOURCE, SWAPS FOR QUERY 2", output);
//
//        sql = "SHOW profile all for query 2";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW PROFILE ALL FOR QUERY 2", output);
//
//        sql = "SHOW slave hosts";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SLAVE HOSTS", output);
//
//        sql = "SHOW slave status";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SLAVE STATUS", output);
//
//        sql = "SHOW global status like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL STATUS LIKE 'expr'", output);
//
//        sql = "SHOW global status where ${abc}";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL STATUS WHERE ${abc}", output);
//
//        sql = "SHOW session status like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS LIKE 'expr'", output);
//
//        sql = "SHOW session status where ?";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS WHERE ?", output);
//
//        sql = "SHOW status like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS LIKE 'expr'", output);
//
//        sql = "SHOW status where 0b10^b'11'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS WHERE b'10' ^ b'11'", output);
//
//        sql = "SHOW status";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS", output);
//
//        sql = "SHOW global status";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL STATUS", output);
//
//        sql = "SHOW session status";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION STATUS", output);
//
//        sql = "SHOW table status from db like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLE STATUS FROM db LIKE 'expr'", output);
//
//        sql = "SHOW table status in db where (select a)>(select b)";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLE STATUS FROM db WHERE (SELECT a) > (SELECT b)", output);
//
//        sql = "SHOW table status from db where id1=a||b";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLE STATUS FROM db WHERE id1 = a OR b", output);
//
//        sql = "SHOW table status ";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLE STATUS", output);
//
//        sql = "SHOW tables from db like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLES FROM db LIKE 'expr'", output);
//
//        sql = "SHOW tables in db where !a";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLES FROM db WHERE ! a", output);
//
//        sql = "SHOW tables like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLES LIKE 'expr'", output);
//
//        sql = "SHOW tables where log((select a))=b";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLES WHERE LOG(SELECT a) = b", output);
//
//        sql = "SHOW tables ";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TABLES", output);
//
//        sql = "SHOW full tables from db like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW FULL TABLES FROM db LIKE 'expr'", output);
//
//        sql = "SHOW full tables in db where id1=abs((select a))";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW FULL TABLES FROM db WHERE id1 = ABS(SELECT a)", output);
//
//        sql = "SHOW full tables ";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW FULL TABLES", output);
//
//        sql = "SHOW triggers from db like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TRIGGERS FROM db LIKE 'expr'", output);
//
//        sql = "SHOW triggers in db where strcmp('test1','test2')";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TRIGGERS FROM db WHERE STRCMP('test1', 'test2')", output);
//
//        sql = "SHOW triggers ";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW TRIGGERS", output);
//
//        sql = "SHOW global variables like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL VARIABLES LIKE 'expr'", output);
//
//        sql = "SHOW global variables where ~a is null";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL VARIABLES WHERE ~ a IS NULL", output);
//
//        sql = "SHOW session variables like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES LIKE 'expr'", output);
//
//        sql = "SHOW session variables where a*b+1=c";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES WHERE a * b + 1 = c", output);
//
//        sql = "SHOW variables like 'expr'";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES LIKE 'expr'", output);
//
//        sql = "SHOW variables where a&&b";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES WHERE a AND b", output);
//
//        sql = "SHOW variables";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES", output);
//
//        sql = "SHOW global variables";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW GLOBAL VARIABLES", output);
//
//        sql = "SHOW session variables";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW SESSION VARIABLES", output);
//
//        sql = "SHOW warnings limit 1,2 ";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW WARNINGS LIMIT 1, 2", output);
//
//        sql = "SHOW warnings";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW WARNINGS", output);
//
//        sql = "SHOW count(*) warnings";
//        lexer = new SQLLexer(sql);
//        parser = new DALParser(lexer, new SQLExprParser(lexer));
//        show = (DALShowStatement) parser.show();
//        parser.match(Token.EOF);
//        output = output2MySQL(show, sql);
//        Assert.assertEquals("SHOW COUNT(*) WARNINGS", output);
//    }
}
