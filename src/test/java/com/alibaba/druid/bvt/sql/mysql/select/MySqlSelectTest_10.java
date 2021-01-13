/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_10 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM t_department  WHERE name IN ('0000','4444') ORDER BY name ASC";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        
        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        Assert.assertNotNull(queryBlock.getOrderBy());
        
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(1, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_department")));
    }

//    public void test_1() throws Exception {
//        String sql = "select t1.sid,t2.id from (select sid from test4dmp.grade where sid >10 )t1 "
//                           + "join (select id from test4dmp.test where int_test >10 ) t2 on t1.sid=t2.id order by t1.sid nulls first,t2.id nulls first";
//
//        MySqlStatementParser parser = new MySqlStatementParser(sql);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        SQLStatement stmt = statementList.get(0);
//
//        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
//
//        SQLSelect select = selectStmt.getSelect();
//        Assert.assertNotNull(select.getQuery());
//        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
//        Assert.assertNotNull(queryBlock.getOrderBy());
//
//        print(statementList);
//    }
//    public void test_2() throws Exception {
//        String sql = "select t1.sid,t2.id from  (select * from table1) t1 group by id  having count(id) > 11.2";
//
//        MySqlStatementParser parser = new MySqlStatementParser(sql);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        SQLStatement stmt = statementList.get(0);
//
//        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
//
//        SQLSelect select = selectStmt.getSelect();
//        Assert.assertNotNull(select.getQuery());
//        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
//        Assert.assertNotNull(queryBlock.getOrderBy());
//
//        print(statementList);
//    }
//    public void test_3() throws Exception {
//        String sql = "select fun(t1.sid), t1.id from  t1 union select t2.sid, t2.id from  t2 ";
//
//        MySqlStatementParser parser = new MySqlStatementParser(sql);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        SQLStatement stmt = statementList.get(0);
//
//        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
//
//        SQLSelect select = selectStmt.getSelect();
//        Assert.assertNotNull(select.getQuery());
//        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
//        Assert.assertNotNull(queryBlock.getOrderBy());
//
//        print(statementList);
//    }
//
//    public static void updateRepository(List<String> ddlList) {
//        SchemaRepository repository = new SchemaRepository(DbType.mysql);
//        SchemaRepository temp = new SchemaRepository(DbType.mysql);
//        for (String ddl : ddlList) {
//            temp.acceptDDL(ddl);
//        }
//        repository = temp;
//    }
//
//    public static void main(String[] args) {
//        updateRepository(Arrays.asList("CREATE TABLE towphasetest_1.ddl_create (\n" + "  id bigint,\n"
//                                       + "  date1 date,\n" + "  age int,\n" + "  desc1 varchar,\n" + "  status int\n"
//                                       + ")"
//                , "CREATE TABLE towphasetest_1.etao_pic (\n" + "  rowkey varchar,\n" + "  ecnn varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.odps_test_seahawks_10_10573 (\n" + "  id bigint,\n" + "  src varchar,\n"
//                  + "  src_project_name varchar,\n" + "  src_table_name varchar,\n"
//                  + "  src_owner_workno varchar,src_bu_id varchar,\n" + "  src_bu_name varchar,\n"
//                  + "  dest varchar,\n" + "  dest_project_name varchar,\n" + "  dest_table_name varchar,\n"
//                  + "  dest_owner_workno varchar,\n" + "  dest_bu_id varchar,\n"
//                  + "  dest_bu_name varchar, asset_level varchar,\n" + "  path varchar,\n" + "  level varchar\n"
//                  + ")"
//                , "CREATE TABLE towphasetest_1.perf_detail (\n" + "  tid varchar,\n" + "  testcase varchar,\n"
//                  + "  exe_time timestamp, response_time int,\n" + "  success int,\n" + "  failed int,\n"
//                  + "  error_info varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.perf_summary (\n" + "  tid varchar,\n" + "  testcase varchar,\n"
//                  + "  rt_min int,\n" + "  rt_max int,\n" + "  rt_avg int,\n" + "  success_cnt int,\n"
//                  + "  failed_cnt int,\n" + "  exe_time timestamp\n" + ")"
//                , "CREATE TABLE towphasetest_1.perf_test (\n" + "  tid varchar, tname varchar,\n"
//                  + "  description varchar,\n" + "  commit1 varchar,\n" + "  threadnum int,\n" + "  sleeptime int,\n"
//                  + "  threadstartdelay int,\n" + "  warmloops int,\n" + "  testloops int, env varchar,\n"
//                  + "  executiontime timestamp,\n" + "  branch varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.table1 (\n" + "  col1 bigint,\n" + "  col2 varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.table2 (\n" + "  col1 bigint,\n" + "  col2 varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.table3 (\n" + "  col1 bigint\n" + ")"
//                , "CREATE TABLE towphasetest_1.table4 (\n" + "  col1 bigint,\n" + "  col2 varchar\n" + ")"
//                , "CREATE TABLE towphasetest_1.vector (\n" + "  id varchar,\n" + "  feature array\n" + ")"
//                , "CREATE TABLE towphasetest_1.vector_feature_centers (\n" + "  id int,\n" + "  center array,\n"
//                  + "  weight bigint,\n" + "  version bigint\n" + ")"
//                , "CREATE TABLE towphasetest_1.vector_feature_index (\n" + "  id varchar,\n" + "  center_idx int,\n"
//                  + "  seg_center_idx array,\n" + "  version bigint\n" + ")"
//                , "CREATE TABLE towphasetest_1.vector_feature_pcenters (\n" + "  id smallint,\n" + "  center array,\n"
//                  + "  version bigint\n" + ")"
//        ));
//    }


}
