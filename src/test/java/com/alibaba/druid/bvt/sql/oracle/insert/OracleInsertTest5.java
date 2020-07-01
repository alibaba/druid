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
package com.alibaba.druid.bvt.sql.oracle.insert;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

public class OracleInsertTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "insert into wrh$_seg_stat_obj          ( snap_id          , dbid          , ts#          , obj#          , dataobj#          , owner          , object_name          , subobject_name          , partition_type          , object_type          , tablespace_name)     select :lah_snap_id          , :dbid          , ss1.tsn_kewrseg          , ss1.objn_kewrseg          , ss1.objd_kewrseg          , ss1.ownername_kewrseg          , ss1.objname_kewrseg          , ss1.subobjname_kewrseg          , decode(po.parttype, 1, 'RANGE', 2, 'HASH',                                  3, 'SYSTEM', 4, 'LIST',                                 NULL, 'NONE', 'UNKNOWN')          , decode(ss1.objtype_kewrseg, 0, 'NEXT OBJECT',                   1, 'INDEX', 2, 'TABLE', 3, 'CLUSTER',                         4, 'VIEW', 5, 'SYNONYM', 6, 'SEQUENCE',                       7, 'PROCEDURE', 8, 'FUNCTION', 9, 'PACKAGE',                  11, 'PACKAGE BODY', 12, 'TRIGGER',                            13, 'TYPE', 14, 'TYPE BODY',                                  19, 'TABLE PARTITION',                                        20, 'INDEX PARTITION', 21, 'LOB',                             22, 'LIBRARY', 23, 'DIRECTORY', 24, 'QUEUE',                  28, 'JAVA SOURCE', 29, 'JAVA CLASS',                          30, 'JAVA RESOURCE', 32, 'INDEXTYPE',                         33, 'OPERATOR', 34, 'TABLE SUBPARTITION',                     35, 'INDEX SUBPARTITION',                                     40, 'LOB PARTITION', 41, 'LOB SUBPARTITION',                  42, 'MATERIALIZED VIEW',                    43, 'DIMENSION',                            44, 'CONTEXT', 47, 'RESOURCE PLAN',                   48, 'CONSUMER GROUP',                                 51, 'SUBSCRIPTION', 52, 'LOCATION',                   55, 'XML SCHEMA', 56, 'JAVA DATA',                    57, 'SECURITY PROFILE',                   'UNDEFINED')             , ss1.tsname_kewrseg       from x$kewrattrnew  at,            x$kewrtsegstat ss1,            (select tp.obj#, pob.parttype               from   sys.tabpart$ tp, sys.partobj$ pob               where  tp.bo#   = pob.obj#             union all             select ip.obj#, pob.parttype               from   sys.indpart$ ip, sys.partobj$ pob               where  ip.bo#   = pob.obj#) po      where at.num1_kewrattr  = ss1.objn_kewrseg        and at.num2_kewrattr  = ss1.objd_kewrseg        and at.num1_kewrattr  = po.obj#(+)        and (ss1.objtype_kewrseg not in                         (1  /* INDEX - handled below */,                           10 /* NON-EXISTENT */)             or (ss1.objtype_kewrseg = 1                              and 1 = (select 1 from ind$  i                              where i.obj# = ss1.objn_kewrseg                                      and i.type# in                                                     (1, 2, 3, 4, 6, 7, 9))))         and ss1.objname_kewrseg != '_NEXT_OBJECT'                      and ss1.objname_kewrseg != '_default_auditing_options_'";

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

        Assert.assertEquals(7, visitor.getTables().size());
        Assert.assertEquals(29, visitor.getColumns().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("raises")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
//
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "commission_pct")));
    }

}
