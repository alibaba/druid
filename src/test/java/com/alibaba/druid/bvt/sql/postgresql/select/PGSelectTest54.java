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

public class PGSelectTest54 extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "select gid, kind, mapid, poi_id, telephone, admincode, x, y, zipcode, name, address, street, number,geom from public.v_poi order by public.v_poi.geom <-> st_point(?,?) limit 1";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT gid, kind, mapid, poi_id, telephone\n" +
                "\t, admincode, x, y, zipcode, name\n" +
                "\t, address, street, number, geom\n" +
                "FROM public.v_poi\n" +
                "ORDER BY public.v_poi.geom <-> st_point(?, ?)\n" +
                "LIMIT 1", SQLUtils.toPGString(stmt));
        
        assertEquals("select gid, kind, mapid, poi_id, telephone\n" +
                "\t, admincode, x, y, zipcode, name\n" +
                "\t, address, street, number, geom\n" +
                "from public.v_poi\n" +
                "order by public.v_poi.geom <-> st_point(?, ?)\n" +
                "limit 1", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(14, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
