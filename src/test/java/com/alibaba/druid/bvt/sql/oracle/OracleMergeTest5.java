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

public class OracleMergeTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE " + //
                     "INTO MEMBER_LAST_LOGIN M2 " + //
                     "USING MEMBER_LAST_LOGIN_HZ M1 ON (M1.ID = M2.ID) " + //
                     "  WHEN MATCHED THEN " + //
                     "      UPDATE SET M2.LAST_LOGIN_TIME = M1.LAST_LOGIN_TIME, M2.GMT_MODIFIED = M1.GMT_MODIFIED" + //
                     "        , M2.OWNER_SEQ = M1.OWNER_SEQ, M2.OWNER_MEMBER_ID = M1.OWNER_MEMBER_ID, M2.IP = M1.IP " + //
                     "  WHEN NOT MATCHED THEN " + //
                     "      INSERT VALUES (M1.ID, M1.GMT_CREATE, M1.GMT_MODIFIED, M1.OWNER_SEQ" + //
                     "        , M1.LAST_LOGIN_TIME, M1.OWNER_MEMBER_ID, M1.IP)";

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

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("MEMBER_LAST_LOGIN_HZ")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("MEMBER_LAST_LOGIN")));

        Assert.assertEquals(13, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("MEMBER_LAST_LOGIN_HZ", "ID"));
    }

}
