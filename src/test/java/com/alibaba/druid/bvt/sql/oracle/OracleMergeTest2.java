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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleMergeTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO \"ESCROW\".\"HT_TASK_TRADE_HISTORY_NEW\" SNA$ " + //
                     " USING (" + //
                     "      SELECT CURRENT$.\"ID\",CURRENT$.\"GMT_MODIFIED\"" + //
                     "          ,CURRENT$.\"GMT_CREATE\",CURRENT$.\"TRADE_ID\",CURRENT$.\"STATUS\",CURRENT$.\"OWNER\"" + //
                     "          ,CURRENT$.\"GMT_FETCH_TASK\",CURRENT$.\"GMT_FINISH_TASK\",CURRENT$.\"VERSION\"" + //
                     "          ,CURRENT$.\"RECORD_TYPE\",CURRENT$.\"TASK_FLOW_LEVEL\",CURRENT$.\"DEAL_TYPE\"" + //
                     "          ,CURRENT$.\"END_REASON\",CURRENT$.\"TRANSIT_TIME\" " + //
                     "      FROM (" + //
                     "          SELECT \"HT_TASK_TRADE_HISTORY\".\"ID\" \"ID\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"GMT_MODIFIED\" \"GMT_MODIFIED\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"GMT_CREATE\" \"GMT_CREATE\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"TRADE_ID\" \"TRADE_ID\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"STATUS\" \"STATUS\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"OWNER\" \"OWNER\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"GMT_FETCH_TASK\" \"GMT_FETCH_TASK\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"GMT_FINISH_TASK\" \"GMT_FINISH_TASK\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"VERSION\" \"VERSION\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"RECORD_TYPE\" \"RECORD_TYPE\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"TASK_FLOW_LEVEL\" \"TASK_FLOW_LEVEL\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"DEAL_TYPE\" \"DEAL_TYPE\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"END_REASON\" \"END_REASON\"" + //
                     "          ,\"HT_TASK_TRADE_HISTORY\".\"TRANSIT_TIME\" \"TRANSIT_TIME\" " + //
                     "          FROM \"ESCROW\".\"HT_TASK_TRADE_HISTORY\" \"HT_TASK_TRADE_HISTORY\"" + //
                     "          ) CURRENT$, " + //
                     "          (SELECT DISTINCT MLOG$.\"ID\" " + //
                     "          FROM \"ESCROW\".\"MLOG$_HT_TASK_TRADE_HISTOR\" MLOG$ " + //
                     "          WHERE \"SNAPTIME$$\" > :1 AND (\"DMLTYPE$$\" != 'D')" + //
                     "          ) LOG$ " + //
                     "      WHERE CURRENT$.\"ID\" = LOG$.\"ID\") AS OF SNAPSHOT(:SCN)  MAS$ ON (SNA$.\"ID\" = MAS$.\"ID\") WHEN MATCHED THEN UPDATE SET SNA$.\"ID\" = MAS$.\"ID\", SNA$.\"GMT_MODIFIED\" = MAS$.\"GMT_MODIFIED\", SNA$.\"GMT_CREATE\" = MAS$.\"GMT_CREATE\", SNA$.\"TRADE_ID\" = MAS$.\"TRADE_ID\", SNA$.\"STATUS\" = MAS$.\"STATUS\", SNA$.\"OWNER\" = MAS$.\"OWNER\", SNA$.\"GMT_FETCH_TASK\" = MAS$.\"GMT_FETCH_TASK\", SNA$.\"GMT_FINISH_TASK\" = MAS$.\"GMT_FINISH_TASK\", SNA$.\"VERSION\" = MAS$.\"VERSION\", SNA$.\"RECORD_TYPE\" = MAS$.\"RECORD_TYPE\", SNA$.\"TASK_FLOW_LEVEL\" = MAS$.\"TASK_FLOW_LEVEL\", SNA$.\"DEAL_TYPE\" = MAS$.\"DEAL_TYPE\", SNA$.\"END_REASON\" = MAS$.\"END_REASON\", SNA$.\"TRANSIT_TIME\" = MAS$.\"TRANSIT_TIME\" WHEN NOT MATCHED THEN INSERT (\"ID\",\"GMT_MODIFIED\",\"GMT_CREATE\",\"TRADE_ID\",\"STATUS\",\"OWNER\",\"GMT_FETCH_TASK\",\"GMT_FINISH_TASK\",\"VERSION\",\"RECORD_TYPE\",\"TASK_FLOW_LEVEL\",\"DEAL_TYPE\",\"END_REASON\",\"TRANSIT_TIME\") VALUES (MAS$.\"ID\",MAS$.\"GMT_MODIFIED\",MAS$.\"GMT_CREATE\",MAS$.\"TRADE_ID\",MAS$.\"STATUS\",MAS$.\"OWNER\",MAS$.\"GMT_FETCH_TASK\",MAS$.\"GMT_FINISH_TASK\",MAS$.\"VERSION\",MAS$.\"RECORD_TYPE\",MAS$.\"TASK_FLOW_LEVEL\",MAS$.\"DEAL_TYPE\",MAS$.\"END_REASON\",MAS$.\"TRANSIT_TIME\")";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.HT_TASK_TRADE_HISTORY")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.MLOG$_HT_TASK_TRADE_HISTOR")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ESCROW.HT_TASK_TRADE_HISTORY_NEW")));

        Assert.assertEquals(31, visitor.getColumns().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
