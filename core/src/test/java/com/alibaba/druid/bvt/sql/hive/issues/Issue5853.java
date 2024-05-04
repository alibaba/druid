package com.alibaba.druid.bvt.sql.hive.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Hive解析CREATE TABLE的问题
 *
 * @see <a href="https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL">...</a>
 */
public class Issue5853 {

    @Test
    public void test_parse_create_0() {
        for (String sql : new String[]{
            "CREATE TABLE page_view (\n"
                + "\tviewTime INT,\n"
                + "\tuserid BIGINT,\n"
                + "\tpage_url STRING,\n"
                + "\treferrer_url STRING,\n"
                + "\tip STRING COMMENT 'IP Address of the User'\n"
                + ")\n"
                + "COMMENT 'This is the page view table'\n"
                + "PARTITIONED BY (\n"
                + "\tdt STRING,\n"
                + "\tcountry STRING\n"
                + ")\n"
                + "CLUSTERED BY (userid)\n"
                + "SORTED BY (viewTime)\n"
                + "INTO 32 BUCKETS\n"
                + "ROW FORMAT DELIMITED\n"
                + "\tFIELDS TERMINATED BY '\\001'\n"
                + "\tCOLLECTION ITEMS TERMINATED BY '\\002'\n"
                + "\tMAP KEYS TERMINATED BY '\\003'\n"
                + "STORED AS SEQUENCEFILE;",
        }) {
            System.out.println("原始的sql===" + sql);
            SQLStatementParser parser1 = SQLParserUtils.createSQLStatementParser(sql, DbType.hive);
            List<SQLStatement> statementList1 = parser1.parseStatementList();
            String sqleNew = statementList1.get(0).toString();
            System.out.println("生成的sql===" + sqleNew);
            assertEquals(sql, sqleNew);
            SQLStatementParser parser2 = SQLParserUtils.createSQLStatementParser(sqleNew, DbType.hive);
            List<SQLStatement> statementList2 = parser2.parseStatementList();
            String sqleNew2 = statementList2.get(0).toString();
            System.out.println("再次解析生成的sql===" + sqleNew2);
            assertEquals(sqleNew, sqleNew2);
        }

    }

    @Test
    public void test_parse_create_1() {
        for (String sql : new String[]{
            "CREATE TABLE db.route(\n"
                + "od_id string COMMENT 'OD',\n"
                + "data_dt string COMMENT 'data date')\n"
                + "CLUSTERED BY (\n"
                + "od_id)\n"
                + "INTO 8 BUCKETS\n"
                + "ROW FORMAT SERDE\n"
                + "'org.apache.hadoop.hive.ql.io.orc.OrcSerde'\n"
                + "STORED AS INPUTFORMAT\n"
                + "'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'\n"
                + "OUTPUTFORMAT\n"
                + "'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat';",

        }) {
            System.out.println("原始的sql===" + sql);
            SQLStatementParser parser1 = SQLParserUtils.createSQLStatementParser(sql, DbType.hive);
            List<SQLStatement> statementList1 = parser1.parseStatementList();
            String sqleNew = statementList1.get(0).toString();
            System.out.println("生成的sql===" + sqleNew);
            SQLStatementParser parser2 = SQLParserUtils.createSQLStatementParser(sqleNew, DbType.hive);
            List<SQLStatement> statementList2 = parser2.parseStatementList();
            String sqleNew2 = statementList2.get(0).toString();
            System.out.println("再次解析生成的sql===" + sqleNew2);
            assertEquals(sqleNew, sqleNew2);
        }

    }

}
