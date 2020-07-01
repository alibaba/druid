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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Condition;

import junit.framework.TestCase;

public class OracleSchemaStatVisitorTest5 extends TestCase {

    public void test_0() throws Exception {
        String sql =  "SELECT B.OBJ_ID AS BDZID,B.BDZMC,count(A.*) "
                + "FROM ("
                + "     SELECT BDZ "
                + "     FROM ("
                + "         SELECT JLZB.OBJ_ID,JLZB.BDZ, JLB.BHSBID, JLB.CLZ "
                + "         FROM SCYW.T_YJ_DWYJ_YXRZ_CDBHCLJL JLB "
                + "         INNER JOIN SCYW.T_YJ_DWYJ_YXRZ_YXJLZB JLZB ON JLB.YXJLID = JLZB.OBJ_ID) T "
                + "         WHERE T.OBJ_ID IN('SYS_B_02','SYS_B_03','SYS_B_04') "
                + "             AND 'SYS_B_05'='SYS_B_06'"
                + ") A, SCYW.T_SB_ZNYC_DZ B "
                + "WHERE A.BDZ=B.OBJ_ID "
                + "     AND B.OBJ_ID IN ("
                + "         SELECT DZ.OBJ_ID "
                + "         FROM T_SB_ZNYC_DZ DZ "
                + "         WHERE DZ.WHBZ = 'SYS_B_07' "
                + "             OR DZ.OBJ_ID IN ("
                + "                 SELECT ZFBZ.ZFID "
                + "                 FROM T_SB_ZNYC_ZFBZ ZFBZ "
                + "                 WHERE ZFBZ.BZID = 'SYS_B_08' AND ZFBZ.BZLB = 'SYS_B_09'"
                + "             ) "
                + "             AND DZ.FBZT = 'SYS_B_10'"
                + "         ) "
                + "GROUP BY B.OBJ_ID,B.BDZMC";

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
        
        for (Condition condition : visitor.getConditions()) {
            String table = condition.getColumn().getTable();
            Assert.assertTrue("table not exists : " + table, visitor.containsTable(table));
        }

        Assert.assertEquals(5, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("T_SB_ZNYC_ZFBZ"));

        Assert.assertEquals(13, visitor.getColumns().size());

    }

}
