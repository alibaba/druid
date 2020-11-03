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
package com.alibaba.druid.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;

public class CommentTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT /*mark for picman*/ * FROM WP_ALBUM WHERE MEMBER_ID = ? AND ID IN (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Lexer lexer = new Lexer(sql);
        for (;;) {
            lexer.nextToken();
            Token tok = lexer.token();

            if (tok == Token.IDENTIFIER) {
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
            } else if (tok == Token.MULTI_LINE_COMMENT) {
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
            } else {
                System.out.println(tok.name() + "\t\t\t" + tok.name);
            }

            if (tok == Token.EOF) {
                break;
            }
        }
    }

    public void test_1() throws Exception {
        String sql = "SELECT /*mark for picman*/ * FROM WP_ALBUM WHERE MEMBER_ID = ? AND ID IN (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.print(";");
            visitor.println();
        }

        System.out.println(out.toString());
    }

    public void test_2() throws Exception {
        String sql = "//hello world\n";
        Lexer lexer = new Lexer(sql);
        lexer.nextToken();
        assertEquals("hello world", lexer.stringVal());

        sql = "/*hello \nworld*/";
        lexer = new Lexer(sql);
        lexer.nextToken();
        assertEquals("hello \nworld", lexer.stringVal());

        sql = "--hello world\n";
        lexer = new Lexer(sql);
        lexer.nextToken();
        assertEquals("hello world", lexer.stringVal());
    }


    public void test_3() throws Exception {
        String sql = "CREATE EXTERNAL TABLE `dwd.dwd_zyscm_goodsattr`( `goodsid` string COMMENT '商品id', `generalname` string COMMENT '通用名') COMMENT '时空商品属性表'  ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe' STORED AS  'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' LOCATION 'hdfs://yx-dc-3-21:8020/user/hive/warehouse/dwd.db/dwd_zyscm_goodsattr' TBLPROPERTIES ( 'k_create_date'='2020-03-19', 'k_creator'='chaixiaoxue', 'parquet.compression'='snappy', 'transient_lastDdlTime'='1586937486')";
        String b="create external table `dwd.dwd_order_goods_detail_df` ( `store_id` string comment '门店 ID. 来自机构表的门店 ID' ) COMMENT '时空商品属性表' row format serde 'org.apache.hadoop.hive.ql.io.parquet.serde.parquethiveserde' stored as inputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' outputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat' location 'hdfs://dc/user/hive/warehouse/dwd.db/dwd_order_goods_detail_df' tblproperties ( last_modified_by = 'hive' last_modified_time = '1597829383' parquet.compression = 'snappy' transient_lastDdlTime = '1597829383' yx_create_date = '2020-04-24' yx_creator = 'wangyangting' )";

        String c= "select store_id,store_name from prf.prf_order_goods_detail_df where dt='2020-09-10' order by store_id limit 1 offset 3; drop table prf.prf_order_goods_detail_df; drop database prf; alter table prf.prf_order_goods_detail_df drop partition(dt='2020-09-10')";
        /*String format = SQLUtils.format(sql
                , JdbcConstants.HIVE
                , SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);*/
        String sql2= "create external table `dwd.dwd_yxhb_lpfw_case_settle_claim_df` ( `c_case_id` string comment '案件ID', `c_depart_code` string comment '机构编码', `yd_drug_name` string comment '药品名称' ) comment '圆心惠保/案件/理赔核算/理赔核算明细的集成表' partitioned by ( `dt` string comment '格式: yyyy-MM-dd' ) row format serde 'org.apache.hadoop.hive.ql.io.parquet.serde.parquethiveserde' stored as inputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' outputformat 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat' location 'hdfs://dc/user/hive/warehouse/dwd.db/dwd_yxhb_lpfw_case_settle_claim_df' tblproperties ( parquet.compression.test.tsest2 = 'snappy' , transient_lastDdlTime = '1603421769'  )";
        List<SQLStatement> sqlStatements = SQLUtils.toStatementList(sql2, JdbcConstants.HIVE);

        System.out.println(sqlStatements);
    }
}
