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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest67 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT NVL(B.KYSLRQ, A.HZDJRQ) HZDJRQ FROM DJ_NSRXX A" +
                        ", （SELECT B.NSRDZDAH, MIN(B.KYSLRQ) KYSLRQ FROM DJ_PZJGXX B WHERE B.NSRDZDAH = :B1 GROUP BY B.NSRDZDAH) B WHERE A.NSRDZDAH = :B1 AND A.NSRDZDAH = B.NSRDZDAH(+)"; //

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
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(4, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT NVL(B.KYSLRQ, A.HZDJRQ) AS HZDJRQ\n" +
                    "FROM DJ_NSRXX A, (\n" +
                    "\tSELECT B.NSRDZDAH, MIN(B.KYSLRQ) AS KYSLRQ\n" +
                    "\tFROM DJ_PZJGXX B\n" +
                    "\tWHERE B.NSRDZDAH = :B1\n" +
                    "\tGROUP BY B.NSRDZDAH\n" +
                    ") B\n" +
                    "WHERE A.NSRDZDAH = :B1\n" +
                    "\tAND A.NSRDZDAH = B.NSRDZDAH(+)", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("select NVL(B.KYSLRQ, A.HZDJRQ) as HZDJRQ\n" +
                    "from DJ_NSRXX A, (\n" +
                    "\tselect B.NSRDZDAH, min(B.KYSLRQ) as KYSLRQ\n" +
                    "\tfrom DJ_PZJGXX B\n" +
                    "\twhere B.NSRDZDAH = :B1\n" +
                    "\tgroup by B.NSRDZDAH\n" +
                    ") B\n" +
                    "where A.NSRDZDAH = :B1\n" +
                    "\tand A.NSRDZDAH = B.NSRDZDAH(+)", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
