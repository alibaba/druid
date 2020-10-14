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
public class MySqlAlterTableAddIndex_11 extends TestCase {

    public void test_alter_table_add_index_with_options() throws Exception {
        String sql = "ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2 ADD FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tADD FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2 WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict;", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tadd fulltext index title_fulltext_idx (title) with index analyzer index_analyzer2 with query analyzer query_analyzer2 with dict user_dict;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("aliyun_poc_db.tbl_custom_analyzer2");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_index_with_options2() throws Exception {
        String sql = "ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2 ADD FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tADD FULLTEXT INDEX title_fulltext_idx (title) WITH INDEX ANALYZER index_analyzer2", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tadd fulltext index title_fulltext_idx (title) with index analyzer index_analyzer2", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("aliyun_poc_db.tbl_custom_analyzer2");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_index_with_options3() throws Exception {
        String sql = "ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2 ADD FULLTEXT INDEX title_fulltext_idx (title) WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tADD FULLTEXT INDEX title_fulltext_idx (title) WITH QUERY ANALYZER query_analyzer2 WITH DICT user_dict;", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tadd fulltext index title_fulltext_idx (title) with query analyzer query_analyzer2 with dict user_dict;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("aliyun_poc_db.tbl_custom_analyzer2");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

    public void test_alter_table_add_index_with_options4() throws Exception {
        String sql = "ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2 ADD FULLTEXT INDEX title_fulltext_idx (title) WITH DICT user_dict;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tADD FULLTEXT INDEX title_fulltext_idx (title) WITH DICT user_dict;", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table aliyun_poc_db.tbl_custom_analyzer2\n" +
                "\tadd fulltext index title_fulltext_idx (title) with dict user_dict;", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("aliyun_poc_db.tbl_custom_analyzer2");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

}
