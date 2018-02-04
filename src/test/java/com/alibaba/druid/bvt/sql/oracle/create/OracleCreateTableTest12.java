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
package com.alibaba.druid.bvt.sql.oracle.create;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleCreateTableTest12 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "create table CARD_MONEYOPERATION (" + //
                "ID varchar2(50 char) not null, " + //
                "DDBH varchar2(50 char) not null, " + //
                "IDNO varchar2(18 char), " + //
                "JKKH varchar2(50 char) not null, " + //
                "JYDW varchar2(50 char), " + //
                "JYJE number(19,2) not null, " + //
                "JYNO varchar2(50 char), " + //
                "JYZT number(19,0) not null, " + //
                "NAME varchar2(10 char), " + //
                "OPDATE timestamp, " + //
                "REMARK varchar2(50 char), " + //
                "TYPE varchar2(20 char) not null, " + //
                "OPID varchar2(50 char), " + //
                "primary key (ID)" + //
                ") ";

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

        Assert.assertEquals(13, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "ID")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "DDBH")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "IDNO")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "JKKH")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "JYDW")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "JYJE")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "JYNO")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "JYZT")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "OPDATE")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "REMARK")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "TYPE")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("CARD_MONEYOPERATION", "OPID")));
    }
}
