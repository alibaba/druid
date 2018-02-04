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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleDeleteTest_3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "delete from credit_corp_baseinfo o where o.applyid in(24032,23942,23579,23511,23408,23327,23322,23230,23228,23218);";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("credit_corp_baseinfo")));
        //
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("credit_corp_baseinfo", "applyid")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "commission_pct")));

        {
            String output = SQLUtils.toOracleString(stmt);
            Assert.assertEquals("DELETE FROM credit_corp_baseinfo o\n" +
                            "WHERE o.applyid IN (24032, 23942, 23579, 23511, 23408, 23327, 23322, 23230, 23228, 23218);", //
                    output);
        }
        {
            String output = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("delete from credit_corp_baseinfo o\n" +
                            "where o.applyid in (24032, 23942, 23579, 23511, 23408, 23327, 23322, 23230, 23228, 23218);", //
                    output);
        }
    }

}
