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

import java.util.List;

public class OdpsSelectTest25 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "INSERT overwrite TABLE ids_openapp_dau_d partition(dt = '${lastday}')\n" +
                "SELECT tt.os,\n" +
                "       tt.ver,\n" +
                "       count(1) AS tt_user,\n" +
                "       sum(tt.tt_cnt) AS tt_cnt,\n" +
                "       count(if(tt.tmp_cnt > 0,1,NULL)) AS tmp_user,\n" +
                "       sum(tt.tmp_cnt) AS tmp_cnt,\n" +
                "       count(if(tt.formal_cnt > 0,1,NULL)) AS formal_user,\n" +
                "       sum(tt.formal_cnt) AS formal_cnt\n" +
                "FROM\n" +
                "  (SELECT t1.uid,\n" +
                "          t2.os,\n" +
                "          t3.ver,\n" +
                "          count(1) AS tt_cnt,\n" +
                "          count(t1.tmp) AS tmp_cnt,\n" +
                "          count(t1.formal) AS formal_cnt\n" +
                "   FROM\n" +
                "     (SELECT uid,\n" +
                "             array(pv,'all') AS os,\n" +
                "             array(v,'all') AS ver,\n" +
                "             if(utype = 0,1,NULL) AS tmp,\n" +
                "             if(utype = 1,1,NULL) AS formal\n" +
                "      FROM openapp_log_d\n" +
                "      WHERE dt = '${lastday}') t1 LATERAL VIEW explode(t1.os) t2 AS os LATERAL VIEW explode(t1.ver) t3 AS ver\n" +
                "   GROUP BY t1.uid,\n" +
                "            t2.os,\n" +
                "            t3.ver) tt\n" +
                "WHERE NOT (tt.os = 'all'\n" +
                "           AND tt.ver <> 'all')\n" +
                "GROUP BY tt.os,\n" +
                "         tt.ver;";//
        assertEquals("INSERT OVERWRITE TABLE ids_openapp_dau_d PARTITION (dt='${lastday}')\n" +
                "SELECT tt.os, tt.ver, COUNT(1) AS tt_user\n" +
                "\t, SUM(tt.tt_cnt) AS tt_cnt\n" +
                "\t, COUNT(IF(tt.tmp_cnt > 0, 1, NULL)) AS tmp_user\n" +
                "\t, SUM(tt.tmp_cnt) AS tmp_cnt\n" +
                "\t, COUNT(IF(tt.formal_cnt > 0, 1, NULL)) AS formal_user\n" +
                "\t, SUM(tt.formal_cnt) AS formal_cnt\n" +
                "FROM (\n" +
                "\tSELECT t1.uid, t2.os, t3.ver, COUNT(1) AS tt_cnt\n" +
                "\t\t, COUNT(t1.tmp) AS tmp_cnt, COUNT(t1.formal) AS formal_cnt\n" +
                "\tFROM (\n" +
                "\t\tSELECT uid, array(pv, 'all') AS os\n" +
                "\t\t\t, array(v, 'all') AS ver\n" +
                "\t\t\t, IF(utype = 0, 1, NULL) AS tmp\n" +
                "\t\t\t, IF(utype = 1, 1, NULL) AS formal\n" +
                "\t\tFROM openapp_log_d\n" +
                "\t\tWHERE dt = '${lastday}'\n" +
                "\t) t1\n" +
                "\t\tLATERAL VIEW EXPLODE(t1.os) t2 AS os\n" +
                "\t\tLATERAL VIEW EXPLODE(t1.ver) t3 AS ver\n" +
                "\tGROUP BY t1.uid, \n" +
                "\t\tt2.os, \n" +
                "\t\tt3.ver\n" +
                ") tt\n" +
                "WHERE NOT (tt.os = 'all'\n" +
                "AND tt.ver <> 'all')\n" +
                "GROUP BY tt.os, \n" +
                "\ttt.ver;", SQLUtils.formatOdps(sql));

        assertEquals("insert overwrite table ids_openapp_dau_d partition (dt='${lastday}')\n" +
                "select tt.os, tt.ver, count(1) as tt_user\n" +
                "\t, sum(tt.tt_cnt) as tt_cnt\n" +
                "\t, count(if(tt.tmp_cnt > 0, 1, null)) as tmp_user\n" +
                "\t, sum(tt.tmp_cnt) as tmp_cnt\n" +
                "\t, count(if(tt.formal_cnt > 0, 1, null)) as formal_user\n" +
                "\t, sum(tt.formal_cnt) as formal_cnt\n" +
                "from (\n" +
                "\tselect t1.uid, t2.os, t3.ver, count(1) as tt_cnt\n" +
                "\t\t, count(t1.tmp) as tmp_cnt, count(t1.formal) as formal_cnt\n" +
                "\tfrom (\n" +
                "\t\tselect uid, array(pv, 'all') as os\n" +
                "\t\t\t, array(v, 'all') as ver\n" +
                "\t\t\t, if(utype = 0, 1, null) as tmp\n" +
                "\t\t\t, if(utype = 1, 1, null) as formal\n" +
                "\t\tfrom openapp_log_d\n" +
                "\t\twhere dt = '${lastday}'\n" +
                "\t) t1\n" +
                "\t\tlateral view explode(t1.os) t2 as os\n" +
                "\t\tlateral view explode(t1.ver) t3 as ver\n" +
                "\tgroup by t1.uid, \n" +
                "\t\tt2.os, \n" +
                "\t\tt3.ver\n" +
                ") tt\n" +
                "where not (tt.os = 'all'\n" +
                "and tt.ver <> 'all')\n" +
                "group by tt.os, \n" +
                "\ttt.ver;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(6, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));
        
        assertTrue(visitor.containsColumn("ids_openapp_dau_d", "dt"));
        assertTrue(visitor.containsColumn("openapp_log_d", "uid"));
        assertTrue(visitor.containsColumn("openapp_log_d", "pv"));
        assertTrue(visitor.containsColumn("openapp_log_d", "v"));
        assertTrue(visitor.containsColumn("openapp_log_d", "utype"));
        assertTrue(visitor.containsColumn("openapp_log_d", "dt"));
    }


}
