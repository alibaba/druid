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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Assert;

import java.util.List;

public class OracleMergeTest9 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO tb_xxx_test t1\n" +
                "USING (SELECT '1' AS aa_item_id, '2' AS mem_test_id FROM dual) t2\n" +
                "ON (t1.aa_item_id = t2.aa_item_id AND t1.mem_test_id = t2.mem_test_id)\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT (aa_id, aa_item_id, gg_id, test_id)\n" +
                "VALUES\n" +
                "(?,\n" +
                "?,\n" +
                "?,\n" +
                "?)";

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLMergeStatement mergeStatement = (SQLMergeStatement) stmtList.get(0);
        String result = SQLUtils.toOracleString(mergeStatement);
        Assert.assertEquals("MERGE INTO tb_xxx_test t1\n" +
                        "USING (\n" +
                        "\tSELECT '1' AS aa_item_id, '2' AS mem_test_id\n" +
                        "\tFROM dual\n" +
                        ") t2 ON (t1.aa_item_id = t2.aa_item_id\n" +
                        "AND t1.mem_test_id = t2.mem_test_id) \n" +
                        "WHEN NOT MATCHED THEN INSERT (aa_id, aa_item_id, gg_id, test_id) VALUES (?, ?, ?, ?)",
                            result);
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
