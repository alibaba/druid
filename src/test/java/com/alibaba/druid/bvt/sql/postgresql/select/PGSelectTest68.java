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

public class PGSelectTest68 extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "                        CASE\n" +
                "                        WHEN POSITION ('市区' IN area_string) > 0 THEN 1\n" +
                "                        WHEN POSITION ('郊区' IN area_string) > 0 THEN 2\n" +
                "                        WHEN POSITION ('无市郊区规划信息' IN area_string) > 0 THEN 9\n" +
                "                        ELSE -1 END\n" +
                "                FROM\n" +
                "                        (\n" +
                "                                SELECT ARRAY_TO_STRING(ARRAY (\n" +
                "                                                SELECT DISTINCT area_info FROM md_mesh WHERE mesh IN (\n" +
                "                                                                        SELECT regexp_split_to_table(?, ',')\n" +
                "                                                        )),',') AS area_string\n" +
                "                        ) t";
        System.out.println(sql);

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT CASE \n" +
                "\t\tWHEN POSITION('市区' IN (area_string)) > 0 THEN 1\n" +
                "\t\tWHEN POSITION('郊区' IN (area_string)) > 0 THEN 2\n" +
                "\t\tWHEN POSITION('无市郊区规划信息' IN (area_string)) > 0 THEN 9\n" +
                "\t\tELSE -1\n" +
                "\tEND\n" +
                "FROM (\n" +
                "\t(SELECT ARRAY_TO_STRING(ARRAY((\n" +
                "\t\t\tSELECT DISTINCT area_info\n" +
                "\t\t\tFROM md_mesh\n" +
                "\t\t\tWHERE mesh IN (\n" +
                "\t\t\t\tSELECT regexp_split_to_table(?, ',')\n" +
                "\t\t\t)\n" +
                "\t\t)), ',') AS area_string)\n" +
                ") t", SQLUtils.toPGString(stmt));
        
        assertEquals("select case \n" +
                "\t\twhen POSITION('市区' in (area_string)) > 0 then 1\n" +
                "\t\twhen POSITION('郊区' in (area_string)) > 0 then 2\n" +
                "\t\twhen POSITION('无市郊区规划信息' in (area_string)) > 0 then 9\n" +
                "\t\telse -1\n" +
                "\tend\n" +
                "from (\n" +
                "\t(select ARRAY_TO_STRING(ARRAY((\n" +
                "\t\t\tselect distinct area_info\n" +
                "\t\t\tfrom md_mesh\n" +
                "\t\t\twhere mesh in (\n" +
                "\t\t\t\tselect regexp_split_to_table(?, ',')\n" +
                "\t\t\t)\n" +
                "\t\t)), ',') as area_string)\n" +
                ") t", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
