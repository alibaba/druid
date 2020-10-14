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
package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableAddIndex_9 extends TestCase {

    public void test_0() throws Exception {
        String sql =
            "ALTER TABLE t_order ADD FULLTEXT INDEX `g_i_buyer` (`buyer_id`);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        
        Assert.assertEquals("ALTER TABLE t_order\n" +
                "\tADD FULLTEXT INDEX `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt));
        
        Assert.assertEquals("alter table t_order\n" +
                "\tadd fulltext index `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("t_order");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_1() throws Exception {
        String sql =
            "ALTER TABLE t_order ADD CLUSTERED INDEX `g_i_buyer` (`buyer_id`);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE t_order\n" +
                "\tADD CLUSTERED INDEX `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table t_order\n" +
                "\tadd clustered index `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("t_order");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_2() throws Exception {
        String sql =
            "ALTER TABLE t_order ADD CLUSTERED KEY `g_i_buyer` (`buyer_id`);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE t_order\n" +
                "\tADD CLUSTERED KEY `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table t_order\n" +
                "\tadd clustered key `g_i_buyer` (`buyer_id`);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("t_order");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_3() throws Exception {
        String sql =
            "alter table xxx add key `idx_001`(`col1`,`current_date`,`col2`)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE xxx\n" +
                "\tADD KEY `idx_001` (`col1`, `current_date`, `col2`)", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table xxx\n" +
                "\tadd key `idx_001` (`col1`, `current_date`, `col2`)", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("xxx");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_4() throws Exception {
        String sql =
            "alter table xxx add key `idx_001`(`col1`,`current_time`,`col2`)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE xxx\n" +
                "\tADD KEY `idx_001` (`col1`, `current_time`, `col2`)", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table xxx\n" +
                "\tadd key `idx_001` (`col1`, `current_time`, `col2`)", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("xxx");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_5() throws Exception {
        String sql =
            "alter table xxx add key `idx_001`(`current_timestamp`,`curdate`,`LOCALTIME`, `LOCALTIMESTAMP`)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE xxx\n" +
                "\tADD KEY `idx_001` (`current_timestamp`, `curdate`, `LOCALTIME`, `LOCALTIMESTAMP`)", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table xxx\n" +
                "\tadd key `idx_001` (`current_timestamp`, `curdate`, `LOCALTIME`, `LOCALTIMESTAMP`)", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("xxx");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

}
