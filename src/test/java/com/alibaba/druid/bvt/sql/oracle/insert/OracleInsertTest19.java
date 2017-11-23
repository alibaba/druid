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
package com.alibaba.druid.bvt.sql.oracle.insert;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleInsertTest19 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "insert into TB_DUOBAO_PARTICIPATE_NUMBER ( PARTICIPATE_NUMBER, PERIOD_ID, PRODUCT_ID,number_index)"
                + "         (SELECT ?,?,?,?  FROM DUAL)"
                + "       union all "
                + "           (SELECT ?,?,?,?  FROM DUAL)";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("INSERT INTO TB_DUOBAO_PARTICIPATE_NUMBER (PARTICIPATE_NUMBER, PERIOD_ID, PRODUCT_ID, number_index)"
                + "\nSELECT ?, ?, ?, ?"
                + "\nFROM DUAL"
                + "\nUNION ALL"
                + "\nSELECT ?, ?, ?, ?"
                + "\nFROM DUAL",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("TB_DUOBAO_PARTICIPATE_NUMBER")));

         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_DUOBAO_PARTICIPATE_NUMBER", "PARTICIPATE_NUMBER")));
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_DUOBAO_PARTICIPATE_NUMBER", "PERIOD_ID")));
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_DUOBAO_PARTICIPATE_NUMBER", "PRODUCT_ID")));
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_DUOBAO_PARTICIPATE_NUMBER", "number_index")));
    }

}
