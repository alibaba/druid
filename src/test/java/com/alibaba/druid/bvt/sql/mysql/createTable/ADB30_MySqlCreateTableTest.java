package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

public class ADB30_MySqlCreateTableTest extends MysqlTest {

    public void test_create_table1() {
        String sql = "CREATE TABLE db_name.table_name (\n" +
                "    auto_id bigint NOT NULL AUTO_INCREMENT,\n" +
                "    col1 boolean NULL DEFAULT 1,\n" +
                "    col2 tinyint null,\n" +
                "    col3 smallint NULL  DEFAULT 2,\n" +
                "    col4 int not null DEFAULT 3,\n" +
                "    col5 bigint  ,\n" +
                "    col6 float ,\n" +
                "    col7 double ,\n" +
                "    col8 varchar,\n" +
                "    col9 timestamp DEFAULT current_timestamp,\n" +
                "    col10 date DEFAULT current_date,\n" +
                "    col11 multivalue DEFAULT 'a' delimiter_tokenizer ': ,' value_type 'varchar int',\n" +
                "    col13 multivalue DEFAULT 'a' nlp_tokenizer 'ik' value_type 'varchar int',\n" +
                "    col14 geo2d   delimiter_tokenizer ',',\n" +
                "    key col3_index(col3),\n" +
                "    key col4_index(col4),\n" +
                "    clustering key col5_col6_cls_index(col5,col6),\n" +
                "    primary key (col1, col3)\n" +
                ")\n" +
                "DISTRIBUTE BY HASH(col1) \n" +
                "PARTITION BY VALUE(col9) PARTITIONS 365 \n" +
                "INDEX_ALL='Y'\n" +
                "COMPRESSION = 'zip'\n" +
                "ENGINE='CSTORE'";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);
        String engine = SQLUtils.toNormalizeMysqlString(stmt.getOption("engine"));
        String indexAll = SQLUtils.toNormalizeMysqlString(stmt.getOption("index_All"));
        String compression = SQLUtils.toNormalizeMysqlString(stmt.getOption("COMPRESSION"));

        Assert.assertEquals("CSTORE", engine);
        Assert.assertEquals("Y", indexAll);
        Assert.assertEquals("zip", compression);

