package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableTest51_table_options extends TestCase {

    public void test_0_options_no_comma_no_eq() {
        String sql = "alter table test001\n" +
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
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tAUTO_INCREMENT = 1 AVG_ROW_LENGTH = 1 CHARACTER SET = utf8 CHECKSUM = 0 COLLATE = utf8_unicode_ci COMMENT = 'hehe' COMPRESSION = 'LZ4' CONNECTION = 'conn' COLLATE = 'path' DELAY_KEY_WRITE = 1 ENCRYPTION = 'N' ENGINE = innodb INSERT_METHOD = no KEY_BLOCK_SIZE = 32 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = DEFAULT PASSWORD = 'psw' ROW_FORMAT = dynamic STATS_AUTO_RECALC = DEFAULT STATS_PERSISTENT = DEFAULT STATS_SAMPLE_PAGES = 10 TABLESPACE = `tbs_name` STORAGE memory UNION = (tb1, tb2, tb3) AUTO_INCREMENT = 1", SQLUtils.toMySqlString(stmt));

    }

    public void test_0_options_comma_eq() {
        String sql = "alter table test001\n" +
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
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        Assert.assertEquals("ALTER TABLE test001\n" +
                "\tAUTO_INCREMENT = 2 AVG_ROW_LENGTH = 2 CHARACTER SET = utf8 CHECKSUM = 1 COLLATE = utf8_unicode_ci COMMENT = 'hehe' COMPRESSION = 'NONE' CONNECTION = 'conn' COLLATE = 'path' DELAY_KEY_WRITE = 0 ENCRYPTION = 'Y' ENGINE = innodb INSERT_METHOD = first KEY_BLOCK_SIZE = 64 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = 0 PASSWORD = 'psw' ROW_FORMAT = fixed STATS_AUTO_RECALC = 1 STATS_PERSISTENT = 0 STATS_SAMPLE_PAGES = 2 TABLESPACE = `tbs_name` UNION = (tb1, tb2, tb3);", SQLUtils.toMySqlString(stmt));

    }

}
