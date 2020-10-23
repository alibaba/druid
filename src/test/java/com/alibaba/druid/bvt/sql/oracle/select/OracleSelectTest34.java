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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest34 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select t.logistics_no, t.event_date, t.country, t.province" + //
                "   , t.city,t.address, t.area_code,t.received_status  " + //
                "from wl_tracking t  " + //
                "where t.logistics_no in ( " + //
                "   select el.logistics_no " + //
                "   from escrow_logistics el" + //
                "   where rownum <= 20" + //
                "       and el.gmt_send between to_date ('2011-9-1', 'yyyy-mm-dd') " + //
                "           and to_date ('2011-11-30 23:59:59','yyyy-mm-dd hh24:mi:ss')" + //
                "       and el.received_status = 'received'" + //
                "       and el.goods_direction = 'send_goods'" + //
                "       and el.country = 'US'" + //
                "       and el.logistics_company in ('Hongkong Post Air Mail','Hongkong Post Air Parcel','China Post Air Mail','China Post Air Parcel')" +
                "       and el.recv_status_desc is null) and t.event_date is not null order by t.logistics_no, t.event_date"; //

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
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("wl_tracking")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("escrow_logistics")));

        Assert.assertEquals(15, visitor.getColumns().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("ESCROW_LOGISTICS", "*")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
