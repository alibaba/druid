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
package com.alibaba.druid.bvt.sql.oracle.select;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest22 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select /*+ no_parallel(t) no_parallel_index(t) dbms_stats cursor_sharing_exact use_weak_name_resl dynamic_sampling(0) no_monitoring */ count(*),sum(sys_op_opnsize(\"ID\")),substrb(dump(min(\"ID\"),16,0,32),1,120),substrb(dump(max(\"ID\"),16,0,32),1,120),count(distinct \"GMT_MODIFIED\"),substrb(dump(min(\"GMT_MODIFIED\"),16,0,32),1,120),substrb(dump(max(\"GMT_MODIFIED\"),16,0,32),1,120),count(distinct \"GMT_CREATE\"),substrb(dump(min(\"GMT_CREATE\"),16,0,32),1,120),substrb(dump(max(\"GMT_CREATE\"),16,0,32),1,120),count(\"TRADE_ID\"),count(distinct \"TRADE_ID\"),sum(sys_op_opnsize(\"TRADE_ID\")),substrb(dump(min(\"TRADE_ID\"),16,0,32),1,120),substrb(dump(max(\"TRADE_ID\"),16,0,32),1,120),count(\"STATUS\"),count(distinct \"STATUS\"),sum(sys_op_opnsize(\"STATUS\")),substrb(dump(min(substrb(\"STATUS\",1,32)),16,0,32),1,120),substrb(dump(max(substrb(\"STATUS\",1,32)),16,0,32),1,120),count(\"OWNER\"),count(distinct \"OWNER\"),sum(sys_op_opnsize(\"OWNER\")),substrb(dump(min(substrb(\"OWNER\",1,32)),16,0,32),1,120),substrb(dump(max(substrb(\"OWNER\",1,32)),16,0,32),1,120),count(\"GMT_FETCH_TASK\"),count(distinct \"GMT_FETCH_TASK\"),substrb(dump(min(\"GMT_FETCH_TASK\"),16,0,32),1,120),substrb(dump(max(\"GMT_FETCH_TASK\"),16,0,32),1,120),count(\"GMT_FINISH_TASK\"),count(distinct \"GMT_FINISH_TASK\"),substrb(dump(min(\"GMT_FINISH_TASK\"),16,0,32),1,120),substrb(dump(max(\"GMT_FINISH_TASK\"),16,0,32),1,120),count(\"VERSION\"),count(distinct \"VERSION\"),sum(sys_op_opnsize(\"VERSION\")),substrb(dump(min(\"VERSION\"),16,0,32),1,120),substrb(dump(max(\"VERSION\"),16,0,32),1,120),count(\"RECORD_TYPE\"),count(distinct \"RECORD_TYPE\"),sum(sys_op_opnsize(\"RECORD_TYPE\")),substrb(dump(min(substrb(\"RECORD_TYPE\",1,32)),16,0,32),1,120),substrb(dump(max(substrb(\"RECORD_TYPE\",1,32)),16,0,32),1,120),count(\"TASK_FLOW_LEVEL\"),count(distinct \"TASK_FLOW_LEVEL\"),sum(sys_op_opnsize(\"TASK_FLOW_LEVEL\")),substrb(dump(min(\"TASK_FLOW_LEVEL\"),16,0,32),1,120),substrb(dump(max(\"TASK_FLOW_LEVEL\"),16,0,32),1,120),count(\"DEAL_TYPE\"),count(distinct \"DEAL_TYPE\"),sum(sys_op_opnsize(\"DEAL_TYPE\")),substrb(dump(min(\"DEAL_TYPE\"),16,0,32),1,120),substrb(dump(max(\"DEAL_TYPE\"),16,0,32),1,120),count(\"END_REASON\"),count(distinct \"END_REASON\"),sum(sys_op_opnsize(\"END_REASON\")),substrb(dump(min(\"END_REASON\"),16,0,32),1,120),substrb(dump(max(\"END_REASON\"),16,0,32),1,120),count(\"TRANSIT_TIME\"),count(distinct \"TRANSIT_TIME\"),sum(sys_op_opnsize(\"TRANSIT_TIME\")),substrb(dump(min(\"TRANSIT_TIME\"),16,0,32),1,120),substrb(dump(max(\"TRANSIT_TIME\"),16,0,32),1,120) from \"ESCROW\".\"HT_TASK_TRADE_HISTORY\" sample (   .5000000000) t "; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.HT_TASK_TRADE_HISTORY")));

        Assert.assertEquals(15, visitor.getColumns().size());

         Assert.assertTrue(visitor.containsColumn("ESCROW.HT_TASK_TRADE_HISTORY", "*"));
    }
}
