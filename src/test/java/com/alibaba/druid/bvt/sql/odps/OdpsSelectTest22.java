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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class OdpsSelectTest22 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "select bucket_id,sum(pv) as pv, sum(clk) as clk,sum(clk)/(sum(pv)+1e-10) as ctr,sum(ut_ad_clk) as ut_ad_clk,sum(ad_clk) as ad_clk,sum(cost) as cost\n" +
                ", sum(cost)*1000/(sum(pv)+1e-10)/100 as rpm\n" +
                "from (\n" +
                "select bucket_id,UT.pv as pv, UT.clk as clk, UT.ad_clk as ut_ad_clk, CLK.clk as ad_clk,CLK.cost as cost\n" +
                "from (\n" +
                "\tselect item_id,pv_id,sum(pv) as pv,sum(clk) as clk, sum(if(str_to_map(args,',','=')['sid'] is not null,clk,0)) as ad_clk\n" +
                "\t,str_to_map(args,',','=')['sid'] as ad_session,split_part(str_to_map(args,',','=')['scm'],'.',2) as bucket_id\n" +
                "\tfrom alimama_algo.fund_mlr_n_chicago_user_track_distinct_shark\n" +
                "\twhere ds=20170627 and hh=00 and mm=00 \n" +
                "\tgroup by item_id,str_to_map(args,',','=')['sid'],pv_id, split_part(str_to_map(args,',','=')['scm'],'.',2)\n" +
                ") UT\n" +
                "left outer join \n" +
                "(\n" +
                "SELECT pv_id AS ad_session\n" +
                "\t, item_it AS item_id\n" +
                "\t, SUM(click_price) AS cost\n" +
                "\t, COUNT(*) AS clk\n" +
                "FROM alimama_algo.fund_mlr_n_chicago_offline_click\n" +
                "WHERE ds = 20170627\n" +
                "\tAND hh = 0\n" +
                "\tAND mm = 0\n" +
                "GROUP BY pv_id, \n" +
                "\titem_it\n" +
                ") CLK on UT.ad_session = CLK.ad_session and UT.item_id = CLK.item_id\n" +
                "where UT.ad_clk > 0 or CLK.clk > 0\n" +
                ") A\n" +
                "group by bucket_id;";//
        assertEquals("SELECT bucket_id, sum(pv) AS pv, sum(clk) AS clk\n" +
                "\t, sum(clk) / (sum(pv) + 1e-10) AS ctr\n" +
                "\t, sum(ut_ad_clk) AS ut_ad_clk, sum(ad_clk) AS ad_clk\n" +
                "\t, sum(cost) AS cost\n" +
                "\t, sum(cost) * 1000 / (sum(pv) + 1e-10) / 100 AS rpm\n" +
                "FROM (\n" +
                "\tSELECT bucket_id, UT.pv AS pv, UT.clk AS clk, UT.ad_clk AS ut_ad_clk, CLK.clk AS ad_clk\n" +
                "\t\t, CLK.cost AS cost\n" +
                "\tFROM (\n" +
                "\t\tSELECT item_id, pv_id, sum(pv) AS pv\n" +
                "\t\t\t, sum(clk) AS clk\n" +
                "\t\t\t, sum(IF(str_to_map(args, ',', '=')['sid'] IS NOT NULL, clk, 0)) AS ad_clk\n" +
                "\t\t\t, str_to_map(args, ',', '=')['sid'] AS ad_session, split_part(str_to_map(args, ',', '=')['scm'], '.', 2) AS bucket_id\n" +
                "\t\tFROM alimama_algo.fund_mlr_n_chicago_user_track_distinct_shark\n" +
                "\t\tWHERE ds = 20170627\n" +
                "\t\t\tAND hh = 0\n" +
                "\t\t\tAND mm = 0\n" +
                "\t\tGROUP BY item_id, \n" +
                "\t\t\tstr_to_map(args, ',', '=')['sid'], \n" +
                "\t\t\tpv_id, \n" +
                "\t\t\tsplit_part(str_to_map(args, ',', '=')['scm'], '.', 2)\n" +
                "\t) UT\n" +
                "\tLEFT OUTER JOIN (\n" +
                "\t\tSELECT pv_id AS ad_session, item_it AS item_id, SUM(click_price) AS cost\n" +
                "\t\t\t, COUNT(*) AS clk\n" +
                "\t\tFROM alimama_algo.fund_mlr_n_chicago_offline_click\n" +
                "\t\tWHERE ds = 20170627\n" +
                "\t\t\tAND hh = 0\n" +
                "\t\t\tAND mm = 0\n" +
                "\t\tGROUP BY pv_id, \n" +
                "\t\t\titem_it\n" +
                "\t) CLK\n" +
                "\tON UT.ad_session = CLK.ad_session\n" +
                "\t\tAND UT.item_id = CLK.item_id\n" +
                "\tWHERE UT.ad_clk > 0\n" +
                "\t\tOR CLK.clk > 0\n" +
                ") A\n" +
                "GROUP BY bucket_id;", SQLUtils.formatOdps(sql));

        assertEquals("select bucket_id, sum(pv) as pv, sum(clk) as clk\n" +
                "\t, sum(clk) / (sum(pv) + 1e-10) as ctr\n" +
                "\t, sum(ut_ad_clk) as ut_ad_clk, sum(ad_clk) as ad_clk\n" +
                "\t, sum(cost) as cost\n" +
                "\t, sum(cost) * 1000 / (sum(pv) + 1e-10) / 100 as rpm\n" +
                "from (\n" +
                "\tselect bucket_id, UT.pv as pv, UT.clk as clk, UT.ad_clk as ut_ad_clk, CLK.clk as ad_clk\n" +
                "\t\t, CLK.cost as cost\n" +
                "\tfrom (\n" +
                "\t\tselect item_id, pv_id, sum(pv) as pv\n" +
                "\t\t\t, sum(clk) as clk\n" +
                "\t\t\t, sum(if(str_to_map(args, ',', '=')['sid'] is not null, clk, 0)) as ad_clk\n" +
                "\t\t\t, str_to_map(args, ',', '=')['sid'] as ad_session, split_part(str_to_map(args, ',', '=')['scm'], '.', 2) as bucket_id\n" +
                "\t\tfrom alimama_algo.fund_mlr_n_chicago_user_track_distinct_shark\n" +
                "\t\twhere ds = 20170627\n" +
                "\t\t\tand hh = 0\n" +
                "\t\t\tand mm = 0\n" +
                "\t\tgroup by item_id, \n" +
                "\t\t\tstr_to_map(args, ',', '=')['sid'], \n" +
                "\t\t\tpv_id, \n" +
                "\t\t\tsplit_part(str_to_map(args, ',', '=')['scm'], '.', 2)\n" +
                "\t) UT\n" +
                "\tleft outer join (\n" +
                "\t\tselect pv_id as ad_session, item_it as item_id, sum(click_price) as cost\n" +
                "\t\t\t, count(*) as clk\n" +
                "\t\tfrom alimama_algo.fund_mlr_n_chicago_offline_click\n" +
                "\t\twhere ds = 20170627\n" +
                "\t\t\tand hh = 0\n" +
                "\t\t\tand mm = 0\n" +
                "\t\tgroup by pv_id, \n" +
                "\t\t\titem_it\n" +
                "\t) CLK\n" +
                "\ton UT.ad_session = CLK.ad_session\n" +
                "\t\tand UT.item_id = CLK.item_id\n" +
                "\twhere UT.ad_clk > 0\n" +
                "\t\tor CLK.clk > 0\n" +
                ") A\n" +
                "group by bucket_id;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
      System.out.println("coditions : " + visitor.getConditions());
      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(15, visitor.getColumns().size());
        assertEquals(6, visitor.getConditions().size());

        System.out.println(SQLUtils.formatOdps(sql));
        
        assertTrue(visitor.containsColumn("alimama_algo.fund_mlr_n_chicago_user_track_distinct_shark", "item_id"));
    }


}
