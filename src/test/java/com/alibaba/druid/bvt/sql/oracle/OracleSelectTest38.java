/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest38 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select * from " +
        "(with vw_kreis_statics_t as" +
        "  (select substr(xzqh,1,6) xzqh,swrslx,sum(swrs_count) acd_totle from" +
        "    (select xzqh,sglx,case when (swrs7 <  3) then '1'" +
        "       when (swrs7 <  5) then '2' when (swrs7 <=  9) then '3' else '4' end  swrslx,1 swrs_count" +
        "       from acduser.vw_acd_info where sglx='1' " +
        "       " +
        "                    and sgfssj  >=   ?" +
        "                 " +
        "                 " +
        "        )" +
        "   group by substr(xzqh,1,6),swrslx)" +
        "" +
        "   select e.\"XZQH\",e.\"LESS3\",e.\"F3TO5\",e.\"F5TO9\",e.\"MORE9\",kreis_code, kreis_name,px1,py1,px2,py2 from" +
        		"    ( select" +
        		"     xzqh," +
        		"     nvl(max(decode(swrslx,'1',acd_totle)),0)  less3," +
        		"     nvl(max(decode(swrslx,'2',acd_totle)),0)  f3to5," +
        		"     nvl(max(decode(swrslx,'3',acd_totle)),0)  f5to9," +
        		"     nvl(max(decode(swrslx,'4',acd_totle)),0)  more9" +
        		"     from( select * from acduser.vw_kreis_statics_t) group by xzqh  " +
        		"     ) e" +
        		"" +
        		"  left join" +
        		" acduser.vw_sc_kreis_code_lv2 f on e.xzqh = f.short_kreis_code) " +
        		"   where kreis_code in" +
        		"(select * from " +
        		"  (select tbek_code from acduser.vw_kreis_code start with tbek_code = ? connect by prior tbek_pk=tbek_parent ) " +
        		"where  tbek_code != ?)"; //

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

        Assert.assertEquals(4, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("acduser.vw_acd_info")));

        Assert.assertEquals(15, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "sglx")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
