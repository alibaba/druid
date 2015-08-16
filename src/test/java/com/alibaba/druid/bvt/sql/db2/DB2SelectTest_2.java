/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SelectTest_2 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT CTRYNUM, FMS_INSTANCE_CD FROM DBEFMSDR.FMSA_O_WW_CTRY_AG WHERE ACCT_YR=? WITH UR";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("DBEFMSDR.FMSA_O_WW_CTRY_AG")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("DBEFMSDR.FMSA_O_WW_CTRY_AG", "CTRYNUM")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.DB2);
        Assert.assertEquals("SELECT CTRYNUM, FMS_INSTANCE_CD" //
                            + "\nFROM DBEFMSDR.FMSA_O_WW_CTRY_AG"//
                            + "\nWHERE ACCT_YR = ?"//
                            + "\nWITH UR", //
                            output);
    }
}
