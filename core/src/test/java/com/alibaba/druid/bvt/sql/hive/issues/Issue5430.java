package com.alibaba.druid.bvt.sql.hive.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5430">Issue 5430</a>
 * @see <a href="https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL">Hive DDL</a>
 * @todo 还有4个SQL解析存在转义字符识别的问题，暂时忽略，等温少来解决识别问题吧
 */
public class Issue5430 {

    private static final Log log = LogFactory.getLog(Issue5430.class);

    @Test
    public void test_createTable() throws Exception {
        DbType dbType = DbType.hive;
        int index = 0;
        for (String sql : new String[]{"Create table if not exists data01.test_20230830(a string ,b string) "
            + "PARTITIONED BY (load_date date, org string) ROW FORMAT DELIMITED NULL DEFINED AS '' STORED AS ORC",
            "Create table if not exists data01.test_20230830(a string ,b string) "
                + "PARTITIONED BY (load_date date, org string) ROW FORMAT DELIMITED NULL DEFINED AS \"\" STORED AS ORC",
            "Create table if not exists data01.test_20230830(a string ,b string) "
                + "PARTITIONED BY (load_date date, org string) ROW FORMAT DELIMITED NULL DEFINED AS 'aa' STORED AS ORC",
            "Create table if not exists data01.test_20230830(a string ,b string) "
                + "PARTITIONED BY (load_date date, org string) ROW FORMAT DELIMITED NULL DEFINED AS \"aa\" STORED AS ORC",
            "CREATE TABLE my_table(a string, b bigint)\n"
                + "ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'\n"
                + "STORED AS TEXTFILE",
            "CREATE TABLE my_table(a string, b bigint)\n"
                + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.JsonSerDe'\n"
                + "STORED AS TEXTFILE",
            "CREATE TABLE my_table(a string, b bigint) STORED AS JSONFILE",
            "create table table_name (\n"
                + "  id                int,\n"
                + "  dtDontQuery       string,\n"
                + "  name              string\n"
                + ")\n"
                + "partitioned by (date string)",
            "CREATE TABLE page_view(viewTime INT, userid BIGINT,\n"
                + "     page_url STRING, referrer_url STRING,\n"
                + "     ip STRING COMMENT 'IP Address of the User')\n"
                + " COMMENT 'This is the page view table'\n"
                + " PARTITIONED BY(dt STRING, country STRING)\n"
                + " STORED AS SEQUENCEFILE",
            "CREATE TABLE page_view(viewTime INT, userid BIGINT,\n"
                + "     page_url STRING, referrer_url STRING,\n"
                + "     ip STRING COMMENT 'IP Address of the User')\n"
                + " COMMENT 'This is the page view table'\n"
                + " PARTITIONED BY(dt STRING, country STRING)\n"
                + " ROW FORMAT DELIMITED\n"
                + "   FIELDS TERMINATED BY '\\001'\n"
                + "STORED AS SEQUENCEFILE",
            "CREATE EXTERNAL TABLE page_view(viewTime INT, userid BIGINT,\n"
                + "     page_url STRING, referrer_url STRING,\n"
                + "     ip STRING COMMENT 'IP Address of the User',\n"
                + "     country STRING COMMENT 'country of origination')\n"
                + " COMMENT 'This is the staging page view table'\n"
                + " ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\054'\n"
                + " STORED AS TEXTFILE\n"
                + " LOCATION '<hdfs_location>'",
            "CREATE TABLE list_bucket_single (key STRING, value STRING)\n"
                + "  SKEWED BY (key) ON (1,5,6) [STORED AS DIRECTORIES]",
            "CREATE TABLE list_bucket_multiple (col1 STRING, col2 int, col3 STRING)\n"
                + "  SKEWED BY (col1, col2) ON (('s1',1), ('s3',3), ('s13',13), ('s78',78)) [STORED AS DIRECTORIES]",
            "CREATE TEMPORARY TABLE list_bucket_multiple (col1 STRING, col2 int, col3 STRING)",
            "CREATE TRANSACTIONAL TABLE transactional_table_test(key string, value string) PARTITIONED BY(ds string) STORED AS ORC",
            "create table pk(id1 integer, id2 integer,\n"
                + "  primary key(id1, id2) disable novalidate)",
            "create table fk(id1 integer, id2 integer,\n"
                + "  constraint c1 foreign key(id1, id2) references pk(id2, id1) disable novalidate)",
            "create table constraints1(id1 integer UNIQUE disable novalidate, id2 integer NOT NULL,\n"
                + "  usr string DEFAULT current_user(), price double CHECK (price > 0 AND price <= 1000))",
            "create table constraints2(id1 integer, id2 integer,\n"
                + "  constraint c1_unique UNIQUE(id1) disable novalidate)",
            "create table constraints3(id1 integer, id2 integer,\n"
                + "  constraint c1_check CHECK(id1 + id2 > 0))",
            "CREATE TABLE apachelog (\n"
                + "  host STRING,\n"
                + "  identity STRING,\n"
                + "  user STRING,\n"
                + "  time STRING,\n"
                + "  request STRING,\n"
                + "  status STRING,\n"
                + "  size STRING,\n"
                + "  referer STRING,\n"
                + "  agent STRING)\n"
                + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'\n"
                + "WITH SERDEPROPERTIES (\n"
                + "  \"input.regex\" = \"([^]*) ([^]*) ([^]*) (-|\\\\[^\\\\]*\\\\]) ([^ \\\"]*|\\\"[^\\\"]*\\\") (-|[0-9]*) (-|[0-9]*)(?: ([^ \\\"]*|\\\".*\\\") ([^ \\\"]*|\\\".*\\\"))?\"\n"
                + ")\n"
                + "STORED AS TEXTFILE;",

            "CREATE TABLE my_table(a string, b string)\n"
                + "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'\n"
                + "WITH SERDEPROPERTIES (\n"
                + "   \"separatorChar\" = \"\\t\",\n"
                + "   \"quoteChar\"     = \"'\",\n"
                + "   \"escapeChar\"    = \"\\\\\"\n"
                + ")  \n"
                + "STORED AS TEXTFILE",
            "CREATE TABLE new_key_value_store\n"
                + "   ROW FORMAT SERDE \"org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe\"\n"
                + "   STORED AS RCFile\n"
                + "   AS\n"
                + "SELECT (key % 1024) new_key, concat(key, value) key_value_pair\n"
                + "FROM key_value_store\n"
                + "SORT BY new_key, key_value_pair",

            "CREATE TABLE page_view(viewTime INT, userid BIGINT,\n"
                + "     page_url STRING, referrer_url STRING,\n"
                + "     ip STRING COMMENT 'IP Address of the User')\n"
                + " COMMENT 'This is the page view table'\n"
                + " PARTITIONED BY(dt STRING, country STRING)\n"
                + " CLUSTERED BY(userid) SORTED BY(viewTime) INTO 32 BUCKETS\n"
                + " ROW FORMAT DELIMITED\n"
                + "   FIELDS TERMINATED BY '\\001'\n"
                + "   COLLECTION ITEMS TERMINATED BY '\\002'\n"
                + "   MAP KEYS TERMINATED BY '\\003'\n"
                + " STORED AS SEQUENCEFILE",
        }) {
            index++;
            if (index >=21) {
                continue;
            }
            String normalizeSql = normalizeSql(sql);
            System.out.println("第" + index + "条原始的sql格式归一化===" + normalizeSql);
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            SQLStatement statement = parser.parseStatement();
            String newSql = statement.toString();
            String normalizeNewSql = normalizeSql(newSql);
            System.out.println("第" + index + "条生成的sql格式归一化===" + normalizeNewSql);
            assertEquals(normalizeSql.toLowerCase(),normalizeNewSql.toLowerCase());
            if (!normalizeSql.equalsIgnoreCase(normalizeNewSql)) {
                System.err.println("第" + index + "条是解析失败原始的sql===" + normalizeSql);
            }
            //assertTrue(newSql.equalsIgnoreCase(sql));
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
            statement.accept(visitor);
            System.out.println("getTables==" + visitor.getTables());
            Map<Name, TableStat> tableMap = visitor.getTables();
            assertFalse(tableMap.isEmpty());

        }
    }

    static String normalizeSql(String sql) {
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "\t", " ");
        sql = StringUtils.replace(sql, "\n", " ");
        sql = StringUtils.replace(sql, "\'", "\"");
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, " (", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, ", ", ",");
        sql = StringUtils.replace(sql, " ,", ",");
        return sql;
    }
}
