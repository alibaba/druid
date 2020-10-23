package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest152_table_options
 * @description
 *
 * table_option:
 *     AUTO_INCREMENT [=] value
 *   | AVG_ROW_LENGTH [=] value
 *   | [DEFAULT] CHARACTER SET [=] charset_name
 *   | CHECKSUM [=] {0 | 1}
 *   | [DEFAULT] COLLATE [=] collation_name
 *   | COMMENT [=] 'string'
 *   | COMPRESSION [=] {'ZLIB'|'LZ4'|'NONE'}
 *   | CONNECTION [=] 'connect_string'
 *   | {DATA|INDEX} DIRECTORY [=] 'absolute path to directory'
 *   | DELAY_KEY_WRITE [=] {0 | 1}
 *   | ENCRYPTION [=] {'Y' | 'N'}
 *   | ENGINE [=] engine_name
 *   | INSERT_METHOD [=] { NO | FIRST | LAST }
 *   | KEY_BLOCK_SIZE [=] value
 *   | MAX_ROWS [=] value
 *   | MIN_ROWS [=] value
 *   | PACK_KEYS [=] {0 | 1 | DEFAULT}
 *   | PASSWORD [=] 'string'
 *   | ROW_FORMAT [=] {DEFAULT|DYNAMIC|FIXED|COMPRESSED|REDUNDANT|COMPACT}
 *   | STATS_AUTO_RECALC [=] {DEFAULT|0|1}
 *   | STATS_PERSISTENT [=] {DEFAULT|0|1}
 *   | STATS_SAMPLE_PAGES [=] value
 *   | TABLESPACE tablespace_name [STORAGE {DISK|MEMORY}]
 *   | UNION [=] (tbl_name[,tbl_name]...)
 *
 * @Author zzy
 * @Date 2019-05-15 16:26
 */
public class MySqlCreateTableTest152_table_options extends TestCase {

    public void test_no_comma_no_eq() {
        String sql = "create table tb (a int)" +
                "auto_increment 1\n" +
                "avg_row_length 1\n" +
                "default character set utf8\n" +
                "checksum 0\n" +
                "default collate utf8_unicode_ci\n" +
                "comment 'hehe'\n" +
                "compression 'LZ4'\n" +
                "connection 'conn'\n" +
                "index directory 'path'\n" +
                "delay_key_write 1\n" +
                "encryption 'N'\n" +
                "engine innodb\n" +
                "insert_method no\n" +
                "key_block_size 32\n" +
                "max_rows 999\n" +
                "min_rows 1\n" +
                "pack_keys default\n" +
                "password 'psw'\n" +
                "row_format dynamic\n" +
                "stats_auto_recalc default\n" +
                "stats_persistent default\n" +
                "stats_sample_pages 10\n" +
                "tablespace `tbs_name` storage memory\n" +
                "union (tb1,tb2,tb3)\n" +
                "auto_increment 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE tb (\n" +
                "\ta int\n" +
                ") AUTO_INCREMENT = 1 AVG_ROW_LENGTH = 1 CHARACTER SET = utf8 CHECKSUM = 0 COLLATE = utf8_unicode_ci COMPRESSION = 'LZ4' CONNECTION = 'conn' INDEX DIRECTORY = 'path' DELAY_KEY_WRITE = 1 ENCRYPTION = 'N' ENGINE = innodb INSERT_METHOD = no KEY_BLOCK_SIZE = 32 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = DEFAULT PASSWORD = 'psw' ROW_FORMAT = dynamic STATS_AUTO_RECALC = DEFAULT STATS_PERSISTENT = DEFAULT STATS_SAMPLE_PAGES = 10 TABLESPACE `tbs_name` STORAGE memory UNION = (tb1, tb2, tb3) AUTO_INCREMENT = 1 COMMENT 'hehe'", stmt.toString());

        assertEquals("create table tb (\n" +
                "\ta int\n" +
                ") auto_increment = 1 avg_row_length = 1 character set = utf8 checksum = 0 collate = utf8_unicode_ci compression = 'LZ4' connection = 'conn' index directory = 'path' delay_key_write = 1 encryption = 'N' engine = innodb insert_method = no key_block_size = 32 max_rows = 999 min_rows = 1 pack_keys = default password = 'psw' row_format = dynamic stats_auto_recalc = default stats_persistent = default stats_sample_pages = 10 tablespace `tbs_name` storage memory union = (tb1, tb2, tb3) auto_increment = 1 comment 'hehe'", stmt.toLowerCaseString());
    }

    public void test_options_comma_eq() {
        String sql = "create table tb (a int)" +
                "auto_increment = 2,\n" +
                "avg_row_length = 2,\n" +
                "character set = utf8,\n" +
                "checksum = 1,\n" +
                "collate = utf8_unicode_ci,\n" +
                "comment = 'hehe',\n" +
                "compression = 'NONE',\n" +
                "connection = 'conn',\n" +
                "data directory = 'path',\n" +
                "delay_key_write = 0,\n" +
                "encryption = 'Y',\n" +
                "engine = innodb,\n" +
                "insert_method = first,\n" +
                "key_block_size = 64,\n" +
                "max_rows = 999,\n" +
                "min_rows = 1,\n" +
                "pack_keys = 0,\n" +
                "password = 'psw',\n" +
                "row_format = fixed,\n" +
                "stats_auto_recalc = 1,\n" +
                "stats_persistent = 0,\n" +
                "stats_sample_pages = 2,\n" +
                "tablespace `tbs_name`\n" +
                "union = (tb1,tb2,tb3);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE tb (\n" +
                "\ta int\n" +
                ") AUTO_INCREMENT = 2 AVG_ROW_LENGTH = 2 CHARACTER SET = utf8 CHECKSUM = 1 COLLATE = utf8_unicode_ci COMPRESSION = 'NONE' CONNECTION = 'conn' DATA DIRECTORY = 'path' DELAY_KEY_WRITE = 0 ENCRYPTION = 'Y' ENGINE = innodb INSERT_METHOD = first KEY_BLOCK_SIZE = 64 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = 0 PASSWORD = 'psw' ROW_FORMAT = fixed STATS_AUTO_RECALC = 1 STATS_PERSISTENT = 0 STATS_SAMPLE_PAGES = 2 TABLESPACE `tbs_name` UNION = (tb1, tb2, tb3) COMMENT 'hehe';", stmt.toString());

        assertEquals("create table tb (\n" +
                "\ta int\n" +
                ") auto_increment = 2 avg_row_length = 2 character set = utf8 checksum = 1 collate = utf8_unicode_ci compression = 'NONE' connection = 'conn' data directory = 'path' delay_key_write = 0 encryption = 'Y' engine = innodb insert_method = first key_block_size = 64 max_rows = 999 min_rows = 1 pack_keys = 0 password = 'psw' row_format = fixed stats_auto_recalc = 1 stats_persistent = 0 stats_sample_pages = 2 tablespace `tbs_name` union = (tb1, tb2, tb3) comment 'hehe';", stmt.toLowerCaseString());
    }

}
