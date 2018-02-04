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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleSchemaStatVisitorTest6 extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT 'SYS_B_0',COUNT('$SYS_B_1') AS DEVICECOUNT "
                + "FROM ("
                + "     SELECT OID,SSJG,OID "
                + "     FROM ("
                + "         SELECT A.OID,A.SSJG,A.OID "
                + "         FROM T_TX_ZLSB_ZLDYHGQ A " //
                + "         WHERE (A.VERSIONID IS NULL OR A.VERSIONID='$SYS_B_2')"
                + "     ) T1 JOIN ("
                + "         SELECT B.OID,B.SSJG,B.OID "
                + "         FROM T_TX_ZLSB_ZLDYHGQ_VER B WHERE B.ADDFLAG<'SYS_B_3'"
                + "     ) T2 ON T1.OID=T2.OID"
                + ") WHERE SSJG='$SYS_B_4'";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println(sql);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("relationShip : " + visitor.getRelationships());
        System.out.println("where : " + visitor.getConditions());
        System.out.println("groupBy : " + visitor.getGroupByColumns());

//        Assert.assertEquals(2, visitor.getTables().size());
//        Assert.assertEquals(true, visitor.containsTable("users"));
//
//        Assert.assertEquals(2, visitor.getColumns().size());
//        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "id")));
//        Assert.assertEquals(true, visitor.getColumns().contains(new Column("users", "name")));

    }

}
