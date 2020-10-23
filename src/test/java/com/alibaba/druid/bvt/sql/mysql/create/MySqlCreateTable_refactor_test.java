package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTable_refactor_test
 * @description 只测试parser语法解析能力，SQL语句不具备合理性
 * @Author zzy
 * @Date 2019-07-02 15:18
 */
public class MySqlCreateTable_refactor_test extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table test001\n" +
                "(" +
                "a varchar(10)," +
                "b varchar(10)," +
                "index c using btree (a(4) desc) comment 'hehe' key_block_size 4," +
                "fulltext d (b) with parser ngram," +
                "constraint symb primary key (a)," +
                "constraint symb unique key e (b)," +
                "constraint symb foreign key (b) references tb (a)" +
                ")" +
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
                "dbpartition by hash(a) tbpartition by hash(b) tbpartitions 4 partition by hash(b)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE test001 (\n" +
                        "\ta varchar(10),\n" +
                        "\tb varchar(10),\n" +
                        "\tINDEX c USING btree(a(4) DESC) KEY_BLOCK_SIZE = 4 COMMENT 'hehe',\n" +
                        "\tFULLTEXT INDEX d(b) WITH PARSER ngram,\n" +
                        "\tPRIMARY KEY (a),\n" +
                        "\tUNIQUE KEY e (b),\n" +
                        "\tCONSTRAINT symb FOREIGN KEY (b) REFERENCES tb (a)\n" +
                        ") AUTO_INCREMENT = 1 AVG_ROW_LENGTH = 1 CHARACTER SET = utf8 CHECKSUM = 0 COLLATE = utf8_unicode_ci COMPRESSION = 'LZ4' CONNECTION = 'conn' INDEX DIRECTORY = 'path' DELAY_KEY_WRITE = 1 ENCRYPTION = 'N' ENGINE = innodb INSERT_METHOD = no KEY_BLOCK_SIZE = 32 MAX_ROWS = 999 MIN_ROWS = 1 PACK_KEYS = DEFAULT PASSWORD = 'psw' ROW_FORMAT = dynamic STATS_AUTO_RECALC = DEFAULT STATS_PERSISTENT = DEFAULT STATS_SAMPLE_PAGES = 10 TABLESPACE `tbs_name` STORAGE memory UNION = (tb1, tb2, tb3) AUTO_INCREMENT = 1 COMMENT 'hehe'\n" +
                        "PARTITION BY HASH (b)\n" +
                        "DBPARTITION BY hash(a)\n" +
                        "TBPARTITION BY hash(b) TBPARTITIONS 4",
                SQLUtils.toMySqlString(statementList.get(0)));
    }

}
