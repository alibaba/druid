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
package com.alibaba.druid.bvt.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SchemaStatVisitorTest {
    @Test
    public void test() {
        String sql = "UPDATE Store_Information\n"
                + "SET shop_money = (\n"
                + "SELECT shop_money\n"
                + "FROM build_info2\n"
                + "WHERE build_info2.id = Store_Information.id)\n"
                + "WHERE Store_Information.user = build_info2.user\n"
                + "AND Store_Information.user = 'test3'";
        DbType dbType = DbType.mysql;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, false);
        List<SQLStatement> statementList = parser.parseStatementList();
        SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        statementList.get(0).accept(schemaStatVisitor);
        // before change:
        // assertEquals(7, schemaStatVisitor.getColumns().size());
        // assertNotNull(schemaStatVisitor.getColumn("UNKNOWN", "user"));
        assertEquals(6, schemaStatVisitor.getColumns().size());
        assertNotNull(schemaStatVisitor.getColumn("Store_Information", "shop_money"));
        assertNotNull(schemaStatVisitor.getColumn("build_info2", "shop_money"));
        assertNotNull(schemaStatVisitor.getColumn("build_info2", "id"));
        assertNotNull(schemaStatVisitor.getColumn("Store_Information", "id"));
        assertNotNull(schemaStatVisitor.getColumn("Store_Information", "user"));
        assertNotNull(schemaStatVisitor.getColumn("build_info2", "user"));
        assertEquals(2, schemaStatVisitor.getTables().size());
        assertTrue(schemaStatVisitor.containsTable("Store_Information"));
        assertTrue(schemaStatVisitor.containsTable("build_info2"));
    }
}