        parseTrue(sql, "CREATE TABLE db_name.table_name (\n" +
                "\tauto_id bigint NOT NULL AUTO_INCREMENT,\n" +
                "\tcol1 boolean NULL DEFAULT 1,\n" +
                "\tcol2 tinyint NULL,\n" +
                "\tcol3 smallint NULL DEFAULT 2,\n" +
                "\tcol4 int NOT NULL DEFAULT 3,\n" +
                "\tcol5 bigint,\n" +
                "\tcol6 float,\n" +
                "\tcol7 double,\n" +
                "\tcol8 varchar,\n" +
                "\tcol9 timestamp DEFAULT CURRENT_TIMESTAMP,\n" +
                "\tcol10 date DEFAULT CURRENT_DATE,\n" +
                "\tcol11 multivalue DEFAULT 'a' DELIMITER_TOKENIZER ': ,' VALUE_TYPE 'varchar int',\n" +
                "\tcol13 multivalue DEFAULT 'a' NLP_TOKENIZER 'ik' VALUE_TYPE 'varchar int',\n" +
                "\tcol14 geo2d DELIMITER_TOKENIZER ',',\n" +
                "\tKEY col3_index (col3),\n" +
                "\tKEY col4_index (col4),\n" +
                "\tCLUSTERING KEY col5_col6_cls_index (col5, col6),\n" +
                "\tPRIMARY KEY (col1, col3)\n" +
                ") INDEX_ALL = 'Y' COMPRESSION = 'zip' ENGINE = 'CSTORE'\n" +
                "DISTRIBUTE BY HASH(col1)\n" +
                "PARTITION BY VALUE (col9) PARTITIONS 365");
    }

    public void test_create_table2() {
        String sql = "CREATE TABLE db_name.table_name (\n" +
                "    auto_id bigint NOT NULL AUTO_INCREMENT,\n" +
                "    col1 boolean NULL DEFAULT 1,\n" +
                "    col2 tinyint null,\n" +
                "    col3 smallint NULL  DEFAULT 2,\n" +
                "    col4 int not null DEFAULT 3,\n" +
                "    col5 bigint  ,\n" +
                "    col6 float ,\n" +
                "    col7 double ,\n" +
                "    col8 varchar,\n" +
                "    col9 timestamp DEFAULT current_timestamp,\n" +
                "    col10 date DEFAULT current_date,\n" +
                "    col11 multivalue DEFAULT 'a' delimiter_tokenizer ': ,' value_type 'varchar int',\n" +
                "    col13 multivalue DEFAULT 'a' nlp_tokenizer 'ik' value_type 'varchar int',\n" +
                "    col14 geo2d   delimiter_tokenizer ',',\n" +
                "    key col3_index(col3),\n" +
                "    key col4_index(col4),\n" +
                "    clustering key col5_col6_cls_index(col5,col6),\n" +
                "    primary key (col1, col3)\n" +
                ")\n" +
                "DISTRIBUTE BY BROADCAST \n" +
                "PARTITION BY VALUE(YEARMONTHDAY(col9)) PARTITIONS 365 \n" +
                "INDEX_ALL='N'\n" +
                "ENGINE='CSTORE'";

        parseTrue(sql, "CREATE TABLE db_name.table_name (\n" +
                "\tauto_id bigint NOT NULL AUTO_INCREMENT,\n" +
                "\tcol1 boolean NULL DEFAULT 1,\n" +
                "\tcol2 tinyint NULL,\n" +
                "\tcol3 smallint NULL DEFAULT 2,\n" +
                "\tcol4 int NOT NULL DEFAULT 3,\n" +
                "\tcol5 bigint,\n" +
                "\tcol6 float,\n" +
                "\tcol7 double,\n" +
                "\tcol8 varchar,\n" +
                "\tcol9 timestamp DEFAULT CURRENT_TIMESTAMP,\n" +
                "\tcol10 date DEFAULT CURRENT_DATE,\n" +
                "\tcol11 multivalue DEFAULT 'a' DELIMITER_TOKENIZER ': ,' VALUE_TYPE 'varchar int',\n" +
                "\tcol13 multivalue DEFAULT 'a' NLP_TOKENIZER 'ik' VALUE_TYPE 'varchar int',\n" +
                "\tcol14 geo2d DELIMITER_TOKENIZER ',',\n" +
                "\tKEY col3_index (col3),\n" +
                "\tKEY col4_index (col4),\n" +
                "\tCLUSTERING KEY col5_col6_cls_index (col5, col6),\n" +
                "\tPRIMARY KEY (col1, col3)\n" +
                ") INDEX_ALL = 'N' ENGINE = 'CSTORE'\n" +
                "DISTRIBUTE BY BROADCAST \n" +
                "PARTITION BY VALUE (YEARMONTHDAY(col9)) PARTITIONS 365");
    }

    public void test_create_table3() {
        String sql = "CREATE TABLE test_rename_subpartition_key_columns (\n" +
                "  cid1 INT,\n" +
                "  cid2 INT,\n" +
                "  cid3 INT,\n" +
                "  cid4 INT,\n" +
                "  cid5 INT,\n" +
                "  `ts` VARCHAR,\n" +
                "  clustering KEY ts_cluster_idx(cid5, `TS`)\n" +
                ") DISTRIBUTE BY HASH (`cid1`)\n" +
                "PARTITION BY VALUE (`cid1`) LIFECYCLE -1\n" +
                "SUBPARTITION BY VALUE (`YEARMONTHDAY(ts)`)\n" +
                "  BLOCK_SIZE 16\n" +
                "ENGINE = 'CSTORE'";

        parseTrue(sql, "CREATE TABLE test_rename_subpartition_key_columns (\n" +
                "\tcid1 INT,\n" +
                "\tcid2 INT,\n" +
                "\tcid3 INT,\n" +
                "\tcid4 INT,\n" +
                "\tcid5 INT,\n" +
                "\t`ts` VARCHAR,\n" +
                "\tCLUSTERING KEY ts_cluster_idx (cid5, `TS`)\n" +
                ") BLOCK_SIZE = 16 ENGINE = 'CSTORE'\n" +
                "DISTRIBUTE BY HASH(`cid1`)\n" +
                "PARTITION BY VALUE (`cid1`) LIFECYCLE -1\n" +
                "SUBPARTITION BY VALUE (`YEARMONTHDAY(ts)`)");
    }
    public void test_create_table4() {
        String sql = "CREATE TABLE sec(col1 int, col2 string, primary key(col1, col2)) " +
                "DISTRIBUTE BY HASH(col1) " +
                "PARTITION BY VALUE(col1) " +
                "SUBPARTITION BY VALUE(YEARMONTHDAY(col2)) LIFECYCLE 30 " +
                "engine='CSTORE'; ";

        parseTrue(sql, "CREATE TABLE sec (\n" +
                "\tcol1 int,\n" +
                "\tcol2 string,\n" +
                "\tPRIMARY KEY (col1, col2)\n" +
                ") ENGINE = 'CSTORE'\n" +
                "DISTRIBUTE BY HASH(col1)\n" +
                "PARTITION BY VALUE (col1)\n" +
                "SUBPARTITION BY VALUE (YEARMONTHDAY(col2)) LIFECYCLE 30;");
    }
}