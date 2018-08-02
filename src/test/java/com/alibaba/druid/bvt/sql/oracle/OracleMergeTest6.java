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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleMergeTest6 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO console_stb_ipstatus T1 " + //
                     "USING (SELECT '02222601005592002863423471' AS stbid  FROM dual) T2 " + //
                     "ON ( T1.stbid=T2.stbid) " + //
                     "WHEN MATCHED THEN " + //
                     "update set t1.ip='10.104.131.175',t1.port='6666',t1.status = 1, t1.time = to_char(sysdate, 'yyyy-MM-dd HH24:mi:ss')  " + //
                     "WHEN NOT MATCHED THEN  insert (id, stbid, ip, port, time, firsttime, status) " + //
                     "values (CONSOLE_SEQ.nextval,'02222601005592002863423471','10.104.131.175','6666',to_char(sysdate, 'yyyy-MM-dd HH24:mi:ss'),to_char(sysdate, 'yyyy-MM-dd HH24:mi:ss'),1) ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("console_stb_ipstatus")));

        Assert.assertEquals(7, visitor.getColumns().size());

         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "stbid"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "ip"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "port"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "status"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "time"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "id"));
         Assert.assertTrue(visitor.containsColumn("console_stb_ipstatus", "firsttime"));
    }

}
