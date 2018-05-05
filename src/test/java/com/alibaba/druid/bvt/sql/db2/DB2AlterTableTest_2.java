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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class DB2AlterTableTest_2 extends TestCase {

    public void test_alter_constraint() throws Exception {
        String sql = "alter table audit.SQLHIS alter column  EXPLAINMSG set data type VARCHAR(255)";
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.DB2).get(0);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.DB2);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals("ALTER TABLE audit.SQLHIS\n" +
                "\tALTER COLUMN EXPLAINMSG SET DATA TYPE VARCHAR(255)", stmt.toString());

        assertEquals("alter table audit.SQLHIS\n" +
                "\talter column EXPLAINMSG set data type VARCHAR(255)", stmt.toLowerCaseString());

        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
    }

}
