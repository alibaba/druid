/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle.alter;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleAlterTableTest26 extends OracleTest {
    public void test_0() throws Exception {
        String sql = "alter table mytable enable row movement";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE mytable\n\t ENABLE ROW MOVEMENT ",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("mytable")));
    }

    public void test_1() throws Exception {
        String sql = "alter table \"EIFINI_BCS\".\"SHEET_DA\" shrink space CHECK";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"EIFINI_BCS\".\"SHEET_DA\"\n" +
                        "\t SHRINK SPACE CHECK ",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("EIFINI_BCS.SHEET_DA")));
    }

    public void test_2() throws Exception {
        String sql = "alter table \"EIFINI_BCS\".\"SHEET_DA\" shrink space compact";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"EIFINI_BCS\".\"SHEET_DA\"\n" +
                        "\t SHRINK SPACE COMPACT ",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("EIFINI_BCS.SHEET_DA")));
    }

    public void test_3() throws Exception {
        String sql = "alter table \"EIFINI_BCS\".\"SHEET_DA\" shrink space CASCADE";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"EIFINI_BCS\".\"SHEET_DA\"\n" +
                        "\t SHRINK SPACE CASCADE ",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("EIFINI_BCS.SHEET_DA")));
    }

    public void test_4() throws Exception {
        String sql = "alter table \"EIFINI_BCS\".\"SHEET_DA\" shrink space COMPACT CASCADE";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"EIFINI_BCS\".\"SHEET_DA\"\n" +
                        "\t SHRINK SPACE COMPACT CASCADE ",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("EIFINI_BCS.SHEET_DA")));
    }

    public void test_5() throws Exception {
        String sql = "ALTER TABLE tester.t1 RENAME TO tester.\"BIN$9SBcFDaAYgDgUykdEKzT1Q==$0\"";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("RENAME tester.t1 TO tester.\"BIN$9SBcFDaAYgDgUykdEKzT1Q==$0\"",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("tester.t1")));
    }

    public void test_6() throws Exception {
        String sql = "ALTER TABLE customers MODIFY city varchar2(75) DEFAULT 'Seattle' NOT NULL;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE customers\n\tMODIFY (\n\t\tcity varchar2(75) DEFAULT 'Seattle' NOT NULL\n\t);",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("customers")));
    }


    public void test_7() throws Exception {
        String sql = "create table \"JUNYU_ORCL\".\"KBS_QUESTION\"(\n" +
                "  \"ID\"  number(19, 0)  not null,\n" +
                "  \"QUESTION\"  varchar2(1500)  null,\n" +
                "  \"GRADE\"  number(3, 0) default 0 not null,\n" +
                "  \"CATEGORY\"  varchar2(1500)  null,\n" +
                "  \"CATEGORY_ID\"  number(19, 0)  null,\n" +
                "  \"PRIMARY_KEYWORD\"  varchar2(180)  null,\n" +
                "  \"ALTERNATE_KEYWORD\"  varchar2(180)  null,\n" +
                "  \"SEARCH_KEYWORD\"  varchar2(1500)  null,\n" +
                "  \"KEYWORD_OPTION\"  number(3, 0) default 0 not null,\n" +
                "  \"SYNONYMS\"  varchar2(1500)  null,\n" +
                "  \"ANSWER_SHAPE\"  number(3, 0) default 0 not null,\n" +
                "  \"PERSPECTIVE_ID\"  varchar2(900) default '500' null,\n" +
                "  \"ANSWER\"  varchar2(4000)  null,\n" +
                "  \"ANSWER_MD5\"  varchar2(96)  null,\n" +
                "  \"VOICE_ANSWER\"  varchar2(4000)  null,\n" +
                "  \"SHOW_ANSWER\"  varchar2(4000)  null,\n" +
                "  \"REMARK\"  varchar2(3000)  null,\n" +
                "  \"STATUS\"  number(3, 0) default 0 not null,\n" +
                "  \"VALID_DATE\"  timestamp(0)  null,\n" +
                "  \"INVALID_DATE\"  timestamp(0)  null,\n" +
                "  \"INVALID_REASON\"  varchar2(300)  null,\n" +
                "  \"ADD_MODE\"  number(3, 0) default 0 not null,\n" +
                "  \"ORIGINAL\"  varchar2(192)  null,\n" +
                "  \"APPROVAL_STATUS\"  number(3, 0) default 1 not null,\n" +
                "  \"MAP_SOURCE\"  varchar2(192)  null,\n" +
                "  \"MAP_ID\"  varchar2(192)  null,\n" +
                "  \"VOICE_URL\"  varchar2(765)  null,\n" +
                "  \"SYNC_FLAG\"  number(10, 0) default 0 not null,\n" +
                "  \"SYNC_TIME\"  timestamp(0)  null,\n" +
                "  \"BIZ_CATEGORY_ID\"  number(19, 0)  null,\n" +
                "  \"FILE_CODE\"  varchar2(1536)  null,\n" +
                "  \"REFERENCE\"  varchar2(3072)  null,\n" +
                "  \"SUGGESTION\"  varchar2(1536)  null,\n" +
                "  \"BRIGHTNESS\"  number(3, 0) default 0 not null,\n" +
                "  \"OPEN_FLAG\"  number(3, 0) default 1 not null,\n" +
                "  \"RELATION_STATUS\"  number(3, 0) default 0 not null,\n" +
                "  \"CREATOR\"  varchar2(150)  null,\n" +
                "  \"CREATION_DATE\"  timestamp(0) default sysdate not null,\n" +
                "  \"MODIFIER\"  varchar2(150)  null,\n" +
                "  \"MODIFICATION_DATE\"  timestamp(0) default sysdate not null,\n" +
                "  \"QUESTION_TYPE\"  varchar2(30)  null,\n" +
                "primary key(\"ID\"))";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"JUNYU_ORCL\".\"KBS_QUESTION\" (\n" +
                        "\t\"ID\" number(19, 0) NOT NULL,\n" +
                        "\t\"QUESTION\" varchar2(1500) NULL,\n" +
                        "\t\"GRADE\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"CATEGORY\" varchar2(1500) NULL,\n" +
                        "\t\"CATEGORY_ID\" number(19, 0) NULL,\n" +
                        "\t\"PRIMARY_KEYWORD\" varchar2(180) NULL,\n" +
                        "\t\"ALTERNATE_KEYWORD\" varchar2(180) NULL,\n" +
                        "\t\"SEARCH_KEYWORD\" varchar2(1500) NULL,\n" +
                        "\t\"KEYWORD_OPTION\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"SYNONYMS\" varchar2(1500) NULL,\n" +
                        "\t\"ANSWER_SHAPE\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"PERSPECTIVE_ID\" varchar2(900) DEFAULT '500' NULL,\n" +
                        "\t\"ANSWER\" varchar2(4000) NULL,\n" +
                        "\t\"ANSWER_MD5\" varchar2(96) NULL,\n" +
                        "\t\"VOICE_ANSWER\" varchar2(4000) NULL,\n" +
                        "\t\"SHOW_ANSWER\" varchar2(4000) NULL,\n" +
                        "\t\"REMARK\" varchar2(3000) NULL,\n" +
                        "\t\"STATUS\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"VALID_DATE\" timestamp(0) NULL,\n" +
                        "\t\"INVALID_DATE\" timestamp(0) NULL,\n" +
                        "\t\"INVALID_REASON\" varchar2(300) NULL,\n" +
                        "\t\"ADD_MODE\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"ORIGINAL\" varchar2(192) NULL,\n" +
                        "\t\"APPROVAL_STATUS\" number(3, 0) DEFAULT 1 NOT NULL,\n" +
                        "\t\"MAP_SOURCE\" varchar2(192) NULL,\n" +
                        "\t\"MAP_ID\" varchar2(192) NULL,\n" +
                        "\t\"VOICE_URL\" varchar2(765) NULL,\n" +
                        "\t\"SYNC_FLAG\" number(10, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"SYNC_TIME\" timestamp(0) NULL,\n" +
                        "\t\"BIZ_CATEGORY_ID\" number(19, 0) NULL,\n" +
                        "\t\"FILE_CODE\" varchar2(1536) NULL,\n" +
                        "\t\"REFERENCE\" varchar2(3072) NULL,\n" +
                        "\t\"SUGGESTION\" varchar2(1536) NULL,\n" +
                        "\t\"BRIGHTNESS\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"OPEN_FLAG\" number(3, 0) DEFAULT 1 NOT NULL,\n" +
                        "\t\"RELATION_STATUS\" number(3, 0) DEFAULT 0 NOT NULL,\n" +
                        "\t\"CREATOR\" varchar2(150) NULL,\n" +
                        "\t\"CREATION_DATE\" timestamp(0) DEFAULT SYSDATE NOT NULL,\n" +
                        "\t\"MODIFIER\" varchar2(150) NULL,\n" +
                        "\t\"MODIFICATION_DATE\" timestamp(0) DEFAULT SYSDATE NOT NULL,\n" +
                        "\t\"QUESTION_TYPE\" varchar2(30) NULL,\n" +
                        "\tPRIMARY KEY (\"ID\")\n" +
                        ")",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("JUNYU_ORCL.KBS_QUESTION")));
    }

    public void test_8() throws Exception {
        String sql = "ALTER TABLE \"JUNYU_ORCL\".\"MYSQL_ALL_TYPES_FOR_8_0\" ADD CONSTRAINT \"uk_c_serial_7049_f_759141250\" UNIQUE (\"C_SERIAL\")";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"JUNYU_ORCL\".\"MYSQL_ALL_TYPES_FOR_8_0\"\n" +
                        "\tADD CONSTRAINT \"uk_c_serial_7049_f_759141250\" UNIQUE (\"C_SERIAL\")",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("JUNYU_ORCL.MYSQL_ALL_TYPES_FOR_8_0")));
    }

    public void test_9() throws Exception {
        String sql = "ALTER TABLE \"JUNYU_ORCL\".\"KBS_QUESTION\" RENAME CONSTRAINT \"SYS_C007896\" TO \"BIN$9Ue7xItcBs3gUyUdEKzZKg==$0\"";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"JUNYU_ORCL\".\"KBS_QUESTION\"\n" +
                        "\tRENAME CONSTRAINT \"SYS_C007896\" TO \"BIN$9Ue7xItcBs3gUyUdEKzZKg==$0\"",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("JUNYU_ORCL.KBS_QUESTION")));
    }

    public void test_10() throws Exception {
        String sql = "drop table KBS_QUESTION AS \"BIN$9Ue7xItyBs3gUyUdEKzZKg==$0\"\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("DROP TABLE KBS_QUESTION  AS \"BIN$9Ue7xItyBs3gUyUdEKzZKg==$0\"",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("KBS_QUESTION")));
    }

    public void test_11() throws Exception {
        String sql = "analyze table JUNYU_ORCL.EQ_DRAGONCARD_TEMP compute statistics";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ANALYZE TABLE JUNYU_ORCL.EQ_DRAGONCARD_TEMP COMPUTE STATISTICS",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("JUNYU_ORCL.EQ_DRAGONCARD_TEMP")));
    }

    public void test_12() throws Exception {
        String sql = "alter table \"JUNYU_ORCL\".\"WORKER_STATS\" drop constraint \"SYS_C007550\" cascade;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER TABLE \"JUNYU_ORCL\".\"WORKER_STATS\"\n" +
                        "\tDROP CONSTRAINT \"SYS_C007550\" CASCADE;",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("JUNYU_ORCL.WORKER_STATS")));
    }

    public void test_13() throws Exception {
        String sql = " /* QSMQ VALIDATION */ ALTER SUMMARY \"CHJMESPRO\".\"MV_PRODUCTION_OVERVIEW_HOUR\" COMPILE;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("ALTER SUMMARY \"CHJMESPRO\".\"MV_PRODUCTION_OVERVIEW_HOUR\" COMPILE ;",
                SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));
    }
}
