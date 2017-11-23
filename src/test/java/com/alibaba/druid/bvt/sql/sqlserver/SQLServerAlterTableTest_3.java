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
package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class SQLServerAlterTableTest_3 extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "ALTER TABLE extensions ADD [core] bit";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.SQL_SERVER);
        Assert.assertEquals("ALTER TABLE extensions" //
                            + "\n\tADD ([core] bit)", output);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
    }

}
