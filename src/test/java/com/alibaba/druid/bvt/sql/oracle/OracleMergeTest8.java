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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class OracleMergeTest8 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "merge into (select * from T09_RULE_CAL_COUNT where data_dt = '20160328' and rule_type = '2') t " //
                + "using ("
                + "     select cust_no,organ_key " //
                + "     from (select t1.cust_no, t1.organ_key from t08_cust_result_c_mid t1 "
                + "         union "
                + "         (select t2.cust_no, t2.organ_key from t08_cust_result_i_mid t2)"
                + "     )"
                + ") t3 on(t3.cust_no =t.cust_no) when matched then update set t.organ_key=t3.organ_key";

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLMergeStatement mergeStatement = (SQLMergeStatement) stmtList.get(0);
        String result = SQLUtils.toOracleString(mergeStatement);
        Assert.assertEquals("MERGE INTO (\n" +
                        "\tSELECT *\n" +
                        "\tFROM T09_RULE_CAL_COUNT\n" +
                        "\tWHERE data_dt = '20160328'\n" +
                        "\t\tAND rule_type = '2'\n" +
                        ") t\n" +
                        "USING (\n" +
                        "\tSELECT cust_no, organ_key\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT t1.cust_no, t1.organ_key\n" +
                        "\t\tFROM t08_cust_result_c_mid t1\n" +
                        "\t\tUNION\n" +
                        "\t\tSELECT t2.cust_no, t2.organ_key\n" +
                        "\t\tFROM t08_cust_result_i_mid t2\n" +
                        "\t)\n" +
                        ") t3 ON (t3.cust_no = t.cust_no) \n" +
                        "WHEN MATCHED THEN UPDATE SET t.organ_key = t3.organ_key",
                            result);
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
