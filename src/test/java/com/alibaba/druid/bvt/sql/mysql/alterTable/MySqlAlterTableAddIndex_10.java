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

/**
 * @version 1.0
 * @ClassName MySqlAlterTableAddIndex_10
 * @description 测试index, fulltext, PK, UK的index option的支持情况，包括后者index type覆盖前者
 * @Author zzy
 * @Date 2019-05-06 15:45
 */
public class MySqlAlterTableAddIndex_10 extends TestCase {

    public void test_alter_table_add_index_with_options() throws Exception {
        String sql = "ALTER TABLE test001 ADD INDEX `i` using btree (`b`) key_block_size=32 comment 'hehe';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD INDEX `i` USING btree (`b`) KEY_BLOCK_SIZE = 32 COMMENT 'hehe';", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table test001\n" +
                "\tadd index `i` using btree (`b`) key_block_size = 32 comment 'hehe';", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test001");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_index_multi_type() throws Exception {
        String sql = "ALTER TABLE test001 ADD INDEX `i2` using btree (`b`) key_block_size=32 comment 'hehe' using hash;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD INDEX `i2` USING hash (`b`) KEY_BLOCK_SIZE = 32 COMMENT 'hehe';", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table test001\n" +
                "\tadd index `i2` using hash (`b`) key_block_size = 32 comment 'hehe';", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test001");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_fulltext_index_option_ngram() throws Exception {
        String sql = "alter table test001 add fulltext index (b) with parser ngram key_block_size=32 comment 'hehe';";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD FULLTEXT INDEX (b) KEY_BLOCK_SIZE = 32 WITH PARSER ngram COMMENT 'hehe';", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table test001\n" +
                "\tadd fulltext index (b) key_block_size = 32 with parser ngram comment 'hehe';", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test001");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_constraint_primary_key_with_options() throws Exception {
        String sql = "alter table test001 add constraint primary key using btree (b) key_block_size=32 comment 'hehe' using btree;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD PRIMARY KEY USING btree (b) KEY_BLOCK_SIZE = 32 COMMENT 'hehe';", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table test001\n" +
                "\tadd primary key using btree (b) key_block_size = 32 comment 'hehe';", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test001");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_constraint_unique_key_with_options() throws Exception {
        String sql = "alter table test001 add constraint unique key `uk` using btree (b) key_block_size=32 comment 'hehe' using btree;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tADD UNIQUE KEY `uk` USING btree (b) KEY_BLOCK_SIZE = 32 COMMENT 'hehe';", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table test001\n" +
                "\tadd unique key `uk` using btree (b) key_block_size = 32 comment 'hehe';", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test001");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

}
