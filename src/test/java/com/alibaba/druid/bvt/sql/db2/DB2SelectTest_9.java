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
package com.alibaba.druid.bvt.sql.db2;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SelectTest_9 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "select tsuserstb0_.USERID as USERID1_1_, tsuserstb0_.CREASTAF as CREASTAF2_1_"
                + " , tsuserstb0_.CREATIME as CREATIME3_1_, tsuserstb0_.LOCORGNO as LOCORGNO4_1_"
                + " , tsuserstb0_.PWDMODIFYTIME as PWDMODIF5_1_, tsuserstb0_.REMARK01 as REMARK6_1_"
                + " , tsuserstb0_.REMARK02 as REMARK7_1_, tsuserstb0_.STAORGNO as STAORGNO8_1_"
                + " , tsuserstb0_.UPDASTAF as UPDASTAF9_1_, tsuserstb0_.UPDATIME as UPDATIM10_1_, tsuserstb0_.USERNAME as USERNAM11_1_"
                + " , tsuserstb0_.USERPWD as USERPWD12_1_, tsuserstb0_.USERSTATE as USERSTA13_1_"
                + " from TS_USERSTB tsuserstb0_ fetch first 10 rows only";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(13, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("TS_USERSTB")));

        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "last_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("SELECT tsuserstb0_.USERID AS USERID1_1_, tsuserstb0_.CREASTAF AS CREASTAF2_1_, tsuserstb0_.CREATIME AS CREATIME3_1_, tsuserstb0_.LOCORGNO AS LOCORGNO4_1_, tsuserstb0_.PWDMODIFYTIME AS PWDMODIF5_1_"
                + "\n\t, tsuserstb0_.REMARK01 AS REMARK6_1_, tsuserstb0_.REMARK02 AS REMARK7_1_, tsuserstb0_.STAORGNO AS STAORGNO8_1_, tsuserstb0_.UPDASTAF AS UPDASTAF9_1_, tsuserstb0_.UPDATIME AS UPDATIM10_1_"
                + "\n\t, tsuserstb0_.USERNAME AS USERNAM11_1_, tsuserstb0_.USERPWD AS USERPWD12_1_, tsuserstb0_.USERSTATE AS USERSTA13_1_"
                + "\nFROM TS_USERSTB tsuserstb0_"
                + "\nFETCH FIRST 10 ROWS ONLY", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        Assert.assertEquals("select tsuserstb0_.USERID as USERID1_1_, tsuserstb0_.CREASTAF as CREASTAF2_1_, tsuserstb0_.CREATIME as CREATIME3_1_, tsuserstb0_.LOCORGNO as LOCORGNO4_1_, tsuserstb0_.PWDMODIFYTIME as PWDMODIF5_1_"
                + "\n\t, tsuserstb0_.REMARK01 as REMARK6_1_, tsuserstb0_.REMARK02 as REMARK7_1_, tsuserstb0_.STAORGNO as STAORGNO8_1_, tsuserstb0_.UPDASTAF as UPDASTAF9_1_, tsuserstb0_.UPDATIME as UPDATIM10_1_"
                + "\n\t, tsuserstb0_.USERNAME as USERNAM11_1_, tsuserstb0_.USERPWD as USERPWD12_1_, tsuserstb0_.USERSTATE as USERSTA13_1_"
                + "\nfrom TS_USERSTB tsuserstb0_"
                + "\nfetch first 10 rows only", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
