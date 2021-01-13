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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest50 extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "delete from itsm_system_role_menu where menu_id in(\n" +
                "with RECURSIVE menuTemp(menu_id,parent_id)\n" +
                "as (\n" +
                "select menu_id ,parent_id from itsm_system_menu where menu_id in\n" +
                "(\n" +
                "'M00006'\n" +
                ")\n" +
                "union all\n" +
                "select a.menu_id ,a.parent_id from itsm_system_menu as a INNER JOIN menuTemp b on a.parent_id=b.menu_id\n" +
                ")\n" +
                "select menu_id from menuTemp);";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("DELETE FROM itsm_system_role_menu\n" +
                "WHERE menu_id IN (\n" +
                "\t\tWITH RECURSIVE menuTemp (menu_id, parent_id) AS (\n" +
                "\t\t\t\tSELECT menu_id, parent_id\n" +
                "\t\t\t\tFROM itsm_system_menu\n" +
                "\t\t\t\tWHERE menu_id IN ('M00006')\n" +
                "\t\t\t\tUNION ALL\n" +
                "\t\t\t\tSELECT a.menu_id, a.parent_id\n" +
                "\t\t\t\tFROM itsm_system_menu a\n" +
                "\t\t\t\t\tINNER JOIN menuTemp b ON a.parent_id = b.menu_id\n" +
                "\t\t\t)\n" +
                "\t\tSELECT menu_id\n" +
                "\t\tFROM menuTemp\n" +
                "\t);", SQLUtils.toPGString(stmt));
        
        assertEquals("delete from itsm_system_role_menu\n" +
                "where menu_id in (\n" +
                "\t\twith recursive menuTemp (menu_id, parent_id) as (\n" +
                "\t\t\t\tselect menu_id, parent_id\n" +
                "\t\t\t\tfrom itsm_system_menu\n" +
                "\t\t\t\twhere menu_id in ('M00006')\n" +
                "\t\t\t\tunion all\n" +
                "\t\t\t\tselect a.menu_id, a.parent_id\n" +
                "\t\t\t\tfrom itsm_system_menu a\n" +
                "\t\t\t\t\tinner join menuTemp b on a.parent_id = b.menu_id\n" +
                "\t\t\t)\n" +
                "\t\tselect menu_id\n" +
                "\t\tfrom menuTemp\n" +
                "\t);", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(3, visitor.getColumns().size());
        assertEquals(2, visitor.getTables().size());

        assertTrue(visitor.containsColumn("itsm_system_role_menu", "menu_id"));
        assertTrue(visitor.containsColumn("itsm_system_menu", "menu_id"));
        assertTrue(visitor.containsColumn("itsm_system_menu", "parent_id"));
    }
}
