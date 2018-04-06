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
package com.alibaba.druid.bvt.sql.oracle.select;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest27 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select object_id tab_id_noprint , -9999999 col_id_noprint " + //
                ", 'table '||lower(owner)||' '||object_id||' '||lower(object_name)||' '||y.tid from dba_objects x" + //
                ", search.retl_table_config_search y where object_type='TABLE' and x.owner=upper(case when instr(y.TSCHEMA,'|',1,1)>0 then substr(y.TSCHEMA,1,instr(y.TSCHEMA,'|',1,1)-1) else y.TSCHEMA end) and x.object_name=upper(y.TNAME) and ( owner not in ('SYS','SYSTEM','OUTLN','PUBLIC','WMSYS') or ( owner in ('SYS') and object_name in ('TAB$','OBJ$','COL$','CCOL$','CDEF$') ) ) union all select g.object_id as tab_id_noprint, g.column_id as tab_id_noprint , 'column '||g.column_id||' '||g.data_type||' '||g.data_length ||' '||decode(h.column_name,null ,0 ,1)||' '||lower(g.column_name) from ( select a.object_id ,a.owner, a.object_name as table_name, b.name as column_name ,b.segcol# as column_id, b.type# as data_type, b.segcollength as data_length from dba_objects a ,sys.col$ b ,search.retl_table_config_search g where a.object_id=b.obj# and a.object_type='TABLE' and a.owner=upper(case when instr(g.TSCHEMA,'|',1,1)>0 then substr(g.TSCHEMA,1,instr(g.TSCHEMA,'|',1,1)-1) else g.TSCHEMA end) and a.object_name=upper(g.TNAME) and b.segcol#!=0 and ( owner not in ('SYS','SYSTEM','OUTLN','PUBLIC','WMSYS') or ( owner in ('SYS') and a.object_name in ('TAB$','OBJ$','COL$','CCOL$','CDEF$') ) ) ) g, ( select upper(case when instr(TSCHEMA,'|',1,1)>0 then substr(TSCHEMA,1,instr(TSCHEMA,'|',1,1)-1) else TSCHEMA end) as owner, upper(a.tname) as table_name, upper(b.COLUMN_VALUE) as column_name from search.retl_table_config_search a, table(cast(erosazm.str2varlist(a.pks) as erosazm.vartabletype)) b ) h where g.owner=h.owner(+) and g.table_name=h.table_name(+) and g.column_name=h.column_name(+) order by tab_id_noprint,col_id_noprint";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println(stmt.toString());

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dba_objects")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("search.retl_table_config_search")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sys.col$")));

        Assert.assertEquals(17, visitor.getColumns().size());

         Assert.assertTrue(visitor.containsColumn("search.retl_table_config_search", "tid"));
         Assert.assertTrue(visitor.containsColumn("dba_objects", "owner"));
         Assert.assertTrue(visitor.containsColumn("search.retl_table_config_search", "TSCHEMA"));
    }
}
