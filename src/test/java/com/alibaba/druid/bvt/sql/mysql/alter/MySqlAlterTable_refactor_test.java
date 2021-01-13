package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlAlterTable_refactor_test
 * @description 只测试parser语法解析能力，SQL语句不具备合理性
 * @Author zzy
 * @Date 2019-07-02 15:27
 */
public class MySqlAlterTable_refactor_test extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "alter table test001\n" +
                "add column x int first," +
                "add column (x int, x int)," +
                "add index (a,b)," +
                "add fulltext index (a,b)," +
                "add constraint syb primary key (a)," +
                "add check (1+2)," +
                "algorithm = default," +
                "alter column c set default 1," +
                "change column a b int first," +
                "convert to character set utf8 collate utf8_generic_ci," +
                "disable keys," +
                "import tablespace," +
                "drop cc," +
                "drop key cc," +
                "drop primary key," +
                "drop foreign key fk," +
                "force," +
                "lock none," +
                "modify a int first," +
                "rename key a to b," +
                "rename to tb2," +
                "without validation," +
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
                "auto_increment 1,\n" +
                "order by a,b,c";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        assertEquals("ALTER TABLE test001\n" +
                        "\tADD COLUMN x int FIRST,\n" +
                        "\tADD COLUMN (x int, x int),\n" +
                        "\tADD INDEX (a, b),\n" +
                        "\tADD FULLTEXT INDEX (a, b),\n" +
                        "\tADD PRIMARY KEY (a),\n" +
                        "\tADD CHECK (1 + 2),\n" +
                        "\tALGORITHM = default,\n" +
                        "\tALTER COLUMN c SET DEFAULT 1,\n" +
                        "\tCHANGE COLUMN a b int FIRST,\n" +
                        "\tCONVERT TO CHARACTER SET utf8 COLLATE utf8_generic_ci,\n" +
                        "\tDISABLE KEYS,\n" +
                        "\tIMPORT TABLESPACE,\n" +
                        "\tDROP COLUMN cc,\n" +
                        "\tDROP KEY cc,\n" +
                        "\tDROP PRIMARY KEY,\n" +
                        "\tDROP FOREIGN KEY fk,\n" +
                        "\tFORCE,\n" +
                        "\tLOCK = none,\n" +
                        "\tMODIFY COLUMN a int FIRST,\n" +
                        "\tRENAME INDEX a TO b,\n" +
                        "\tRENAME TO tb2,\n" +
                        "\tWITHOUT VALIDATION,\n" +
                        "\tORDER BY a, b, c,\n" +
                        "\tAUTO_INCREMENT = 1 AVG_ROW_LENGTH = 1 CHARACTER SET = utf8 CHECKSUM = 0 COLLATE = utf8_unicode_ci COMMENT = 'hehe' COMPRESSION = 'LZ4' CONNECTION = 'conn' COLLATE = 'path' DELAY_KEY_WRITE = 1 ENCRYPTION = 'N' ENGINE = innodb INSERT_METHOD = no KEY_BLOCK_SIZE = 32 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = DEFAULT PASSWORD = 'psw' ROW_FORMAT = dynamic STATS_AUTO_RECALC = DEFAULT STATS_PERSISTENT = DEFAULT STATS_SAMPLE_PAGES = 10 TABLESPACE = `tbs_name` STORAGE memory UNION = (tb1, tb2, tb3) AUTO_INCREMENT = 1",
                SQLUtils.toMySqlString(statementList.get(0)));
    }

    public void test_1() throws Exception {
        String sql = "alter table test001\n" +
                "add index i0 using hash (a,b) key_block_size 32 using btree comment 'index comment'," +
                "add fulltext key ftk0 (a,b) key_block_size=16 with parser ngram comment 'fulltext comment'," +
                "add spatial sp0 (a,b) key_block_size=16 comment 'spatial comment'," +
                "add constraint syb0 primary key using hash (a,b,c) using btree comment 'pk comment'," +
                "add constraint unique uk0 using hash (a,b) using btree comment 'uk comment'," +
                "add index i1 (a,b) covering (c,d) dbpartition by hash(a) tbpartition by MM(actionDate) tbpartitions 12;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        assertEquals("ALTER TABLE test001\n" +
                        "\tADD INDEX i0 USING btree (a, b) KEY_BLOCK_SIZE = 32 COMMENT 'index comment',\n" +
                        "\tADD FULLTEXT KEY ftk0 (a, b) KEY_BLOCK_SIZE = 16 WITH PARSER ngram COMMENT 'fulltext comment',\n" +
                        "\tADD SPATIAL sp0 (a, b) KEY_BLOCK_SIZE = 16 COMMENT 'spatial comment',\n" +
                        "\tADD PRIMARY KEY USING btree (a, b, c) COMMENT 'pk comment',\n" +
                        "\tADD UNIQUE uk0 USING btree (a, b) COMMENT 'uk comment',\n" +
                        "\tADD INDEX i1 (a, b) COVERING (c, d) DBPARTITION BY hash(a) TBPARTITION BY MM(actionDate) TBPARTITIONS 12;",
                SQLUtils.toMySqlString(statementList.get(0)));
    }

}
