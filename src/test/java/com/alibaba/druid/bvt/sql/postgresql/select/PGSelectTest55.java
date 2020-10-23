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

public class PGSelectTest55 extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "select b.*,st_astext(p.pos) as pos,st_astext(p.polygon) as polygon " +
                "from ts_biz as b join (" +
                "   select * from ts_polygon" +
                "   where type in (?) and st_intersects(st_transform(ST_GeomFromText(?, 4326),26986),st_transform(polygon,26986))" +
                ") as p " +
                "   on b.objid=p.objid and b.type=p.type and (b.cp_code=p.cp_code or (b.cp_code is null and p.cp_code is null)) " +
                "where b.cp_code=(?) or b.cp_code is null";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT b.*, st_astext(p.pos) AS pos, st_astext(p.polygon) AS polygon\n" +
                "FROM ts_biz b\n" +
                "\tJOIN (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM ts_polygon\n" +
                "\t\tWHERE type IN (?)\n" +
                "\t\t\tAND st_intersects(st_transform(ST_GeomFromText(?, 4326), 26986), st_transform(polygon, 26986))\n" +
                "\t) p\n" +
                "\tON b.objid = p.objid\n" +
                "\t\tAND b.type = p.type\n" +
                "\t\tAND (b.cp_code = p.cp_code\n" +
                "\t\t\tOR (b.cp_code IS NULL\n" +
                "\t\t\t\tAND p.cp_code IS NULL))\n" +
                "WHERE b.cp_code = ?\n" +
                "\tOR b.cp_code IS NULL", SQLUtils.toPGString(stmt));
        
        assertEquals("select b.*, st_astext(p.pos) as pos, st_astext(p.polygon) as polygon\n" +
                "from ts_biz b\n" +
                "\tjoin (\n" +
                "\t\tselect *\n" +
                "\t\tfrom ts_polygon\n" +
                "\t\twhere type in (?)\n" +
                "\t\t\tand st_intersects(st_transform(ST_GeomFromText(?, 4326), 26986), st_transform(polygon, 26986))\n" +
                "\t) p\n" +
                "\ton b.objid = p.objid\n" +
                "\t\tand b.type = p.type\n" +
                "\t\tand (b.cp_code = p.cp_code\n" +
                "\t\t\tor (b.cp_code is null\n" +
                "\t\t\t\tand p.cp_code is null))\n" +
                "where b.cp_code = ?\n" +
                "\tor b.cp_code is null", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(9, visitor.getColumns().size());
        assertEquals(2, visitor.getTables().size());
    }
}
